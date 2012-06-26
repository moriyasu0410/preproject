package validators

import play.data.validation.Validation
import play.mvc.Http.Cookie

/**
 * 入力チェックに関わる共通クラス.
 * 以下の機能を提供
 * <ul>
 * <li>パラメータ用必須・任意チェック
 * <li>Cookie用必須・任意チェック
 * </ul>
 */
trait CustomValidator {

  /**
   * 必須項目入力チェック
   * 引数のパラメータ値に値が設定されている場合、入力チェック関数を実行
   * 引数のパラメータ値に値が設定されていない場合、必須チェックNGとする
   * @param name パラメータ名
   * @param value パラメータ値
   * @param func:(String,String) 入力チェック関数
   */
  def required(name: String, value: Option[String])(func: (String, String) => Unit): Unit = {
    if (value == null) {
      Validation.required(name, null) //必須エラーを発生させるために、第二引数にnullを設定する
    }

    value match {
      case Some(a) => {
        if (a != null
          && !a.isEmpty()) {
          func(name, a)
        } else {
          Validation.required(name, null) //必須エラーを発生させるために、第二引数にnullを設定する
        }
      }
      case _ => {
        Validation.required(name, null) //必須エラーを発生させるために、第二引数にnullを設定する
      }
    }
  }

  /**
   * 必須項目入力チェック
   * 引数のパラメータ値に値が設定されている場合、入力チェック関数を実行
   * 引数のパラメータ値に値が設定されていない場合、必須チェックNGとする
   * @param name パラメータ名
   * @param value パラメータ値
   * @param func:(String,String) 入力チェック関数
   */
  def requiredForCookie(name: String, value: Option[Cookie])(func: (String, String) => Unit): Unit = {
    if (value == null) {
      Validation.required(name, null) //必須エラーを発生させるために、第二引数にnullを設定する
    }

    value match {
      case Some(a) => {
        if (a != null
          && !a.value.isEmpty()) {
          func(name, a.value)
        } else {
          Validation.required(name, null)
        }
      }
      case _ => Validation.required(name, null) //必須エラーを発生させるために、第二引数にnullを設定する
    }
  }

  /**
   * 任意項目入力チェック
   * 引数のパラメータ値に値が設定されている場合、入力チェック関数を実行
   * 引数のパラメータ値に値が設定されていない場合、何もしない
   * @param name パラメータ名
   * @param value パラメータ値
   * @param func:(String,String) 入力チェック関数
   */
  def option(name: String, value: Option[String])(func: (String, String) => Unit): Unit = {
    if (value == null) {
      return
    }

    value match {
      case Some(a) => {
        if (a != null
          && !a.isEmpty()) {
          func(name, a)
        }
      }
      case _ =>
    }
  }

  /**
   * 任意項目入力チェック
   * 引数のパラメータ値に値が設定されている場合、入力チェック関数を実行
   * 引数のパラメータ値に値が設定されていない場合、何もしない
   * @param name パラメータ名
   * @param value パラメータ値
   * @param func:(String,String) 入力チェック関数
   */
  def optionForCookie(name: String, value: Option[Cookie])(func: (String, String) => Unit): Unit = {
    if (value == null) {
      return
    }

    value match {
      case Some(a) => {
        if (a != null
          && !a.value.isEmpty()) {
          func(name, a.value)
        }
      }
      case _ =>
    }
  }

  /**
   * URLチェック関数
   * valueで指定されるURLが
   * @param	name パラメータ名
   * @param value パラメータ値
   */
  def matchURL(name:String, value:String):Unit = {
	 Validation.`match`(name, value, "^(http|https|ftp)\\://[a-zA-Z0-9\\-\\.]+(:[a-zA-Z0-9]*)?/?([a-zA-Z0-9\\-\\._\\?\\,\\'/\\\\\\+&amp;%\\$#\\=~\\!])*$")
  }

  /**
   * URLチェック関数(エンコードされていない)
   * valueで指定されるURLがエンコードされていない場合でもurlとして適当かチェックする
   * @param	name パラメータ名
   * @param value パラメータ値
   */
  def matchNotEncodedURL(name:String, value:String):Unit = {
    Validation.`match`(name, value, """^(http|https|ftp)\://[a-zA-Z0-9\-\.]+(:[a-zA-Z0-9]*)?/?(.*)$""")
  }

}