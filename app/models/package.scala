import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.{Locale, TimeZone}
import javax.inject.Inject

import play.i18n.{Lang, Langs}
import play.api.libs.json._

package object models {

  implicit object timestampFormat extends Format[Timestamp] {
    var formatUTC = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS'Z'")
    formatUTC.setTimeZone(TimeZone.getTimeZone("UTC"));

    var formatLocaleGMT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS'Z'")
    formatLocaleGMT.setTimeZone(TimeZone.getDefault)

    def reads(json: JsValue) = {
      val str = json.as[String]
      JsSuccess(new Timestamp(formatUTC.parse(str).getTime))
    }

    def readsValue(date: String): Timestamp =
      new Timestamp(formatUTC.parse(date).getTime)

    def writes(ts: Timestamp) = JsString(formatLocaleGMT.format(ts))
  }

  implicit val taskJsonFormat = Json.format[Task]
}
