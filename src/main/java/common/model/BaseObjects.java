package common.model;

import java.util.List;

import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlTransient;

/**
 * エンティティリストの基底クラス.
 */
@MappedSuperclass
public abstract class BaseObjects<T> {

    /** 件数 */
    private int count;

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
        count = objects.size();
    }

    /**
     * 件数を取得する.
     *
     * @return 件数
     */
    public int getCount() {
        return count;
    }

    /**
     * 件数を設定する.
     *
     * @param count
     *            件数
     */
    public void setCount(int count) {
        this.count = count;
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
