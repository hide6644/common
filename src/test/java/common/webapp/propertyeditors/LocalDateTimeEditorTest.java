package common.webapp.propertyeditors;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;

class LocalDateTimeEditorTest {

    @Test
    void testGetAsText() throws Exception {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        LocalDateTimeEditor editor = new LocalDateTimeEditor(dateTimeFormatter, true);
        LocalDateTime now = LocalDateTime.now();
        editor.setValue(now);

        assertEquals(now.format(dateTimeFormatter), editor.getAsText());
    }

    @Test
    void testSetAsTextAllowEmpty() throws Exception {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        LocalDateTimeEditor editor = new LocalDateTimeEditor(dateTimeFormatter, true);
        LocalDateTime now = LocalDateTime.now();
        editor.setAsText(null);

        assertEquals("", editor.getAsText());

        editor.setAsText(now.format(dateTimeFormatter));

        assertEquals(now.format(dateTimeFormatter), ((LocalDateTime) editor.getValue()).format(dateTimeFormatter));
    }

    @Test
    void testGetAsTextNull() throws Exception {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        LocalDateTimeEditor editor = new LocalDateTimeEditor(dateTimeFormatter, true);

        assertEquals("", editor.getAsText());
    }

    @Test
    void testSetAsTextInvalidDate() throws Exception {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        LocalDateTimeEditor editor = new LocalDateTimeEditor(dateTimeFormatter, false);

        try {
            editor.setAsText("Invalid Date");
            fail("IllegalArgumentException not thrown");
        } catch (IllegalArgumentException expected) {
            assertNotNull(expected);
        }
    }
}
