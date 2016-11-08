package common.model;

import java.util.List;

import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlTransient;

/**
 * エンティティリストの基底クラス.
 */
@MappedSuperclass
public abstract class BaseObjects<T> {

    /** エンティティリスト */
    private List<T> objects;

    /**
     * デフォルト・コンストラクタ.
     */
    public BaseObjects() {
    }

    /**
     * コンストラクタ.
     *
     * @param objects
     *            エンティティリスト
     */
    public BaseObjects(List<T> objects) {
        this.objects = objects;
    }

    /**
     * 件数を取得する.
     *
     * @return 件数
     */
    public int getCount() {
        return objects.size();
    }

    /**
     * エンティティリストを取得する.
     *
     * @return エンティティリスト
     */
    @XmlTransient
    public List<T> getObjects() {
        return objects;
    }

    /**
     * エンティティリストを設定する.
     *
     * @param objects
     *            エンティティリスト
     */
    public void setObjects(List<T> objects) {
        this.objects = objects;
    }
}
