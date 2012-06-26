package daos.kvses

import play.libs.URLs
import play.modules.mem.Mem
import _root_.models.kvses._
import collection.JavaConversions._
import collection.mutable._

/**
 * KVSDao既定クラス.
 */
trait KvsDao {

  /**
   * キーに対応する値を設定する(非同期).
   * @param expiration 有効期限(秒)
   */
  def set(expiration: Int = Int.MaxValue) = {
    processModify() { key =>
      Mem.master.set(URLs.encodePart(key), makeValue, expiration)
    }
  }

  /**
   * キーに対応する値を設定する(同期).
   * @param expiration 有効期限(秒)
   */
  def safeSet(expiration: Int = Int.MaxValue) = {
    processModify() { key =>
      Mem.master.safeSet(URLs.encodePart(key), makeValue, expiration)
    }
  }

  /**
   * キーに対応する値を削除する(非同期).
   */
  def delete() = {
    processModify() { key =>
      Mem.master.del(URLs.encodePart(key))
    }
  }

  /**
   * キーに対応する値を削除する(同期).
   */
  def safeDelete() = {
    processModify() { key =>
      Mem.master.safeDel(URLs.encodePart(key))
    }
  }

  /**
   * 更新系の共通処理.
   * @throws IllegalArgumentException キーがない場合
   */
  private def processModify()(function: String => Any) = {
    this.makeKey() match {
      case Some(key) => function(key)
      case None => throw new IllegalArgumentException("Unspecified key.")
    }
  }

  /**
   * キー一覧を取得する.
   * @param preWord 接頭語(各Modelのprefixに続く文字を指定する)
   * @param max 最大取得件数
   */
  def keys(preWord:String = "", max:Int = -1) = {
    KvsRpcDao.matchPrefix(prefix + preWord, max)
  }

  /** KVSのキー名の接頭語. */
  def prefix: String

  /**
   * KVSのキーとなる文字列を生成する.
   */
  def makeKey(): Option[String]
  /**
   * 保存する値を取得する.
   */
  protected def makeValue(): String
}
