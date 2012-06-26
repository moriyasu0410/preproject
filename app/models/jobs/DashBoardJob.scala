package models.jobs

import play.jobs.Job
import play.Logger
import java.io.ByteArrayInputStream
import _root_.jobs.JobExceptionHandler
import _root_.validators.jobs.JobValidator
import _root_.models.JsonDeSerializer
import _root_.models.kvses.jobs._
import _root_.utils.Dates

/**
 * DashBoard情報収集用Job
 * @param jobName job名：前回実行時を保持するためのキー
 * @param getData データ取得関数：fileから読み込むか、APIのResponseの結果を使用するかは呼び出すJobにより決まる(読み込み結果をjson形式の文字列で返却する関数であれば良い)
 */
case class DashBoardJob[Model](
  jobName: String,
  getData: () => String) extends JobUpdator[Model] with JobValidator[Model] with JsonDeSerializer[Model] {

  /**
   * 実行日時を保存する（次回実行時の基準時とする）
   * @param currentTime 実行日時
   */
  def setJobTime(currentTime: String) = {
    JobTime(Some(jobName), Some(currentTime)).safeSet()
  }

  /**
   * Jobのメインフロー
   * @param データ出力関数 : 第一引数は事前に出力ファイル名を指定。第二引数はgetDataの戻り値を内部で指定。　戻り値:暗黙型
   */
  def execute()(writeFile: (String) => Unit)(implicit m: Manifest[Model]) = {

    Logger.info(jobName + " start")

    var currentTime = Dates.formatCurrentTimeISO8601()
    var jsonStr = getData()
    var models = deserializeStream(new ByteArrayInputStream(jsonStr.getBytes()))
    multiValidate(models)
    multiUpdate(models)
    writeFile(jsonStr)

    setJobTime(currentTime)

    Logger.info(jobName + " end")

  }
}
object DashBoardJob {
  /**
   * データ取得用にfromパラメータが必要な場合に
   * JobTimeスキーマからJobNameをキーに前回実行日時を取得する
   */
  def getJobFromTime(jobName:String): String = {
    var jobTime = JobTime(Some(jobName)).get() match {
      case Some(jc) if jc.from.isDefined == true => jc;
      case _ => JobTime(Some(jobName), Some("")) //初回基準日時は指定なし
    }
    jobTime.from.get
  }
}