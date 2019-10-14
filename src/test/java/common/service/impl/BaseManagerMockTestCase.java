package common.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import common.webapp.util.ConvertUtil;

@ExtendWith(MockitoExtension.class)
public abstract class BaseManagerMockTestCase {

    protected transient Logger log = LogManager.getLogger(getClass());

    protected ResourceBundle rb;

    public BaseManagerMockTestCase() {
        String className = this.getClass().getName();

        try {
            rb = ResourceBundle.getBundle(className);
        } catch (MissingResourceException e) {
            log.trace("No resource bundle found for:{}", className, e);
        }
    }

    protected Object populate(Object obj) {
        Map<String, String> map = ConvertUtil.convertBundleToMap(rb);

        try {
            BeanUtils.copyProperties(obj, map);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.trace("Copy failed:", e);
        }

        return obj;
    }
}
