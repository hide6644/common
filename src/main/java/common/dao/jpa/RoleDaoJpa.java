package common.dao.jpa;

import javax.persistence.NoResultException;

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
    public Role getByNameEquals(String name) {
        try {
            return entityManager.createNamedQuery("Role.findByNameEquals", persistentClass).setParameter("name", name).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeRole(String name) {
        entityManager.remove(getByNameEquals(name));
    }
}
