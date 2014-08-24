package common.service.util;

import java.text.DecimalFormat;
import java.text.ParseException;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CurrencyConverter implements Converter {

    private final Log log = LogFactory.getLog(CurrencyConverter.class);

    private DecimalFormat formatter = new DecimalFormat("###,###.00");

    public void setDecimalFormatter(DecimalFormat df) {
        this.formatter = df;
    }

    public final Object convert(@SuppressWarnings("rawtypes") Class type, Object value) {
        // for a null value, return null
        if (value == null) {
            return null;
        } else {
            if (value instanceof String) {
                if (log.isDebugEnabled()) {
                    log.debug("value (" + value + ") instance of String");
                }

                try {
                    if (StringUtils.isBlank(String.valueOf(value))) {
                        return null;
                    }

                    if (log.isDebugEnabled()) {
                        log.debug("converting '" + value + "' to a decimal");
                    }

                    //formatter.setDecimalSeparatorAlwaysShown(true);
                    Number num = formatter.parse(String.valueOf(value));

                    return num.doubleValue();
                } catch (ParseException pe) {
                    pe.printStackTrace();
                }
            } else if (value instanceof Double) {
                if (log.isDebugEnabled()) {
                    log.debug("value (" + value + ") instance of Double");
                    log.debug("returning double: " + formatter.format(value));
                }

                return formatter.format(value);
            }
        }

        throw new ConversionException("Could not convert " + value + " to " + type.getName() + "!");
    }
}
