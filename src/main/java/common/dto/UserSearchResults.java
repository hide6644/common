package common.dto;

import java.io.Serializable;

import common.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ユーザ検索結果を保持するクラス.
 */
@AllArgsConstructor
@Getter
public final class UserSearchResults implements Serializable {

    /** ID */
    private final Long id;

    /** ユーザ名 */
    private final String username;

    /** ｅメール */
    private final String email;

    /** 有効 */
    private final boolean enabled;

    /**
     * {@code UserSearchResults}のインスタンスを取得する.
     *
     * @param user
     *            ユーザ
     * @return {@code UserSearchResults}のインスタンス
     */
    public static UserSearchResults of(User user) {
        return new UserSearchResults(user.getId(), user.getUsername(), user.getEmail(), user.isEnabled());
    }
}
