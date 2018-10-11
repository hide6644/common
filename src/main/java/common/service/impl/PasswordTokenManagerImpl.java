package common.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import common.entity.User;
import common.service.PasswordTokenManager;

/**
 * パスワードトークン処理の実装クラス.
 */
@Component("passwordTokenManager")
public class PasswordTokenManagerImpl implements PasswordTokenManager {

    /** 有効期限日付のフォーマット */
    public static final String EXPIRATION_DATE_FORMAT = "yyyyMMddHHmm";

    /** パスワードエンコーダー */
    @Qualifier("passwordTokenEncoder")
    @Autowired
    private PasswordEncoder passwordTokenEncoder;

    /**
     * {@inheritDoc}
     */
    @Override
    public String generateRecoveryToken(User user) {
        return Optional.ofNullable(user).map(entity -> {
            String tokenSource = getTokenSource(entity);
            String expirationTimeStamp = LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ofPattern(EXPIRATION_DATE_FORMAT));
            return expirationTimeStamp + passwordTokenEncoder.encode(expirationTimeStamp + tokenSource);
        }).orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRecoveryTokenValid(User user, String token) {
        if (token == null || token.length() < EXPIRATION_DATE_FORMAT.length()) {
            return false;
        }

        String expirationTimestamp = token.substring(0, EXPIRATION_DATE_FORMAT.length());
        String tokenWithoutTimestamp = token.substring(EXPIRATION_DATE_FORMAT.length());

        return Optional.ofNullable(user).map(userData -> isAfter(expirationTimestamp)
                && passwordTokenEncoder.matches(expirationTimestamp + getTokenSource(userData), tokenWithoutTimestamp))
                .orElse(false);
    }

    /**
     * トークン生成の元となる文字列を取得する.
     *
     * @param user
     *            ユーザ
     * @return トークン生成の元となる文字列
     */
    private String getTokenSource(User user) {
        return user.getEmail() + user.getVersion() + user.getPassword();
    }

    /**
     * 指定された日付/時間が現在の日付/時間より後であるか.
     *
     * @param timestamp
     *            日付/時間
     * @return true:指定された日付/時間が現在の日付/時間より後
     */
    private boolean isAfter(String timestamp) {
        try {
            return LocalDateTime.parse(timestamp, DateTimeFormatter.ofPattern(EXPIRATION_DATE_FORMAT)).isAfter(LocalDateTime.now());
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
