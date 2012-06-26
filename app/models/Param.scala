package models

import play.data.binding.BeanWrapper
import play._
import play.mvc.Scope
import collection.JavaConversions._
import java.lang.reflect.{ Type, ParameterizedType }
import java.lang.annotation.Annotation
import java.lang.reflect.Modifier
import java.lang.reflect.Method
import java.lang.reflect.Field
import _root_.models.annos.Alias
import _root_.models.annos.Aliases
import scala.collection.immutable.TreeMap

/**
 * パラメータ関連Trait.
 */
trait Param[Model] {

  /**
   * フィールド毎に指定された処理(function)を実行する.
   *
   * @param init 初期値
   * @param aliasType 別名のタイプ
   * @param excludes 対象外にしたいフィールド
   * @param function 実行する処理
   * @return 結果
   */
  def processByFields[RESULT](init: RESULT, aliasType: String = null, excludes: Set[String] = Set.empty)(
    function: (Option[RESULT], Option[String], Option[String]) => Option[RESULT])(implicit m: ClassManifest[Model]): Option[RESULT] = {
    def getName(methodOpt: Option[Method], field: Field): Option[String] = {
      var fieldName = methodOpt.map { _.getName }.getOrElse(field.getName)
      if (excludes.contains(fieldName)) {
        return None
      }
      if (aliasType != null) {
        ClassInfo.getAlias[Model](aliasType, fieldName) match {
          case Some(alias) => Some(alias)
          case None => Some(fieldName)
        }
      } else {
        Some(fieldName)
      }
    }
    var result: Option[RESULT] = Some(init)
    ClassInfo.getFields[Model].foreach { field =>
      val methodOpt = ClassInfo.getMethods[Model].get(field.getName)
      val value = methodOpt.map { _.invoke(this) }.getOrElse(field.get(this)) match {
        case Some(value: String) => Some(value)
        case value: String => Some(value)
        case _ => None
      }
      result = function(result, getName(methodOpt, field), value)
    }
    result
  }

  /**
   * Modelの値をMap(変数名,値)に変換する.
   * 現在、変数がString(Option[String])のもののみ対応.
   *
   * @param aliasType 別名のタイプ
   * @param excludes 対象外にしたいフィールド
   */
  def toParameter(aliasType: String = null, excludes: Set[String] = Set.empty)(implicit m: ClassManifest[Model]) = {
    val result = processByFields[TreeMap[String, String]](TreeMap.empty[String, String], aliasType, excludes) { (result, fieldName, fieldValue) =>
      fieldName match {
        case Some(name) => fieldValue match {
          case Some(value) if (value != null && value.length() > 0) => Some(result.get.insert(name, value))
          case _ => result
        }
        case _ => result
      }
    }
    result.getOrElse(TreeMap.empty[String, String])
  }
}

/**
 * BinderTrait.
 */
trait Binder[Model] {
  /**
   * ParameterをModel(case class等)の値にマッピングする。
   */
  def bind(params: Scope.Params, aliasType: String = null)(implicit m: ClassManifest[Model]): Model = {
    BinderInitializer.bind(params, aliasType)
  }
  /**
   * MapをModel(case class等)の値にマッピングする。
   */
  def bindMap(values: scala.collection.mutable.Map[String, Array[String]], aliasType: String = null)(implicit m: ClassManifest[Model]): Model = {
    BinderInitializer.bindMap(values, aliasType)
  }
}

/**
 * Binderの初期化クラス.
 */
object BinderInitializer {

  /**
   * ParameterをModel(case class等)の値にマッピングする。
   */
  def bind[Model](params: Scope.Params, aliasType: String = null)(implicit m: ClassManifest[Model]): Model = {
    bindMap(params.all(), aliasType)
  }

  /**
   * MapをModel(case class等)の値にマッピングする。
   */
  def bindMap[Model](values: java.util.Map[String, Array[String]], aliasType: String = null)(implicit m: ClassManifest[Model]): Model = {
    if (aliasType != null) {
      ClassInfo.getAliasType[Model](aliasType) match {
        case Some(_aliasTypes) => {
          _aliasTypes.foreach(_aliasType => {
            if (values.containsKey(_aliasType._1)) {
              values.put(_aliasType._2, values(_aliasType._1))
            }
          })
        }
        case None =>
      }
    }
    def isEmpty(value:Array[String]):Boolean = {
      if (value == null || value.length == 0 || value(0) == null || value(0).length == 0) return true
      else return false
    }
    def set(instance:Model, field:Field, value:Array[String]) {
      field.setAccessible(true)
      field.getType() match {
        case actualClass if actualClass == classOf[Option[String]] => {
          if (isEmpty(value)) field.set(instance, None)
          else field.set(instance, Some(value(0)))
        }
        case _ => {
          if (isEmpty(value)) field.set(instance, null)
          else field.set(instance, value(0))
        }
      }
    }
    val instance = ClassInfo.newInstance[Model]
    ClassInfo.getFields[Model]().foreach( field => {
      if (values.containsKey(field.getName())) {
        set(instance, field, values.get(field.getName()))
      } else {
        set(instance, field, null)
      }
    })
    instance.asInstanceOf[Model]
  }
}

