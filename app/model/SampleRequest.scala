package model
import play.mvc.Scope.Params
import _root_.validators.SampleRequestValidator
import _root_.daos.kvses.SampleRequestDao
import models.JsonDeSerializer
import models.JsonSerializer

case class SampleRequest(
  val pk: Option[String] = None,
  val a: Option[String],
  val b: Option[String],
  val c: Option[String]) extends SampleRequestValidator with SampleRequestDao with JsonSerializer{
  def prefix = "sample_request_"
  override def key = pk
}



object SampleRequest extends JsonDeSerializer[SampleRequest]{
  def bind(param: Params): SampleRequest = {
    val b = param.get("b")
    if (b != null) {
      new SampleRequest(
        a = Some(param.get("a")),
        b = Some(param.get("b")),
        pk = Some(param.get("pk")),
        c = Some(param.get("c")))

    } else {
      new SampleRequest(
        a = Some(param.get("a")),
        b = None,
        pk = Some(param.get("pk")),
        c = Some(param.get("c")))

    }
  }
}