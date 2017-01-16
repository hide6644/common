package common.service.impl;

import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import common.service.util.ConvertUtil;

@RunWith(MockitoJUnitRunner.class)
public abstract class BaseManagerMockTestCase {

    protected transient Logger log = LogManager.getLogger(getClass());

    protected ResourceBundle rb;

    public BaseManagerMockTestCase() {
        String className = this.getClass().getName();

        try {
            rb = ResourceBundle.getBundle(className);
        } catch (MissingResourceException mre) {
            log.trace("No resource bundle found for: " + className);
        }
    }

    protected Object populate(Object obj) throws Exception {
        Map<String, String> map = ConvertUtil.convertBundleToMap(rb);

        BeanUtils.copyProperties(obj, map);

        return obj;
    }
}
