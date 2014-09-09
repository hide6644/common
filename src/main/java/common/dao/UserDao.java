package common.dao;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import common.model.User;

/**
 * ユーザDAOインターフェイス.
 */
public interface UserDao extends GenericDao<User, Long> {

    /**
     * 指定されたユーザ名のユーザ情報を取得する.
     *
     * @param username
     *            ユーザ名
     * @return ユーザ情報
     */
    @Transactional
    UserDetails loadUserByUsername(String username);

    /**
     * すべてのユーザを取得する.
     *
     * @return ユーザ一覧
     */
    List<User> getUsers();

    /**
     * 指定されたユーザを永続化する.
     *
     * @param user
     *            永続化するユーザ
     * @return 永続化されたユーザ
     */
    User saveUser(User user);

    /**
     * 指定されたユーザのパスワードを取得する.
     *
     * @param id
     *            ユーザID
     * @return パスワード
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    String getUserPassword(Long id);
}
