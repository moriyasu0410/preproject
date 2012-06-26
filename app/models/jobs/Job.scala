package models.jobs

import play.Logger
import _root_.models.kvses.jobs.JobTime
import _root_.utils.Dates

/**
 * <p>
 * ジョブのためのtrait。<br/>
 * </p>
 * @author keisuke sekiguchi
 */
trait Job {
  /**ジョブの名前*/
  protected lazy val jobName: String = ""

  /**
   * ジョブを処理する関数。<br/>
   * <br/>
   * @param partialFuncion 実際のジョブ処理を実行する部分関数
   */
  def execute(partialFuncion: => Unit) = {
    Logger.info(jobName + " start")
    //ジョブの実際の処理を実行
    partialFuncion
    //ジョブの終了時間をに保存
    setJobTime(Dates.formatCurrentTimeISO8601())
    Logger.info(jobName + " end")
  }

  /**
   * ジョブの実行日時をKTに保存する（次回実行時の基準時とする）関数。<br/>
   * <br/>
   * @param currentTime 実行日時
   */
  private def setJobTime(currentTime: String) = {
    JobTime(Some(jobName), Some(currentTime)).safeSet()
  }
}
