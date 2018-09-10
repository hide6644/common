package common.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import common.entity.Role;

/**
 * 権限DAOインターフェイス.
 */
public interface RoleDao extends JpaRepository<Role, Long> {

    /**
     * 指定された名称の権限を取得する.
     *
     * @param name
     *            名称
     * @return 権限
     */
    Role findByName(String name);

    /**
     * 指定された名称の権限を削除する.
     *
     * @param name
     *            名称
     */
    void removeByName(String name);
}
