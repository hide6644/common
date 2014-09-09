package common.dao.hibernate;

import java.util.List;

import javax.persistence.Table;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate4.SessionFactoryUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import common.dao.UserDao;
import common.model.User;

/**
 * ユーザDAOクラス.
 */
@Repository("userDao")
public class UserDaoHibernate extends GenericDaoHibernate<User, Long> implements UserDao, UserDetailsService {

    /**
     * デフォルト・コンストラクタ
     */
    public UserDaoHibernate() {
        super(User.class);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<User> getUsers() {
        return getSession().createQuery("from User u order by upper(u.username)").list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User saveUser(User user) {
        if (log.isDebugEnabled()) {
            log.debug("user's id: " + user.getId());
        }

        getSession().saveOrUpdate(user);
        // necessary to throw a DataIntegrityViolation and catch it in UserManager
        getSession().flush();
        return user;
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
    public UserDetails loadUserByUsername(String username) {
        List<?> users = getSession().createCriteria(User.class).add(Restrictions.eq("username", username)).list();

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
    public String getUserPassword(Long id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(SessionFactoryUtils.getDataSource(getSessionFactory()));
        Table table = AnnotationUtils.findAnnotation(User.class, Table.class);
        return jdbcTemplate.queryForObject("select password from " + table.name() + " where id=?", String.class, id);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<User> getPaged(Class<?> searchClass, Object searchCondition, Integer offset, Integer limit) {
        Criteria criteria = getSession().createCriteria(searchClass);
        criteria.setFirstResult(offset);
        criteria.setMaxResults(limit);

        User user = (User) searchCondition;
        if (user.getUsername() != null) {
            criteria.add(Restrictions.like("username", "%" + user.getUsername() + "%"));
        }
        if (user.getEmail() != null) {
            criteria.add(Restrictions.like("email", "%" + user.getEmail() + "%"));
        }
        criteria.addOrder(Order.asc("username"));

        return criteria.list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getRecordCount(Class<?> searchClass, Object searchCondition) {
        Criteria criteria = getSession().createCriteria(searchClass);
        criteria.setProjection(Projections.rowCount());

        User user = (User) searchCondition;
        if (user.getUsername() != null) {
            criteria.add(Restrictions.like("username", "%" + user.getUsername() + "%"));
        }
        if (user.getEmail() != null) {
            criteria.add(Restrictions.like("email", "%" + user.getEmail() + "%"));
        }

        return (Long) criteria.uniqueResult();
    }
}
