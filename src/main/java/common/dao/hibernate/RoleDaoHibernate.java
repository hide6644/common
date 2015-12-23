package common.dao.hibernate;

import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import common.dao.RoleDao;
import common.model.Role;

/**
 * 権限DAOクラス.
 */
@Repository
public class RoleDaoHibernate extends GenericDaoHibernate<Role, Long> implements RoleDao {

    /**
     * デフォルト・コンストラクタ
     */
    public RoleDaoHibernate() {
        super(Role.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Role getRoleByName(String name) {
        List<?> roles = getSession().createCriteria(Role.class).add(Restrictions.eq("name", name)).list();

        if (roles == null || roles.isEmpty()) {
            return null;
        } else {
            return (Role) roles.get(0);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeRole(String name) {
        getSessionFactory().getCurrentSession().delete(getRoleByName(name));
    }
}
