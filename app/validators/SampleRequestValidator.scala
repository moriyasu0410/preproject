package validator
import model.SampleRequest
trait SampleRequestValidator {
  self : SampleRequest =>
  def validate():SampleRequest={
    if (p ==""){
     throw new Exception 
    }
    if (b == None) {
     throw new Exception 
    }
    this
  }
}
