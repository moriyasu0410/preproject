package daos.kvses

import play.libs.URLs
import play.modules.mem.Mem
import models._
import com.codahale.jerkson.Json._
import play.Logger

/**
 * KVSにJson形式で保存する場合に使用するDaoクラス.
 */
trait JsonKvsDao[T] extends KvsDao with JsonSerializer {

  /**
   * キーに対応する値を取得する.
   * キーがない場合はNone
   */
  def get()(implicit mf: Manifest[T]): Option[T] = {
    this.makeKey() match {
      case Some(key) => get(key)
      case None => None
    }
  }

  /**
   * キーに対応する値を取得する(指定されたkeyをそのまま使用し取得する).
   * キーがない場合はNone
   * @param key 取得するKTのキー
   */
  def get(key: String)(implicit mf: Manifest[T]): Option[T] = {
    val encKey = URLs.encodePart(key);
    try {
      Mem.slave.get(encKey) match {
        case Some(json) => Some(parse[T](json))
        case None => None
      }
    } catch {
      case e: IllegalArgumentException => {
        Logger.info(e, "Key:%sのデータの取得に失敗しました。", encKey);
        None
      }
    }
  }

  /** Keyに使用する値. */
  def key: Option[String] = None

  /**
   * KVSのキーとなる文字列(prefix + key)を生成する.
   */
  override def makeKey(): Option[String] = {
    key match {
      case Some(id) => Some(prefix + id)
      case _ => None
    }
  }

  /**
   * 保存する値を生成する.
   */
  override protected def makeValue = {
    this.serialize()
  }

}

