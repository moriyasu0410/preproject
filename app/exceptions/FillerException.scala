package exceptions

import play.data.validation.Error
import play.data.validation.Validation
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer

/**
 * 外部ADNWFiller例外<br/>
 * ValidateExceptionと同等の性能を持つ。
 * 
 */
class FillerException() extends ValidateException {

  /** message */
  def this(mes: String) {
    this()
    message = mes
  }

  def this(keyName: String, keyValue: String) {
    this()
    name = keyName
    value = keyValue
  }

  def this(childs: ListBuffer[ValidateException]) {
    this()
    this.childs = childs
  }
}