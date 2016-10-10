package common.dao.jpa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

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
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<T> getList(Object searchCondition, Integer offset, Integer limit) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = builder.createQuery(persistentClass);
        Root<T> root = criteriaQuery.from(persistentClass);
        List<Predicate> preds = makeSearchCondition(builder, root, searchCondition);

        criteriaQuery.where(builder.and(preds.toArray(new Predicate[]{})));
        criteriaQuery.orderBy(builder.asc(root.get("username")));

        Query query = entityManager.createQuery(criteriaQuery);

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
        List<Predicate> preds = makeSearchCondition(builder, root, searchCondition);

        criteriaQuery.select(builder.count(root));
        criteriaQuery.where(builder.and(preds.toArray(new Predicate[]{})));


        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    /**
     * 検索条件を生成する.
     *
     * @param builder
     *            {@link CriteriaBuilder}
     * @param root
     *            {@link Root}
     * @param searchCondition
     *            検索条件
     * @return 検索条件
     */
    protected List<Predicate> makeSearchCondition(CriteriaBuilder builder, Root<T> root, Object searchCondition) {
        return  new ArrayList<>();
    }
}
