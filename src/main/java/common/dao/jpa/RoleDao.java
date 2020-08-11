package common.dao.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import common.entity.Role;

/**
 * 権限DAOインターフェイス.
 */
public interface RoleDao extends JpaRepository<Role, String> {
}
