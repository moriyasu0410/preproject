package models.kvses.jobs

import java.util.Date
import org.apache.commons.lang.time.DateFormatUtils
import play.mvc.Scope
import _root_.daos.kvses.jobs.JobTimeDao
import _root_.models.Param
import _root_.models.Binder
import _root_.validators.kvses.jobs.JobTimeValidator

/**
 * バッチ起動日時制御
 * @param jobName Job名称
 * @param from 前回起動時間
 */
case class JobTime(
  var jobName: Option[String] = None,
  var from: Option[String] = None)
  extends JobTimeValidator with JobTimeDao with Param[JobTime]  with JobCase[JobTime]{
  override def prefix = "job_date_"
  override def key = jobName
  override def deletedJudgedColumn = None
}

object JobTime extends Binder[JobTime] {
  def bind(params: Scope.Params): JobTime = {
    super[Binder].bind(params)
  }
}