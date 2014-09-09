package common.service.util;

import java.util.Calendar;
import java.util.Date;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.context.i18n.LocaleContextHolder;

import common.Constants;

/**
 * 日時のUtilityクラス.
 */
public final class DateUtil {

    /** ログ出力クラス */
    private static Log log = LogFactory.getLog(DateUtil.class);

    /**
     * プライベート・コンストラクタ.
     * Utilityクラスはインスタンス化禁止.
     */
    private DateUtil() {
    }

    /**
     * 日付フォーマットを取得する.
     *
     * @return 日付フォーマット
     */
    public static String getDatePattern() {
        try {
            return ResourceBundle.getBundle(Constants.BUNDLE_KEY, LocaleContextHolder.getLocale()).getString("date.format");
        } catch (MissingResourceException mse) {
            return "MM/dd/yyyy";
        }
    }

    /**
     * 日時フォーマットを取得する.
     *
     * @return 日時フォーマット
     */
    public static String getDateTimePattern() {
        return getDatePattern() + " HH:mm:ss.S";
    }

    /**
     * 指定された日付の文字列を取得する.
     *
     * @param date
     *            日付
     * @return 日付の文字列
     */
    public static String getDate(Date date) {
        return getDateTime(getDatePattern(), date);
    }

    /**
     * 指定された時間の文字列を取得する.
     *
     * @param time
     *            時間
     * @return 時間の文字列
     */
    public static String getTime(Date time) {
        return getDateTime("HH:mm", time);
    }

    /**
     * 指定されたフォーマットの日付の文字列を取得する.
     *
     * @param mask
     *            フォーマット
     * @param date
     *            日付
     * @return 日付の文字列
     */
    public static String getDateTime(String mask, Date date) {
        if (date != null) {
            return new DateTime(date).toString(mask);
        } else {
            return "";
        }
    }

    /**
     * 指定された文字列を日付型に変換する.
     *
     * @param strDate
     *            日付の文字列
     * @return 日付型
     */
    public static Date convertStringToDate(String strDate) {
        return convertStringToDate(getDatePattern(), strDate);
    }

    /**
     * 指定された文字列を日付型に変換する.
     *
     * @param mask
     *            フォーマット
     * @param strDate
     *            日付の文字列
     * @return 日付型
     */
    public static Date convertStringToDate(String mask, String strDate) {
        if (log.isDebugEnabled()) {
            log.debug("converting '" + strDate + "' to date with mask '" + mask + "'");
        }

        return DateTimeFormat.forPattern(mask).parseDateTime(strDate).toDate();
    }

    /**
     * 今日の日付のカレンダーオブジェクトを取得する.
     *
     * @return カレンダーオブジェクト
     */
    public static Calendar getToday() {
        return new DateTime().toGregorianCalendar();
    }
}
