package common.service;

import java.util.List;

import common.model.LabelValue;
import common.model.Role;

/**
 * 権限処理のインターフェース.
 */
public interface RoleManager extends GenericManager<Role, Long> {

    /**
     * 指定された名称の権限を取得する.
     *
     * @param rolename
     *            名称
     * @return 権限
     */
    Role getRole(String rolename);

    /**
     * 指定された名称の権限を削除する.
     *
     * @param rolename
     *            名称
     */
    void removeRole(String rolename);

    /**
     * プルダウン表示用の権限一覧を取得する.
     *
     * @return プルダウン表示用の子分類一覧
     */
    public List<LabelValue> getLabelValues();
}
