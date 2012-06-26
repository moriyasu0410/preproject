package models.kvses.jobs

import daos.kvses.JsonKvsDao
//import models.kvses.Ad
import utils.Checks

/**
 * Job更新系親CaseClass
 * JobUpdatorを使用するときに、CaseClass側に指定する
 * <ul>
 * <li>削除判定項目(deletedJudgedColumn)を定義（JobUpdatorで使用）
 * </ul>
 */
trait JobCase[Model] extends JsonKvsDao[Model] {
  def deletedJudgedColumn: Option[String] = { throw new UnsupportedOperationException("CaseClassでdeletedJudgedColumnを指定してください") }
}

trait JobUpdator[Model] {

  /**
   * 引数で渡されたListに対して、以下の処理を行う。
   * <ul>
   * <li>削除判定対象項目に値が設定されているものをsafeDelete
   * <li>削除判定対象項目に値が設定されていないものをsafeSet
   * </ul>
   * @param models 反映対象Modelリスト
   */
  def multiUpdate(models: List[Model])(implicit m: Manifest[Model]) = {

    //更新対象Modelリストから削除対象項目に値が設定されているものを抽出し、KTに反映（削除対象：削除対象項目値あり）
    var deleteList = models.filter {
      case (p) =>
        var deletedAtValue = p.asInstanceOf[JobCase[Model]].deletedJudgedColumn;
        deletedAtValue.isDefined && !deletedAtValue.get.isEmpty
    }
    deleteList.foreach {
      (deleteModel: Model) =>
        {
          var model = deleteModel.asInstanceOf[JsonKvsDao[Model]]
          play.Logger.debug("deleteModel:" + model.toString())
          model.get() match {
            case Some(ad) => model.safeDelete()
            case None => None
          }
        }
    }

    //更新対象Modelリストから更新対象を抽出し、KTに反映（更新対象：削除日付なし）
    var setModels = models.filter {
      case (p) =>
        var deletedAtValue = p.asInstanceOf[JobCase[Model]].deletedJudgedColumn;
        deletedAtValue.isEmpty || deletedAtValue.get.isEmpty
    }
    setModels.foreach {
      (setModel: Model) =>
        {
          var model = setModel.asInstanceOf[JsonKvsDao[Model]]
          model = setBigBanner(model)
          play.Logger.debug("setModel:" + model.toString());
          model.safeSet()
        }
    }
  }
  /**
   * 現在該当のauidのデータが存在し、BigBanner設定がある場合、更新データにその値を引き継ぐ
   * ※AdでBigBanner項目が存在する場合のみ値を引きつぐ
   * @param 更新処理対象のModel
   * @return 更新処理対象のModelにBigbanner設定を施したもの。
   */
  def setBigBanner(setModel: JsonKvsDao[Model]): JsonKvsDao[Model] = {
    setModel
//    if (!setModel.isInstanceOf[Ad]) {
//      return setModel //Adでない場合
//    }
//    var updateAd = setModel.asInstanceOf[Ad]
//    Ad(auid = updateAd.auid).get match {
//      case Some(ad) => {
//        ad.bigBanner match {
//          case Some(bigBannerValue) => {//BigBanner項目設定済みの場合
//            updateAd.bigBanner = Some(bigBannerValue)
//          }
//          case None => {}//BigBanner項目未設定の場合
//        }
//      }
//      case None => {}//新規登録の場合
//    }
//    return updateAd.asInstanceOf[JsonKvsDao[Model]]
  }
}