package common.dao;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import common.webapp.util.ConvertUtil;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = { "classpath:/common/dao/applicationContext-resources.xml",
        "classpath:/common/dao/applicationContext-dao.xml", "classpath*:/applicationContext.xml" })
@Transactional
@Rollback
public abstract class BaseDaoTestCase {

    public static final String PERSISTENCE_UNIT_NAME = "ApplicationEntityManager";

    protected final transient Logger log = LogManager.getLogger(getClass());

    protected ResourceBundle rb;

    @PersistenceContext(unitName = PERSISTENCE_UNIT_NAME)
    protected EntityManager entityManager;

    public BaseDaoTestCase() {
        String className = this.getClass().getName();

        try {
            rb = ResourceBundle.getBundle(className);
        } catch (MissingResourceException e) {
            log.trace("No resource bundle found for:{}", className, e);
        }
    }

    protected Object populate(final Object obj) {
        Map<String, String> map = ConvertUtil.convertBundleToMap(rb);

        try {
            BeanUtils.copyProperties(obj, map);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.trace("Copy failed:", e);
        }

        return obj;
    }
}
