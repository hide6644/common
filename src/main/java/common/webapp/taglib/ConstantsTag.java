package common.webapp.taglib;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import common.Constants;

/**
 * ConstantsクラスをTag Librariesとして提供するクラス.
 */
public class ConstantsTag extends TagSupport {

    /** 変数をJSPに公開するクラス */
    private String clazz = Constants.class.getName();

    /** JSPに公開する範囲 */
    protected String scope;

    /** JSPに公開する変数 */
    protected String var;

    /** JSPに公開する範囲を保持する定数 */
    private static final Map<String, Integer> SCOPES = new HashMap<String, Integer>();

    static {
        SCOPES.put("page", PageContext.PAGE_SCOPE);
        SCOPES.put("request", PageContext.REQUEST_SCOPE);
        SCOPES.put("session", PageContext.SESSION_SCOPE);
        SCOPES.put("application", PageContext.APPLICATION_SCOPE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int doStartTag() throws JspException {
        Class<?> c = null;
        int toScope = PageContext.PAGE_SCOPE;

        if (scope != null) {
            toScope = getScope(scope);
        }

        try {
            c = Class.forName(clazz);
        } catch (ClassNotFoundException cnf) {
            throw new JspException(cnf.getMessage());
        }

        try {
            if (var == null) {
                Field[] fields = c.getDeclaredFields();

                AccessibleObject.setAccessible(fields, true);

                for (Field field : fields) {
                    pageContext.setAttribute(field.getName(), field.get(this), toScope);
                }
            } else {
                try {
                    Object value = c.getField(var).get(this);
                    pageContext.setAttribute(c.getField(var).getName(), value, toScope);
                } catch (NoSuchFieldException nsf) {
                    throw new JspException(nsf);
                }
            }
        } catch (IllegalArgumentException iae) {
            throw new JspException(iae);
        } catch (IllegalAccessException iae) {
            throw new JspException(iae);
        }

        return SKIP_BODY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void release() {
        super.release();
        clazz = null;
        scope = null;
        var = null;
    }

    /**
     * JSPに公開する範囲をPageContext定値に変換する.
     *
     * @param scopeName
     *            JSPに公開する範囲
     * @return PageContext定値
     * @throws JspException
     *             {@link JspException}
     */
    public int getScope(String scopeName) throws JspException {
        Integer scope = (Integer) SCOPES.get(scopeName.toLowerCase());

        if (scope == null) {
            throw new JspException("Scope '" + scopeName + "' not a valid option");
        }

        return scope;
    }

    /**
     * 変数をJSPに公開するクラスを設定する.
     *
     * @param clazz
     *            変数をJSPに公開するクラス
     */
    public void setClassName(String clazz) {
        this.clazz = clazz;
    }

    /**
     * 変数をJSPに公開するクラスを取得する.
     *
     * @return 変数をJSPに公開するクラス
     */
    public String getClassName() {
        return clazz;
    }

    /**
     * JSPに公開する範囲を設定する.
     *
     * @param scope
     *            JSPに公開する範囲
     */
    public void setScope(String scope) {
        this.scope = scope;
    }

    /**
     * JSPに公開する範囲を取得する.
     *
     * @return JSPに公開する範囲
     */
    public String getScope() {
        return scope;
    }

    /**
     * JSPに公開する変数を設定する.
     *
     * @param var
     *            JSPに公開する変数
     */
    public void setVar(String var) {
        this.var = var;
    }

    /**
     * JSPに公開する変数を取得する.
     *
     * @return JSPに公開する変数
     */
    public String getVar() {
        return var;
    }
}
