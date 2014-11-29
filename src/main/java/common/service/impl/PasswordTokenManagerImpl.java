package common.service.impl;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import common.model.User;
import common.service.PasswordTokenManager;

/**
 * パスワードトークン処理の実装クラス.
 */
@Component("passwordTokenManager")
public class PasswordTokenManagerImpl implements PasswordTokenManager {

    /** 有効期限日付のフォーマット */
    public static final String EXPIRATION_TIME_FORMAT = "yyyyMMddHHmm";

    /** パスワードエンコーダー */
    @Qualifier("passwordTokenEncoder")
    @Autowired
    private PasswordEncoder passwordTokenEncoder;

    /**
     * {@inheritDoc}
     */
    @Override
    public String generateRecoveryToken(User user) {
        if (user != null) {
            String tokenSource = getTokenSource(user);
            String expirationTimeStamp = new DateTime().plusDays(1).toString(EXPIRATION_TIME_FORMAT);
            return expirationTimeStamp + passwordTokenEncoder.encode(expirationTimeStamp + tokenSource);
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRecoveryTokenValid(User user, String token) {
        if (user != null && token != null) {
            String expirationTimeStamp = token.substring(0, EXPIRATION_TIME_FORMAT.length());
            String tokenWithoutTimestamp = token.substring(EXPIRATION_TIME_FORMAT.length());
            String tokenSource = expirationTimeStamp + getTokenSource(user);
            DateTime expirationTime = parseTimestamp(expirationTimeStamp);

            return expirationTime != null && expirationTime.isAfterNow() && passwordTokenEncoder.matches(tokenSource, tokenWithoutTimestamp);
        }

        return false;
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
     * 文字列から日付型に変換する.
     *
     * @param timestamp
     *            日付文字列
     * @return 日付型
     */
    private DateTime parseTimestamp(String timestamp) {
        try {
            return DateTimeFormat.forPattern(EXPIRATION_TIME_FORMAT).parseDateTime(timestamp);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
