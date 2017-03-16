package common.dao;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;

import common.model.User;

/**
 * ユーザDAOインターフェイス.
 */
public interface UserDao extends PaginatedDao<User, Long> {

    /**
     * すべてのユーザを取得する.
     *
     * @return ユーザ一覧
     */
    List<User> getAllOrderByUsername();

    /**
     * 指定されたユーザ名のユーザ情報を取得する.
     *
     * @param username
     *            ユーザ名
     * @return ユーザ情報
     */
    UserDetails loadUserByUsername(String username);

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
    String getPasswordById(Long id);
}
