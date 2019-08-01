package common.service;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import common.webapp.util.ConvertUtil;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = { "classpath:/common/service/applicationContext-resources.xml",
        "classpath:/common/dao/applicationContext-dao.xml", "classpath:/applicationContext-service.xml",
        "classpath*:/**/applicationContext.xml"
})
@Transactional
@Rollback
public abstract class BaseManagerTestCase {

    protected transient Logger log = LogManager.getLogger(getClass());

    protected ResourceBundle rb;

    @Autowired
    protected ApplicationContext applicationContext;

    public BaseManagerTestCase() {
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
