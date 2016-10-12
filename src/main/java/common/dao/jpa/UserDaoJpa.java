package common.dao.jpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Table;
import javax.persistence.criteria.CriteriaBuilder;
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
    public List<User> getUsers() {
        return entityManager.createNamedQuery(User.GET_ALL, persistentClass).getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<User> users = entityManager.createNamedQuery(User.FIND_BY_USERNAME, persistentClass).setParameter("username", username).getResultList();

        if (users.isEmpty()) {
            throw new UsernameNotFoundException("user '" + username + "' not found...");
        } else {
            return users.get(0);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User saveUser(User user) {
        user = entityManager.merge(user);
        entityManager.flush();
        return user;
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
    @Override
    protected List<Predicate> makeSearchCondition(CriteriaBuilder builder, Root<User> root, Object searchCondition) {
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
