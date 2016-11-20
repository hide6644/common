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
        List<Role> roles = entityManager.createNamedQuery(Role.FIND_BY_NAME, persistentClass).setParameter("name", name).getResultList();

        if (roles.isEmpty()) {
            return null;
        } else {
            return roles.get(0);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeRole(String name) {
        entityManager.remove(getRoleByName(name));
    }
}
