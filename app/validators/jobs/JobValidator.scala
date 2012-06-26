package validators.jobs

import play._
import play.data.validation.Validation
import _root_.exceptions.ValidateException
import _root_.models.kvses.jobs.JobCase
import scala.collection.mutable.ListBuffer

/**
 * Jobの入力チェックで使用するValidator
 */
trait AbstractJobValidator {
  /**
   * Jobでの一括実行用に、定義のみ行う（実装は各Validator）
   */
  def validate() = {}
}

/**
 * Modelのリストに入力チェックを行う。
 */
trait JobValidator[Model] {

  /**
   * 引数に対して入力チェックを行う。
   * 全件の入力チェックが終わった際に、エラーが1件でもあれば入力チェックエラーを発生。
   * @param models : 入力チェック対象
   * @throws ValidateException 入力チェックエラーが発生した場合
   */
  def multiValidate(models: List[Model])(implicit m: Manifest[Model]) = {
    var exceptionList = ListBuffer[ValidateException]()
    for (model <- models) {
      try {
        model.asInstanceOf[AbstractJobValidator].validate()
      } catch {
        case ve: ValidateException => {
          exceptionList += ve;
          Logger.warn("入力チェックエラーが発生しました。" + ve.name + ":" + ve.value + " " + ve.errsMap.toString())
          Validation.clear()
        }
        case e => { throw e }
      }
    }
    if(exceptionList.length !=0){
      throw new ValidateException(exceptionList)
    }
  }
}
