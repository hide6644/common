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

@Component("passwordTokenManager")
public class PasswordTokenManagerImpl implements PasswordTokenManager {

    private SimpleDateFormat expirationTimeFormat = new SimpleDateFormat("yyyyMMddHHmm");

    private int expirationTimeTokenLength = expirationTimeFormat.toPattern().length();

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
            String expirationTimeStamp = expirationTimeFormat.format(getExpirationTime());
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
            String expirationTimeStamp = getTimestamp(token);
            String tokenWithoutTimestamp = getTokenWithoutTimestamp(token);
            String tokenSource = expirationTimeStamp + getTokenSource(user);
            Date expirationTime = parseTimestamp(expirationTimeStamp);

            return expirationTime != null && expirationTime.after(new Date()) && passwordTokenEncoder.matches(tokenSource, tokenWithoutTimestamp);
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void invalidateRecoveryToken(User user, String token) {
        // NOP
    }

    /**
     * Return tokens expiration time, now + 1 day.
     *
     * @return
     */
    private Date getExpirationTime() {
        return DateUtils.addDays(new Date(), 1);
    }

    private String getTimestamp(String token) {
        return StringUtils.substring(token, 0, expirationTimeTokenLength);
    }

    private String getTokenSource(User user) {
        return user.getEmail() + user.getVersion() + user.getPassword();
    }

    private String getTokenWithoutTimestamp(String token) {
        return StringUtils.substring(token, expirationTimeTokenLength, token.length());
    }

    private Date parseTimestamp(String timestamp) {
        try {
            return expirationTimeFormat.parse(timestamp);
        } catch (final ParseException e) {
            return null;
        }
    }
}
