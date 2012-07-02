package models.https

import play._
import play.mvc._
import play.mvc.Http.Request
import play.mvc.results.Status
import play.mvc.results.Result
import _root_.models.Binder
import _root_.daos.https.SampleHttpDao

case class SampleHttp(
  a: Option[String]) extends Http(
  url = Play.configuration.getProperty("sample.url"),
  timeout = Play.configuration.getProperty("sample.timeout"),
  encoding = Play.configuration.getProperty("sample.encoding"))
  with SampleHttpDao {
}
object SampleHttp {
  def bind(p:Scope.Params,r:Request) = {
    val sampleHttp = new SampleHttp(Some(p.get("a")))
    sampleHttp.headers += "User-Agent"      -> r.headers.get("user-agent").value()
    sampleHttp.headers += "Accept-Language" -> r.headers.get("accept-language").value()
    sampleHttp.headers += "Accept"          -> r.headers.get("accept").value()
    sampleHttp.headers += "Accept-Charset"  -> r.headers.get("accept-charset").value()
//    sampleHttp.headers += "Referer"         -> r.headers.get("referer").value()
    sampleHttp
  }
}