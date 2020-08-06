package common.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;

import common.entity.Role;

/**
 * 権限DAOインターフェイス.
 */
public interface RoleDao extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {

    /**
     * 指定された名称の権限を取得する.
     *
     * @param name
     *            名称
     * @return 権限
     */
    Role findByName(@Param("name") String name);

    /**
     * 指定された名称の権限を削除する.
     *
     * @param name
     *            名称
     */
    void deleteByName(@Param("name") String name);
}
