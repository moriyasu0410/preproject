package exceptions

import play.data.validation.Error
import play.data.validation.Validation
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer

/**
 * 外部ADNWパースエラー例外<br/>
 * ValidateExceptionと同等の性能を持つ。
 * 
 */
class ParseException() extends ValidateException {

  /** 入力チェックエラーメッセージ */
//  var errsMap: java.util.Map[java.lang.String, java.util.List[Error]] = Validation.current().errorsMap()

  /** 1件ずつのエラー情報 */
//  var childs: ListBuffer[ValidateException] = ListBuffer[ValidateException]()

  /** keyvalue */
//  var value: String = ""

  /** keyName */
//  var name: String = ""

   /** エラーメッセージ */
//  var message: String = ""

  /** message */
  def this(mes: String) {
    this()
    message = mes
  }

  def this(keyName: String, keyValue: String) {
    this()
    name = keyName
    value = keyValue
  }

  def this(childs: ListBuffer[ValidateException]) {
    this()
    this.childs = childs
  }

//  def getDisplayMessage(tagName:String):String = {
//    def tag(tagName:String, message:String, keyInfo:String) = {
//      "<%s>%s (%s)</%s>".format(tagName, message, keyInfo, tagName)
//    }
//    var ret = new StringBuilder
//    var keyInfo = ""
//    if (name != null && name.length() > 0) {
//      keyInfo = "%s=%s".format(this.name, this.value)
//    } 
//    if (message != null && message.length() > 0) {
//      tag(tagName, message, keyInfo).addString(ret)
//    } else if (childs.size() > 0) {
//      var messages = new StringBuilder
//      childs.foreach( child => {
//        child.getDisplayMessage(tagName).addString(messages)
//      })
//      messages.toString().addString(ret)
//    } else if (errsMap != null && errsMap.size() > 0) {
//      var messages = new StringBuilder
//      errsMap.foreach( error => {
//        error._2.foreach( error => {
//          tag(tagName, error.message(), keyInfo).addString(messages)
//        })
//      })
//      messages.toString().addString(ret)
//    } else {
//      tag(tagName, super.getMessage(), keyInfo).addString(ret)
//    }
//    ret.toString
//  }
}