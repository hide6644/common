package common.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
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
    private SimpleDateFormat expirationTimeFormat = new SimpleDateFormat("yyyyMMddHHmm");

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
            String expirationTimeStamp = expirationTimeFormat.format(DateUtils.addDays(new Date(), 1));
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
            String expirationTimeStamp = StringUtils.substring(token, 0, expirationTimeFormat.toPattern().length());
            String tokenWithoutTimestamp = StringUtils.substring(token, expirationTimeFormat.toPattern().length(), token.length());
            String tokenSource = expirationTimeStamp + getTokenSource(user);
            Date expirationTime = parseTimestamp(expirationTimeStamp);

            return expirationTime != null && expirationTime.after(new Date()) && passwordTokenEncoder.matches(tokenSource, tokenWithoutTimestamp);
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
    private Date parseTimestamp(String timestamp) {
        try {
            return expirationTimeFormat.parse(timestamp);
        } catch (ParseException e) {
            return null;
        }
    }
}
