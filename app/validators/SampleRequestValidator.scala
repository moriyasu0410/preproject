package validators
import model.SampleRequest
trait SampleRequestValidator {
  // SampleRequestを継承している扱いとなる
  self : SampleRequest =>

    def validate():SampleRequest={
    	if (pk ==""){
    		throw new Exception
    	}
    	if (a ==""){
    		throw new Exception
    	}
    	if (b == None) {
    		throw new Exception
    	}
    	this
    }
}
