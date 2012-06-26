package daos.kvses

import play.libs.URLs
import play.modules.mem.Mem
import _root_.models.kvses._
import collection.JavaConversions._
import collection.mutable._
import play.Logger

/**
 * KVSに通常形式で保存する場合に使用するDaoクラス.
 */
trait PlainKvsDao extends KvsDao {

  /**
   * キーに対応する値を取得する.
   * キーがない場合はNone
   */
  def get(): Option[String] = {
    this.makeKey() match {
      case Some(key) => {
        val encKey = URLs.encodePart(key)
        try {
          Mem.slave.get(encKey)
        } catch {
          case e: IllegalArgumentException => {
            Logger.info(e, "Key:%sのデータの取得に失敗しました。", encKey);
            None
          }
        }
      }
      case None => None
    }
  }

  /** ID */
  var id: String
  /** IDに対する値 */
  var value: Option[String]

  /**
   * KVSのキーとなる文字列(prefix + key)を生成する.
   */
  override def makeKey(): Option[String] = {
    id match {
      case id if id != null && !id.isEmpty() => Some(prefix + id)
      case _ => None
    }
  }

  /**
   * 保存する値を生成する.
   */
  override protected def makeValue = {
    value match {
      case Some(value) => value
      case None => ""
    }
  }
}
