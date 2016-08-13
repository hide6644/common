package common.dao.jpa;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import common.dao.RoleDao;
import common.model.Role;

/**
 * 権限DAOクラス.
 */
@Repository
public class RoleDaoJpa extends GenericDaoJpa<Role, Long> implements RoleDao {

    /**
     * デフォルト・コンストラクタ
     */
    public RoleDaoJpa() {
        super(Role.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Role getRoleByName(String name) {
        Query q = getEntityManager().createQuery("select r from Role r where r.name = ?");
        q.setParameter(1, name);
        List<?> roles = q.getResultList();

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
        getEntityManager().remove(getRoleByName(name));
    }
}
