package common.service;

import java.util.List;
import java.util.Set;

import common.dto.LabelValue;
import common.entity.Role;

/**
 * 権限処理のインターフェース.
 */
public interface RoleManager {

    /**
     * 指定された名称の権限を取得する.
     *
     * @param rolename
     *            名称
     * @return 権限
     */
    Role getRole(String rolename);

    /**
     * 指定された名称の権限の一覧を取得する.
     *
     * @param names
     *            権限の一覧(名称)
     * @return 権限の一覧
     */
    Set<Role> getRoles(Set<Role> names);

    /**
     * 指定された名称の権限を削除する.
     *
     * @param name
     *            名称
     */
    void removeRole(String name);

    /**
     * プルダウン表示用の権限一覧を取得する.
     *
     * @return プルダウン表示用の子分類一覧
     */
    List<LabelValue> getLabelValues();
}
