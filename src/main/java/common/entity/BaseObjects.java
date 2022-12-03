package common.entity;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.MappedSuperclass;
import jakarta.xml.bind.annotation.XmlTransient;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * エンティティリストの基底クラス.
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@MappedSuperclass
public abstract class BaseObjects<T extends Serializable> implements Serializable {

    /** エンティティリスト */
    private List<T> objects;

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
}
