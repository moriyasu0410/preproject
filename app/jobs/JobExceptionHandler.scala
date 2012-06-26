package jobs
import play.jobs.Job
import play.mvc.Catch
import play.Logger
import _root_.exceptions.ValidateException

/**
 * Job処理中に例外が発生した場合、
 * 当処理にて捕捉しログ出力とエラーレスポンスの構築・返却を行う。
 */
trait JobExceptionHandler {
  self: Job[Any] =>
  @Catch(Array(classOf[ValidateException]))
  def handle(ve: ValidateException) = {
    Logger.error(ve, "入力チェックエラーが発生しました。ログをご確認ください。");
  }

  @Catch(Array(classOf[java.lang.Throwable]))
  def handle(e: Throwable) { Logger.error(e, "エラーが発生しました。") }
}
