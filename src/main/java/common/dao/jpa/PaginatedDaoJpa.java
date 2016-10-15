package common.dao.jpa;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.core.convert.TypeDescriptor;

import common.dao.PaginatedDao;

/**
 * Paginated List DAOの実装クラス.
 */
public class PaginatedDaoJpa<T, PK extends Serializable> extends GenericDaoJpa<T, PK> implements PaginatedDao<T, PK> {

    /**
     * コンストラクタ.
     *
     * @param persistentClass
     *            エンティティクラス
     */
    public PaginatedDaoJpa(Class<T> persistentClass) {
        super(persistentClass);
    }

    /**
     * コンストラクタ.
     *
     * @param persistentClass
     *            エンティティクラス
     * @param entityManager
     *            Entity Managerクラス
     */
    public PaginatedDaoJpa(Class<T> persistentClass, EntityManager entityManager) {
        super(persistentClass ,entityManager);
    }

    /**
     * 検索条件を生成する.
     *
     * @param builder
     *            {@link CriteriaBuilder}
     * @param root
     *            {@link Root}
     * @param searchCondition
     *            検索オブジェクト
     * @return 検索条件
     */
    protected Predicate makeSearchCondition(CriteriaBuilder builder, Root<T> root, Object searchCondition) {
        List<Predicate> preds = new ArrayList<>();
        BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(searchCondition);

        for (PropertyDescriptor propertyDesc : beanWrapper.getPropertyDescriptors()) {
            TypeDescriptor typeDesc = beanWrapper.getPropertyTypeDescriptor(propertyDesc.getName());

            if (typeDesc.getAnnotation(Column.class) != null) {
                Object value = beanWrapper.getPropertyValue(propertyDesc.getName());

                if (value != null) {
                    preds.add(builder.equal(root.get(propertyDesc.getName()), value));
                }
            }
        }

        return builder.and(preds.toArray(new Predicate[]{}));
    }

    /**
     * ソート条件を生成する.
     *
     * @param builder
     *            {@link CriteriaBuilder}
     * @param root
     *            {@link Root}
     * @param searchCondition
     *            検索オブジェクト
     * @return ソート条件
     */
    protected List<Order> makeOrder(CriteriaBuilder builder, Root<T> root, Object searchCondition) {
        List<Order> columns = new ArrayList<>();
        BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(searchCondition);

        for (PropertyDescriptor propertyDesc : beanWrapper.getPropertyDescriptors()) {
            TypeDescriptor typeDesc = beanWrapper.getPropertyTypeDescriptor(propertyDesc.getName());

            if (typeDesc.getAnnotation(Id.class) != null) {
                columns.add(builder.asc(root.get(propertyDesc.getName())));
            }
        }

        return columns;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> getList(Object searchCondition, Integer offset, Integer limit) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = builder.createQuery(persistentClass);
        Root<T> root = criteriaQuery.from(persistentClass);

        if (searchCondition != null) {
            criteriaQuery.where(makeSearchCondition(builder, root, searchCondition));
            criteriaQuery.orderBy(makeOrder(builder, root, searchCondition));
        }

        TypedQuery<T> query = entityManager.createQuery(criteriaQuery);

        query.setFirstResult(offset);
        query.setMaxResults(limit);

        return query.getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getCount(Object searchCondition) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
        Root<T> root = criteriaQuery.from(persistentClass);

        criteriaQuery.select(builder.count(root));

        if (searchCondition != null) {
            criteriaQuery.where(makeSearchCondition(builder, root, searchCondition));
        }

        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<T> searchList(String searchTerm, Integer offset, Integer limit) {
        FullTextQuery query = Search.getFullTextEntityManager(entityManager).createFullTextQuery(HibernateSearchJpaTools.generateQuery(searchTerm, persistentClass, entityManager, defaultAnalyzer), persistentClass);

        query.setFirstResult(offset);
        query.setMaxResults(limit);

        return query.getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long searchCount(String searchTerm) {
        FullTextQuery query = Search.getFullTextEntityManager(entityManager).createFullTextQuery(HibernateSearchJpaTools.generateQuery(searchTerm, persistentClass, entityManager, defaultAnalyzer), persistentClass);

        return query.getResultSize();
    }
}
