package common.dao.hibernate;

import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.EntityPersister;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import common.dao.BaseDaoTestCase;

public class HibernateConfigurationTest extends BaseDaoTestCase {

    @Autowired
    SessionFactory sessionFactory;

    @Test
    public void testColumnMapping() throws Exception {
        Session session = sessionFactory.openSession();

        try {
            Map<String, ClassMetadata> metadata = sessionFactory.getAllClassMetadata();

            for (ClassMetadata cm : metadata.values()) {
                EntityPersister persister = (EntityPersister) cm;
                String className = persister.getEntityName();
                log.debug("Trying select * from: " + className);
                Query q = session.createQuery("from " + className + " c");
                q.iterate();
                log.debug("ok: " + className);
            }
        } finally {
            session.close();
        }
    }
}
