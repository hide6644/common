package common.service.util;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.i18n.LocaleContextHolder;

public class DateUtilTest {

    private Logger log = LogManager.getLogger(getClass());

    public void testGetInternationalDatePattern() {
        LocaleContextHolder.setLocale(new Locale("nl"));
        assertEquals("dd-MMM-yyyy", DateUtil.getDatePattern());

        LocaleContextHolder.setLocale(Locale.FRANCE);
        assertEquals("dd/MM/yyyy", DateUtil.getDatePattern());

        LocaleContextHolder.setLocale(Locale.GERMANY);
        assertEquals("dd.MM.yyyy", DateUtil.getDatePattern());

        // non-existant bundle should default to default locale
        LocaleContextHolder.setLocale(new Locale("fi"));
        String fiPattern = DateUtil.getDatePattern();
        LocaleContextHolder.setLocale(Locale.getDefault());
        String defaultPattern = DateUtil.getDatePattern();

        assertEquals(defaultPattern, fiPattern);
    }

    public void testGetDate() throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("db date to convert: " + new Date());
        }

        String date = DateUtil.getDate(new Date());

        if (log.isDebugEnabled()) {
            log.debug("converted ui date: " + date);
        }

        assertTrue(date != null);
    }

    public void testGetDateTime() {
        if (log.isDebugEnabled()) {
            log.debug("entered 'testGetDateTime' method");
        }
        String now = DateUtil.getTime(new Date());
        assertTrue(now != null);
        log.debug(now);
    }

    public void testGetDateWithNull() {
        final String date = DateUtil.getDate(null);
        assertEquals("", date);
    }

    public void testGetDateTimeWithNull() {
        final String date = DateUtil.getDateTime(null, null);
        assertEquals("", date);
    }

    public void testGetToday() throws ParseException {
        assertNotNull(DateUtil.getToday());
    }
}