/**
 * クラス情報管理.
 */
object ClassInfo {
  private var fieldMap = Map.empty[Class[_], Array[Field]]
  private var aliasMap = Map.empty[Class[_], Map[String, Map[String, String]]]
  private var aliasTypeMap = Map.empty[Class[_], Map[String, Map[String, String]]]
  private var methodMap = Map.empty[Class[_], Map[String, Method]]
  def newInstance[Model]()(implicit m: ClassManifest[Model]): Model = {
    val constructor = m.erasure.getDeclaredConstructor()
    constructor.setAccessible(true)
    val instance = constructor.newInstance()
    constructor.setAccessible(false)
    instance.asInstanceOf[Model]
  }
  def getFields[Model]()(implicit m: ClassManifest[Model]): Array[Field] = {
    val clazz = m.erasure
    synchronized {
      fieldMap.get(clazz) match {
        case Some(fields) => return fields
        case None =>
      }
      val feilds = clazz.getDeclaredFields().filterNot { f =>
        (f.getModifiers & Modifier.TRANSIENT) != 0 || f.getName.contains("$")
      }
      fieldMap += clazz -> feilds
      return feilds
    }
  }

  def getField[Model](fieldName: String)(implicit m: ClassManifest[Model]): Option[Field] = {
    getFields[Model]().foreach(field => {
      if (fieldName.equals(field.getName())) {
        return Some(field);
      }
    })
    return None;
  }

  def getAliasType[Model](aliasType: String)(implicit m: ClassManifest[Model]): Option[Map[String, String]] = {
    val clazz = m.erasure
    synchronized {
      aliasTypeMap.get(clazz) match {
        case Some(aliasTypes) => aliasTypes.get(aliasType) match {
          case Some(annosMap) => return Some(annosMap)
          case None =>
        }
        case None =>
      }
      var _annosMap = Map.empty[String, String]
      getFields[Model]().foreach(field => {
        var _aliasTypeMap = Map.empty[String, Map[String, String]]
        field.getDeclaredAnnotations().foreach(anno => {
          anno match {
            case anno: Alias if anno.aliasType().equals(aliasType) => _annosMap += anno.name() -> field.getName()
            case anno: Aliases => {
              anno.value().foreach(anno => {
                if (anno.aliasType().equals(aliasType)) {
                  _annosMap += anno.name() -> field.getName()
                }
              })
            }
            case _ =>
          }
          _aliasTypeMap += aliasType -> _annosMap
        })
        aliasTypeMap += clazz -> _aliasTypeMap
      })
      return Some(_annosMap)
    }
  }

  def getAlias[Model](aliasType: String, fieldName: String)(implicit m: ClassManifest[Model]): Option[String] = {
    val clazz = m.erasure
    val field = getField(fieldName) match {
      case Some(field) => field
      case None => return None
    }
    synchronized {
      val annos = aliasMap.get(clazz) match {
        case Some(fields) => fields.get(fieldName)
        case None => {
          val fields = Map.empty[String, Map[String, String]]
          aliasMap += clazz -> fields
          fields.get(fieldName)
        }
      }
      annos match {
        case Some(annos) => return annos.get(aliasType)
        case None => {
          var _annosMap = Map.empty[String, String]
          field.getDeclaredAnnotations().foreach(anno => {
            anno match {
              case anno: Alias => _annosMap += anno.aliasType() -> anno.name()
              case anno: Aliases => {
                anno.value().foreach(anno => _annosMap += anno.aliasType() -> anno.name())
              }
            }
          })
          aliasMap(clazz).updated(field.getName(), _annosMap)
          return _annosMap.get(aliasType)
        }
      }
    }
  }

  def getMethods[Model]()(implicit m: ClassManifest[Model]): Map[String, Method] = {
    var clazz = m.erasure
    synchronized {
      methodMap.get(clazz) match {
        case Some(methods) => return methods
        case None =>
      }
      val methods = clazz.getDeclaredMethods
        .filter { _.getParameterTypes.isEmpty }.map { m => m.getName -> m }.toMap
      methodMap += clazz -> methods
      return methods
    }
  }

}
