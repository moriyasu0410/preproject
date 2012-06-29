package controllers

import play._
import play.mvc._
import play.mvc.Http.Request
import play.mvc.results.Status
import play.mvc.results.Result
import model.SampleRequest
import daos.files.TestFileDao
import model.SampleKVS

object Application extends Controller with ExceptionHandler{

    def index:Result = {

      // ヘッダー
      val headers = Request.current().headers
      Logger.info("header:"+headers)
      //Logger.info("header:"+headers.get("host"))

      // クッキー
      val cookies = Request.current().cookies
      Logger.info("cookie:"+cookies)

      // リクエストパラメータ
      val req = SampleRequest.bind(Scope.Params.current.get())

      // バリデート
      req.validate

      // レコード挿入
      req.safeSet()

      // レコード取得
      val kvs = SampleKVS.get(req)
      Logger.info(kvs.pk.toString())
      Logger.info(kvs.a.toString())
      Logger.info(kvs.b.toString())
      Logger.info(kvs.c.toString())

      // プロパティ取得（conf/application.confを取得する）
      Logger.info(Play.configuration.getProperty("application.mode"))

      // ファイル取得
      val msg = TestFileDao.readFileAsString("messages")
      Logger.info(TestFileDao.readFileAsString("messages"))

      val a = req.get() match {
        case Some(samplerequest) => samplerequest.toString()
        case _ => "none"
      }
      Logger.info(a)

      req.b match {
        case Some(b_)  => {(b_)}
        case None => {}
      }
      req.pk match {
        case Some("1111") => {Text("OK1111")}
        case Some("1112") => Text("OK1112")
        case Some("1113") => Text("OK1113")
        case Some("2222") => {Text("OK2222")}
        case Some("3333") => Text("OK3333")
        case Some("4444") => Text("OK4444")
        case _ =>      Html("""<html><header><title>たいとる</title></header><body>本文</body></html>""")
      }
    }
}
trait ExceptionHandler {
  self: Controller =>
  @Catch(Array(classOf[java.lang.Throwable]))
  def handle(e: Throwable) = {
    Logger.error(e, "エラーが発生しました。");
    (500, "error")
  }
}
