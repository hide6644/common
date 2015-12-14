package common.dao.hibernate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.Search;
import org.hibernate.search.query.facet.Facet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.orm.ObjectRetrievalFailureException;

import common.dao.GenericDao;
import common.dao.SearchException;

/**
 * 一般的なCRUD DAOの実装クラス.
 */
public class GenericDaoHibernate<T, PK extends Serializable> implements GenericDao<T, PK> {

    /** ログ出力クラス */
    protected final Log log = LogFactory.getLog(getClass());

    /** エンティティクラス */
    private Class<T> persistentClass;

    /** DBセッション生成クラス */
    @Resource
    private SessionFactory sessionFactory;

    /**
     * コンストラクタ.
     *
     * @param persistentClass
     *            エンティティクラス
     */
    public GenericDaoHibernate(Class<T> persistentClass) {
        this.persistentClass = persistentClass;
    }

    /**
     * コンストラクタ.
     *
     * @param persistentClass
     *            エンティティクラス
     * @param sessionFactory
     *            セッション生成クラス
     */
    public GenericDaoHibernate(Class<T> persistentClass, SessionFactory sessionFactory) {
        this.persistentClass = persistentClass;
        this.sessionFactory = sessionFactory;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<T> getAll() {
        return getSession().createCriteria(persistentClass).list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> getAllDistinct() {
        return new ArrayList<T>(new LinkedHashSet<T>(getAll()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T get(PK id) {
        T entity = getSession().byId(persistentClass).load(id);

        if (entity == null) {
            log.warn("'" + persistentClass + "' object with id '" + id + "' not found...");
            throw new ObjectRetrievalFailureException(persistentClass, id);
        }

        return entity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exists(PK id) {
        return getSession().byId(persistentClass).load(id) != null;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public T save(T object) {
        return (T) getSession().merge(object);
    }

    /**
     * {@inheritDoc}
     */
    @Override
   public void remove(T object) {
        getSession().delete(object);
    }

   /**
    * {@inheritDoc}
    */
    @Override
    public void remove(PK id) {
        Session sess = getSession();
        sess.delete(sess.byId(persistentClass).load(id));
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public List<T> findByNamedQuery(String queryName, Map<String, Object> queryParams) {
        Query namedQuery = getSession().getNamedQuery(queryName);

        if (queryParams != null) {
            queryParams.forEach((key, val) -> {
                if (val instanceof Collection) {
                    namedQuery.setParameterList(key, (Collection) val);
                } else {
                    namedQuery.setParameter(key, val);
                }
            });
        }

        return namedQuery.list();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<T> search(String searchTerm) throws SearchException {
        Session sess = getSession();
        FullTextQuery hibQuery = Search.getFullTextSession(sess).createFullTextQuery(
                HibernateSearchTools.generateQuery(searchTerm, persistentClass, sess), persistentClass);

        return hibQuery.list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Facet> facet(String field, int maxCount) {
        return HibernateSearchTools.generateFacet(field, maxCount, persistentClass, getSession());
    }

   /**
    * {@inheritDoc}
    */
    @Override
    public void reindex() {
        HibernateSearchTools.reindex(persistentClass, getSessionFactory().getCurrentSession());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reindexAll(boolean async) {
        HibernateSearchTools.reindexAll(async, getSessionFactory().getCurrentSession());
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<T> getPaged(Class<?> searchClass, Object searchCondition, Integer offset, Integer limit) {
        Criteria criteria = getSession().createCriteria(searchClass);
        criteria.setFirstResult(offset);
        criteria.setMaxResults(limit);

        return criteria.list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getRecordCount(Class<?> searchClass, Object searchCondition) {
        Criteria criteria = getSession().createCriteria(searchClass);
        criteria.setProjection(Projections.rowCount());

        return (Long) criteria.uniqueResult();
    }

    /**
     * DBセッション生成クラスを取得する.
     *
     * @return DBセッション生成クラス
     */
    public SessionFactory getSessionFactory() {
        return this.sessionFactory;
    }

    /**
     * DBセッション生成クラスを設定する.
     *
     * @param sessionFactory
     *            DBセッション生成クラス
     */
    @Autowired
    @Required
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * DBセッションを取得する.
     *
     * @return DBセッション
     */
    public Session getSession() {
        return Optional.ofNullable(getSessionFactory().getCurrentSession())
                .orElseGet(() -> getSessionFactory().openSession());
    }
}
