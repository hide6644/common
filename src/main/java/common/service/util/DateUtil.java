package common.service.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.i18n.LocaleContextHolder;

import common.Constants;

public final class DateUtil {

    private static Log log = LogFactory.getLog(DateUtil.class);

    private static final String TIME_PATTERN = "HH:mm";

    private DateUtil() {
    }

    public static String getDatePattern() {
        Locale locale = LocaleContextHolder.getLocale();
        String defaultDatePattern;
        try {
            defaultDatePattern = ResourceBundle.getBundle(Constants.BUNDLE_KEY, locale).getString("date.format");
        } catch (MissingResourceException mse) {
            defaultDatePattern = "MM/dd/yyyy";
        }

        return defaultDatePattern;
    }

    public static String getDateTimePattern() {
        return DateUtil.getDatePattern() + " HH:mm:ss.S";
    }

    public static String getDate(Date aDate) {
        SimpleDateFormat df;
        String returnValue = "";

        if (aDate != null) {
            df = new SimpleDateFormat(getDatePattern());
            returnValue = df.format(aDate);
        }

        return (returnValue);
    }

    public static Date convertStringToDate(String aMask, String strDate) throws ParseException {
        SimpleDateFormat df;
        Date date;
        df = new SimpleDateFormat(aMask);

        if (log.isDebugEnabled()) {
            log.debug("converting '" + strDate + "' to date with mask '" + aMask + "'");
        }

        try {
            date = df.parse(strDate);
        } catch (ParseException pe) {
            //log.error("ParseException: " + pe);
            throw new ParseException(pe.getMessage(), pe.getErrorOffset());
        }

        return (date);
    }

    public static String getTimeNow(Date theTime) {
        return getDateTime(TIME_PATTERN, theTime);
    }

    public static Calendar getToday() throws ParseException {
        Date today = new Date();
        SimpleDateFormat df = new SimpleDateFormat(getDatePattern());

        // This seems like quite a hack (date -> string -> date),
        // but it works ;-)
        String todayAsString = df.format(today);
        Calendar cal = new GregorianCalendar();
        cal.setTime(convertStringToDate(todayAsString));

        return cal;
    }

    public static String getDateTime(String aMask, Date aDate) {
        SimpleDateFormat df = null;
        String returnValue = "";

        if (aDate == null) {
            log.warn("aDate is null!");
        } else {
            df = new SimpleDateFormat(aMask);
            returnValue = df.format(aDate);
        }

        return (returnValue);
    }

    public static String convertDateToString(Date aDate) {
        return getDateTime(getDatePattern(), aDate);
    }

    public static Date convertStringToDate(final String strDate) throws ParseException {
        return convertStringToDate(getDatePattern(), strDate);
    }
}
