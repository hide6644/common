package common.dao.hibernate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
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
    public GenericDaoHibernate(final Class<T> persistentClass) {
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
    public GenericDaoHibernate(final Class<T> persistentClass, SessionFactory sessionFactory) {
        this.persistentClass = persistentClass;
        this.sessionFactory = sessionFactory;
    }

    @SuppressWarnings("unchecked")
    public List<T> getAll() {
        return getSession().createCriteria(persistentClass).list();
    }

    public List<T> getAllDistinct() {
        return new ArrayList<T>(new LinkedHashSet<T>(getAll()));
    }

    public T get(PK id) {
        @SuppressWarnings("unchecked")
        T entity = (T) getSession().byId(persistentClass).load(id);

        if (entity == null) {
            log.warn("'" + persistentClass + "' object with id '" + id + "' not found...");
            throw new ObjectRetrievalFailureException(persistentClass, id);
        }

        return entity;
    }

    public boolean exists(PK id) {
        return getSession().byId(persistentClass).load(id) != null;
    }

    @SuppressWarnings("unchecked")
    public T save(T object) {
        return (T) getSession().merge(object);
    }

    public void remove(T object) {
        getSession().delete(object);
    }

    public void remove(PK id) {
        Session sess = getSession();
        sess.delete(sess.byId(persistentClass).load(id));
    }

    @SuppressWarnings("unchecked")
    public List<T> findByNamedQuery(String queryName, Map<String, Object> queryParams) {
        Query namedQuery = getSession().getNamedQuery(queryName);

        if (queryParams != null) {
            for (String s : queryParams.keySet()) {
                namedQuery.setParameter(s, queryParams.get(s));
            }
        }

        return namedQuery.list();
    }

    @SuppressWarnings("unchecked")
    public List<T> search(String searchTerm) throws SearchException {
        Session sess = getSession();
        FullTextQuery hibQuery = Search.getFullTextSession(sess).createFullTextQuery(
                HibernateSearchTools.generateQuery(searchTerm, persistentClass, sess), persistentClass);

        return hibQuery.list();
    }

    public List<Facet> facet(String field, int maxCount) {
        return HibernateSearchTools.generateFacet(field, maxCount, persistentClass, getSession());
    }

    public void reindex() {
        HibernateSearchTools.reindex(persistentClass, getSessionFactory().getCurrentSession());
    }

    public void reindexAll(boolean async) {
        HibernateSearchTools.reindexAll(async, getSessionFactory().getCurrentSession());
    }

    public List<T> getPaged(Serializable search, Integer offset, Integer limit) {
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("search", search);
        queryParams.put("offset", offset);
        queryParams.put("limit", limit);

        return findByNamedQuery("getPaged" + persistentClass + "s", queryParams);
    }

    public int getRecordCount(Serializable search) {
        Query namedQuery = getSession().getNamedQuery("get" + persistentClass + "RecordCount");
        namedQuery.setParameter("search", search);

        return (Integer) namedQuery.list().get(0);
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
     * @throws HibernateException
     */
    public Session getSession() throws HibernateException {
        Session sess = getSessionFactory().getCurrentSession();

        if (sess == null) {
            sess = getSessionFactory().openSession();
        }

        return sess;
    }
}
