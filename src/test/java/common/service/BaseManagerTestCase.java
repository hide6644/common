package common.service;

import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import common.service.util.ConvertUtil;

@ContextConfiguration(locations = { "classpath:/common/service/applicationContext-resources.xml",
        "classpath:/common/dao/applicationContext-dao.xml", "classpath:/applicationContext-service.xml",
        "classpath*:/**/applicationContext.xml"
})
public abstract class BaseManagerTestCase extends AbstractTransactionalJUnit4SpringContextTests {

    protected transient final Log log = LogFactory.getLog(getClass());

    protected ResourceBundle rb;

    public BaseManagerTestCase() {
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
