package common.dao.jpa;

import java.util.List;

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
        List<?> roles = getEntityManager().createNamedQuery(Role.FIND_BY_NAME).setParameter("name", name).getResultList();

        if (roles.isEmpty()) {
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
