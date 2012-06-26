package utils
import org.apache.commons.lang.time.DateFormatUtils
import java.util.Date

object Dates {

  def getCurrentDateStr(format:String): String = {
    DateFormatUtils.format(new Date(), format);
  }
  def formatCurrentTimeISO8601(): String = {
    var format = DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT
    format.format(new Date())
  }

}