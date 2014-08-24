package common.webapp.taglib;

import java.util.Collection;

/**
 * CollectionのcontainsをTag Librariesとして提供するUtilityクラス.
 *
 * @author hide6644
 */
public class Contains {

    /**
     * プライベート・コンストラクタ.<br />
     * Utilityクラスはインスタンス化禁止.
     */
    private Contains() {
    }

    /**
     * コレクションに指定の要素があるかチェックする.
     *
     * @param col
     *            対象のコレクション
     * @param o
     *            コレクションに含まれるか確認する値
     * @return true 存在する, false 存在しない
     */
    public static boolean contains(Collection<?> col, Object o) {
        return col.contains(o);
    }
}
