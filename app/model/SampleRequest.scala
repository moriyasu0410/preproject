package model
import play.mvc.Scope.Params
import validator.SampleRequestValidator
case class SampleRequest(
  val p: String = "",
  val a: Option[String],
  val b: Option[String],
  val c: Option[String]) extends SampleRequestValidator{
}  
object SampleRequest {
  def bind(param: Params): SampleRequest = {
    val b = param.get("b")
    if (b != null) {
      new SampleRequest(
        a = Some(param.get("a")),
        b = Some(param.get("b")),
        p = param.get("p"),
        c = Some(param.get("c")))

    } else {
      new SampleRequest(
        a = Some(param.get("a")),
        b = None,
        p = param.get("p"),
        c = Some(param.get("c")))
      
    }
  }
}