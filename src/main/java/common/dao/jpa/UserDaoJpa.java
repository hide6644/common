package common.dao.jpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import common.dao.UserDao;
import common.model.User;

/**
 * ユーザDAOクラス.
 */
@Repository("userDao")
public class UserDaoJpa extends GenericDaoJpa<User, Long> implements UserDao, UserDetailsService {

    @Autowired
    private DataSource dataSource;

    /**
     * デフォルト・コンストラクタ
     */
    public UserDaoJpa() {
        super(User.class);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<User> getUsers() {
        return getEntityManager().createNamedQuery(User.GET_ALL).getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<?> users = getEntityManager().createNamedQuery(User.FIND_BY_USERNAME).setParameter("username", username).getResultList();

        if (users == null || users.isEmpty()) {
            throw new UsernameNotFoundException("user '" + username + "' not found...");
        } else {
            return (UserDetails) users.get(0);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User saveUser(User user) {
        return entityManager.merge(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User save(User user) {
        return this.saveUser(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserPassword(Long id) {
        Table table = AnnotationUtils.findAnnotation(User.class, Table.class);
        return new JdbcTemplate(dataSource).queryForObject("select password from " + table.name() + " where id = ?", String.class, id);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<User> getPaged(Class<?> searchClass, Object searchCondition, Integer offset, Integer limit) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = builder.createQuery(User.class);
        Root<User> root = criteriaQuery.from((Class<User>) searchClass);
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
    public long getRecordCount(Class<?> searchClass, Object searchCondition) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
        @SuppressWarnings("unchecked")
        Root<User> root = criteriaQuery.from((Class<User>) searchClass);
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
    private List<Predicate> makeSearchCondition(CriteriaBuilder builder, Root<User> root, Object searchCondition) {
        User user = (User) searchCondition;
        List<Predicate> preds = new ArrayList<>();

        if (user.getUsername() != null) {
            preds.add(builder.like(root.get("username"), "%" + user.getUsername() + "%"));
        }
        if (user.getEmail() != null) {
            preds.add(builder.like(root.get("email"), "%" + user.getEmail() + "%"));
        }

        return preds;
    }
}
