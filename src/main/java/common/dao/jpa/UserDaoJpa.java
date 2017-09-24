package common.dao.jpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Table;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import common.dao.UserDao;
import common.model.User;

/**
 * ユーザDAOクラス.
 */
@Repository("userDao")
public class UserDaoJpa extends PaginatedDaoJpa<User, Long> implements UserDao, UserDetailsService {

    /** Data Sourceクラス */
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
    @Override
    public List<User> getAllOrderByUsername() {
        return entityManager.createNamedQuery("User.findAllOrderByUsername", persistentClass).getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) {
        try {
            return entityManager.createNamedQuery("User.findByUsernameEquals", persistentClass).setParameter("username", username).getSingleResult();
        } catch (NoResultException e) {
            throw new UsernameNotFoundException("user '" + username + "' not found...");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User saveUser(User user) {
        User managedUser = entityManager.merge(user);
        entityManager.flush();
        return managedUser;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public String getPasswordById(Long id) {
        Table table = AnnotationUtils.findAnnotation(User.class, Table.class);
        return new JdbcTemplate(dataSource).queryForObject("select password from " + table.name() + " where id = ?", String.class, id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Predicate makeSearchCondition(CriteriaBuilder builder, Root<User> root, Object searchCondition) {
        User user = (User) searchCondition;
        List<Predicate> preds = new ArrayList<>();

        if (user.getUsername() != null) {
            preds.add(builder.like(root.get("username"), "%" + user.getUsername() + "%"));
        }
        if (user.getEmail() != null) {
            preds.add(builder.like(root.get("email"), "%" + user.getEmail() + "%"));
        }

        return builder.and(preds.toArray(new Predicate[0]));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<Order> makeOrder(CriteriaBuilder builder, Root<User> root, Object searchCondition) {
        List<Order> columns = new ArrayList<>();
        columns.add(builder.asc(root.get("username")));
        return columns;
    }
}
