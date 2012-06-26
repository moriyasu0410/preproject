package validators.kvses.jobs

import _root_.validators._
import _root_.models.kvses.jobs.JobTime
import _root_.validators.jobs.AbstractJobValidator
import play.data.validation.Validation
import _root_.exceptions.ValidateException

trait JobTimeValidator extends CustomValidator with AbstractJobValidator {
  self: JobTime =>

  override def validate() = {
    required("jobName", jobName) { (name, value) =>
    }
    if (Validation.hasErrors()) {
      var printKey = ""
      if (jobName != null && jobName!=None) {
        printKey = jobName.get
      }
      throw new ValidateException("jobName", printKey)
    }
    this
  }
}