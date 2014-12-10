package common.webapp.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;

import common.exception.DBCheckException;
import common.exception.DBException;
import common.service.MailEngine;
import common.webapp.filter.FlashMap;
import common.webapp.util.ValidateUtil;

/**
 * 画面処理の基底クラス.
 */
public abstract class BaseController {

    /** ログ出力クラス */
    protected final transient Log log = LogFactory.getLog(getClass());

    /** メッセージソースアクセサー */
    protected MessageSourceAccessor messages;

    /** メールを送信するクラス */
    @Autowired(required = false)
    protected MailEngine mailEngine;

    /** Simple Mailメッセージモデル */
    @Autowired(required = false)
    protected SimpleMailMessage mailMessage;

    /**
     * 入力値を型変換するためのカスタムエディターを初期化する.
     *
     * @param binder
     *            データバインダ
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Integer.class, new CustomNumberEditor(Integer.class, true));
        binder.registerCustomEditor(Long.class, new CustomNumberEditor(Long.class, true));
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
        binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
    }

    /**
     * FlashMapにエラーメッセージを設定する.
     *
     * @param error
     *            エラーメッセージ
     */
    protected void saveFlashError(String error) {
        FlashMap.add("flash_error_messages", error);
    }

    /**
     * FlashMapに例外からのエラーメッセージを設定する.
     *
     * @param e
     *            データベース例外
     */
    protected void saveFlashError(DBException e) {
        saveFlashError(getText(e.getMessage()));
    }

    /**
     * FlashMapにメッセージを設定する.
     *
     * @param message
     *            メッセージ
     */
    protected void saveFlashMessage(String message) {
        FlashMap.add("flash_info_messages", message);
    }

    /**
     * エラーメッセージを設定する.
     *
     * @param error
     *            エラーメッセージ
     */
    protected void saveError(String error) {
        save("error_messages", error);
    }

    /**
     * 例外からエラーメッセージを設定する.
     *
     * @param e
     *            実行時例外
     */
    protected void saveError(RuntimeException e) {
        saveError(getText(e.getMessage()));
    }

    /**
     * メッセージを設定する.
     *
     * @param message
     *            メッセージ
     */
    protected void saveMessage(String message) {
        save("info_messages", message);
    }

    /**
     * Requestに要素を追加する.
     *
     * @param key
     *            キー
     * @param value
     *            要素
     */
    protected void save(String key, String value) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        @SuppressWarnings("unchecked")
        List<String> messages = (List<String>) request.getAttribute(key);

        if (messages == null) {
            messages = new ArrayList<String>();
        }

        messages.add(value);
        request.setAttribute(key, messages);
    }

    /**
     * 例外を入力値チェックに設定する.
     *
     * @param result
     *            入力値チェック結果
     * @param e
     *            データベース例外
     */
    protected void rejectValue(BindingResult result, DBException e) {
        if (e instanceof DBCheckException) {
            for (Map<String, Serializable> error : ((DBCheckException) e).getAllErrors()) {
                String filedName = (String) error.get(ValidateUtil.FILED_NAME);
                String massage = (String) error.get(ValidateUtil.MESSAGE);
                Object[] args = (Object[]) error.get(ValidateUtil.ARGS);

                if (filedName != null && filedName.length() > 0) {
                    result.rejectValue(filedName, massage, args, getText(massage));
                } else {
                    saveError(getText(massage, args));
                }
            }
        } else {
            saveError(e);
        }
    }

    /**
     * メッセージソースからメッセージを取得する.
     *
     * @param msgKey
     *            キー
     * @return メッセージ
     */
    protected String getText(String msgKey) {
        return messages.getMessage(msgKey);
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
    protected String getText(String msgKey, Object... arg) {
        return messages.getMessage(msgKey, arg);
    }

    /**
     * メッセージソースを設定する.
     *
     * @param messageSource
     *            メッセージソース
     */
    @Autowired
    public void setMessages(MessageSource messageSource) {
        messages = new MessageSourceAccessor(messageSource);
    }
}
