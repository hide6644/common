package common.entity;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.NoArgsConstructor;

/**
 * ユーザリスト.
 */
@NoArgsConstructor
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public final class Users extends BaseObjects<User> implements Serializable {

    /**
     * コンストラクタ.
     *
     * @param users
     *            ユーザリスト
     */
    public Users(List<User> users) {
        super(users);
    }

    /**
     * ユーザリストを取得する.
     *
     * @return ユーザリスト
     */
    @XmlElement(name = "user")
    public List<User> getUsers() {
        return super.getObjects();
    }

    /**
     * ユーザリストを設定する.
     *
     * @param users
     *            ユーザリスト
     */
    public void setUsers(List<User> users) {
        super.setObjects(users);
    }
}
