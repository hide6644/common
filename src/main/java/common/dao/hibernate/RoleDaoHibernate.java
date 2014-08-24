package common.dao.hibernate;

import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import common.dao.RoleDao;
import common.model.Role;

@Repository
public class RoleDaoHibernate extends GenericDaoHibernate<Role, Long> implements RoleDao {

    public RoleDaoHibernate() {
        super(Role.class);
    }

    /**
     * {@inheritDoc}
     */
    public Role getRoleByName(String name) {
        List<?> roles = getSession().createCriteria(Role.class).add(Restrictions.eq("name", name)).list();

        if (roles.isEmpty()) {
            return null;
        } else {
            return (Role) roles.get(0);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeRole(String name) {
        getSessionFactory().getCurrentSession().delete(getRoleByName(name));
    }
}
