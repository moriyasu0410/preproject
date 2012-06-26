package daos.https

import collection.JavaConversions._
import play._
import play.libs._
import play.libs.WS._
import _root_.models.https.Http
import _root_.models.https.Http._
import _root_.models.https.Response
import _root_.exceptions.HttpResponseHandleException
import java.util.concurrent.TimeoutException
import scala.annotation.tailrec
import scala.actors.threadpool.TimeUnit
import org.jboss.netty.handler.codec.http.HttpHeaders

/**
 * Httpでのデータアクセスオブジェクト.
 * このクラスはextendsしたクラスを用意し使用する.
 */
trait HttpDao {

  self: Http =>

  /** リクエストログ */
  private val logger: org.apache.log4j.Logger = org.apache.log4j.Logger.getLogger("requestlog")

  /**
   * Requestオブジェクトを生成する.
   */
  private def toRequest(): WSRequest = {
    val request = WS.withEncoding(this.encoding).url(this.url).timeout(this.timeout)
    request.params(this.params).headers(this.headers)
    request
  }

  /**
   * Requestオブジェクト(POST用)を生成する.
   */
  private def toPostRequest(): WSRequest = {
    val request = toRequest()
    request.body(this.body)
    request
  }

  /**
   * getする.
   * タイムアウトした場合、TimeoutExceptionがthrowされる
   */
  def get(): Response = {
    processHttp("GET") { method =>
      processCommon(method) { method => toRequest.get() }
    }.asInstanceOf[Response]
  }

  /**
   * getする(非同期).
   * 結果を待つ場合は、Http.wait(getAsynの戻り値)をすると結果を待ち合わせできる.
   * TimeoutもHttp.waitの方で取得可能.
   * @param callback 成功時の処理(未指定可)
   */
  def getAsync(callback: (Response, Throwable) => Unit = { (response, throwable) => }): F.Promise[HttpResponse] = {
    processHttp("GET") { method =>
      processCommonAsync(method, callback) { method => toRequest.getAsync() }
    }.asInstanceOf[F.Promise[HttpResponse]]
  }

  /**
   * postする.
   * タイムアウトした場合、TimeoutExceptionがthrowされる
   */
  def post() = {
    processHttp("POST") { method =>
      processCommon(method) { method => toPostRequest().post() }
    }.asInstanceOf[Response]
  }

  /**
   * postする(非同期).
   * 結果を待つ場合は、Http.wait(getAsynの戻り値)をすると結果を待ち合わせできる.
   * TimeoutもHttp.waitの方で取得可能.
   * @param callback 成功時の処理(未指定可)
   */
  def postAsync(callback: (Response, Throwable) => Unit = { (response, throwable) => }): F.Promise[HttpResponse] = {
    processHttp("POST") { method =>
      processCommonAsync(method, callback) { method => toPostRequest.postAsync() }
    }.asInstanceOf[F.Promise[HttpResponse]]
  }

  /**
   * Http共通処理.
   */
  private def processCommon(method: String)(function: String => HttpResponse) = {
    addCommonHeaders()
    if (logger.isDebugEnabled()) logger.debug("[REQ] %s %s".format(method, toDebug()))
    var start: Long = 0
    if (logger.isInfoEnabled()) start = System.nanoTime()
    try {
      val response = Response(function(method))
      handleResponse(start, method, response) match {
        case Some(e: HttpResponseHandleException) => throw e
        case _ =>
      }
      response
    } catch {
      case e: HttpResponseHandleException => throw e
      case e: Throwable => {
        if (logger.isInfoEnabled()) logger.info("%s %s %s %s [%s]".format(method, toInfo(), "-", "-", "-"))
        throw e
      }
    }
  }

  /**
   * Http共通処理.
   */
  private def processCommonAsync(method: String, callback: (Response, Throwable) => Unit = { (response, throwable) => })(function: String => F.Promise[HttpResponse]) = {
    addCommonHeaders()
    if (logger.isDebugEnabled()) logger.debug("[REQ] %s %s".format(method, toDebug()))
    var start: Long = 0
    if (logger.isInfoEnabled()) start = System.nanoTime()
    val promise = function(method)
    promise.onRedeem(new F.Action[F.Promise[HttpResponse]]() {
      override def invoke(promise: F.Promise[HttpResponse]) = {
        val response = Response(promise.get)
        handleResponse(start, method, response) match {
          case Some(e: HttpResponseHandleException) => callback(response, e)
          case _ => callback(response, null)
        }
      }
    })
    promise
  }

  private def handleResponse(start: Long, method: String, response: Response):Option[HttpResponseHandleException] = {
    if (logger.isInfoEnabled()) {
      logger.info("%s %s %s %s [%s]".format(method, toInfo(), response.status,
        TimeUnit.MICROSECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS), responseBodySize(response)))
    }
    if (logger.isDebugEnabled()) logger.debug("[RES] %s %s %s".format(method, url, response.toDebug()))
    if (mvc.Http.StatusCode.OK != response.status) {
      Logger.info("""Http接続時に200以外のステータスが返却されました。access:"%s", status:"%s", contentType:"%s", body:"%s"""",
        toInfo(), response.status.toString(), response.contentType, response.body)
      return Some(new HttpResponseHandleException(response))
    }
    return None
  }

  private def addCommonHeaders() = {
    if (this.cookie.size > 0) {
      val cookie = this.cookie.toList.map(t => t._1 + "=" + t._2).reduceLeft((x, y) => x + "; " + y)
      this.headers += ("Cookie" -> cookie)
    }
    this.headers += (HttpHeaders.Names.ACCEPT_ENCODING -> HttpHeaders.Values.GZIP)
  }

  private def responseBodySize(response: Response) = {
    var responseBodySize = -1
    if (response.body != null) {
      if (this.encoding != null) {
        responseBodySize = response.body.getBytes(this.encoding).length
      } else {
        responseBodySize = response.body.getBytes().length
      }
    }
    responseBodySize
  }
}
