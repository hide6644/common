package common.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.MessageSourceAccessor;
import lombok.extern.log4j.Log4j2;

/**
 * 基底クラス.
 */
@Log4j2
public class BaseManagerImpl {

    /** メッセージソースアクセサー */
    protected MessageSourceAccessor messageSourceAccessor;

    /**
     * メッセージソースからメッセージを取得する.
     *
     * @param msgKey
     *            キー
     * @return メッセージ
     */
    protected String getText(String msgKey) {
        try {
            return messageSourceAccessor.getMessage(msgKey);
        } catch (NoSuchMessageException e) {
            log.error(e);
            return "{" + msgKey + "}";
        }
    }

    /**
     * メッセージソースからメッセージを取得する.
     *
     * @param msgKey
     *            キー
     * @param args
     *            引数
     * @return メッセージ
     */
    protected String getText(String msgKey, Object... args) {
        try {
            return messageSourceAccessor.getMessage(msgKey, args);
        } catch (NoSuchMessageException e) {
            log.error(e);
            return "{" + msgKey + "}";
        }
    }

    /**
     * メッセージソースを設定する.
     *
     * @param messageSource
     *            メッセージソース
     */
    @Autowired
    public void setMessages(MessageSource messageSource) {
        messageSourceAccessor = new MessageSourceAccessor(messageSource);
    }
}
