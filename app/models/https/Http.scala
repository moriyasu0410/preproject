package models.https

import daos.https.HttpDao
import scala.annotation.tailrec
import java.util.concurrent.TimeoutException
import play.libs._
import play.libs.WS._

/**
 * Http接続情報
 * @param url URL(必須)
 * @param timeout タイムアウト(3s等)(必須)
 * @param encoding エンコーディング(必須)
 * @param body リクエストボディ(post時のみ使用可, paramsと併用不可)
 * @param params リクエストパラメータ(post時、bodyと併用不可)
 * @param headers リクエストヘッダ
 * @param cookie クッキー
 */
class Http(
  var url: String,
  var timeout: String,
  var encoding: String,
  var body: String = null,
  var params: Map[String, String] = Map.empty[String, String],
  var headers: Map[String, String] = Map.empty[String, String],
  var cookie: Map[String, String] = Map.empty[String, String]) extends HttpDao {

  def toDebug() = {
    "%s timeout[%s] encoding[%s] params[%s] headers[%s] cookie[%s] body[%s]".format(
      url, timeout, encoding, toS(params), toS(headers), toS(cookie), body)
  }

  def toInfo() = {
    def toQueryString(map: Map[String, String]): String = {
      val _map = map.toList.map(t => t._1 + "=" + t._2)
      if (_map.isEmpty) return ""
      _map.reduceLeft((x, y) => x + "&" + y)
    }
    val queryString = toQueryString(params)
    if (queryString == null || queryString.isEmpty()) {
      "%s headers[%s]".format(url, toS(headers))
    } else {
      "%s headers[%s]".format(url + "?" + toQueryString(params), toS(headers))
    }
  }

  private def toS(map: Map[String, String]): String = {
    val _map = map.toList.map(t => t._1 + "=" + t._2)
    if (_map.isEmpty) return ""
    _map.reduceLeft((x, y) => x + "," + y)
  }
}

object Http {

  /**
   * Http共通処理.
   */
  def processHttp(method: String)(function: String => Any) = {
    try {
      function(method)
    } catch {
      case e: Throwable => {
        @tailrec
        def getTimeoutException(throwable: Throwable): Throwable = {
          throwable.getCause() match {
            case timeout: TimeoutException => throw timeout
            case throwable if throwable != null => getTimeoutException(throwable)
            case _ => e
          }
        }
        throw getTimeoutException(e)
      }
    }
  }

  /**
   * async系の処理結果を待つ.
   * タイムアウトした場合、TimeoutExceptionがthrowされる
   * その他エラーが発生した場合も、例外がthrowされる
   * @return 結果オブジェクト
   */
  def wait(promise: F.Promise[HttpResponse]): Response = {
    processHttp("GET") { method =>
      Response(promise.get())
    }.asInstanceOf[Response]
  }
}

case class Response(
  var success: Boolean = false,
  var status: Int,
  var contentType: String,
  var body: String) {

  def toDebug() =
    "success[%s] status[%d] contentType[%s] body[%s]".format(success.toString(), status, contentType, body)
}

object Response {

  /**
   * レスポンスオブジェクトを生成する.
   */
  def apply(response: HttpResponse) = {
    new Response(
      success = response.success(),
      status = response.getStatus().intValue(),
      contentType = response.getContentType(),
      body = response.getString(response.getEncoding()))
  }
}
