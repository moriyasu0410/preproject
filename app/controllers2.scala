package controllers

import play._
import play.mvc._
import play.mvc.Http.Request
import play.mvc.results.Status
import play.mvc.results.Result
import models.https.SampleHttp

object Application2 extends Controller with ExceptionHandler2{
    
    def index:Result = {
      val req = SampleHttp.bind(Scope.Params.current.get(),Request.current)
      Text(req.get().body)
    }
    
}
trait ExceptionHandler2 {
  self: Controller =>
  @Catch(Array(classOf[java.lang.Throwable]))
  def handle(e: Throwable) = {
    Logger.error(e, "エラーが発生しました。");
    Error(500,"error")
  }
}
