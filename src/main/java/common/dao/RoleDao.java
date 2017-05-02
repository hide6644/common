package common.dao;

import common.model.Role;

/**
 * 権限DAOインターフェイス.
 */
public interface RoleDao extends GenericDao<Role, Long> {

    /**
     * 指定された名称の権限を取得する.
     *
     * @param name
     *            名称
     * @return 権限
     */
    Role getByNameEquals(String name);

    /**
     * 指定された名称の権限を削除する.
     *
     * @param name
     *            名称
     */
    void removeRole(String name);
}
