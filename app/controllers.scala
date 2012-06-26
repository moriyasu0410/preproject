package controllers

import play._
import play.mvc._
import play.mvc.Http.Request
import play.mvc.results.Status
import play.mvc.results.Result
import model.SampleRequest

object Application extends Controller with ExceptionHandler{
    
    def index:Result = {
      val req = SampleRequest.bind(Scope.Params.current.get())  
      req.validate
      req.b match {
        case Some(b_)  => {println(b_)}
        case None => {}
      }
      req.p match {
        case "1111" => {Text("OK")}
        case "1112" => Text("OK1")
        case "1113" => Text("OK2")
        case _ =>      Html("""<html><header><title>たいとる</title></header><body>本文</body></html>""")
      }
    }
    
}
trait ExceptionHandler {
  self: Controller =>
  @Catch(Array(classOf[java.lang.Throwable]))
  def handle(e: Throwable) = {
    Logger.error(e, "エラーが発生しました。");
    Error(500,"error")
  }
}
