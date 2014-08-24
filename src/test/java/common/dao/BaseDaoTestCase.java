package common.dao;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hibernate.search.Search;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

@ContextConfiguration(locations = { "classpath:/common/dao/applicationContext-resources.xml",
        "classpath:/applicationContext-dao.xml", "classpath*:/applicationContext.xml",
        "classpath:**/applicationContext*.xml" })
public abstract class BaseDaoTestCase extends AbstractTransactionalJUnit4SpringContextTests {

    protected transient final Log log = LogFactory.getLog(getClass());

    protected ResourceBundle rb;

    @Autowired
    private SessionFactory sessionFactory;

    public BaseDaoTestCase() {
        String className = this.getClass().getName();

        try {
            rb = ResourceBundle.getBundle(className);
        } catch (MissingResourceException mre) {
            log.trace("No resource bundle found for: " + className);
        }
    }

    protected Object populate(final Object obj) throws Exception {
        Map<String, String> map = new HashMap<String, String>();

        for (Enumeration<String> keys = rb.getKeys(); keys.hasMoreElements();) {
            String key = keys.nextElement();
            map.put(key, rb.getString(key));
        }

        BeanUtils.copyProperties(obj, map);

        return obj;
    }

    protected void flush() throws BeansException {
        sessionFactory.getCurrentSession().flush();
    }

    public void flushSearchIndexes() {
        Search.getFullTextSession(sessionFactory.getCurrentSession()).flushToIndexes();
    }
}
