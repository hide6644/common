package common.webapp.listener;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;

import common.Constants;
import common.model.LabelValue;
import common.model.User;
import common.service.GenericManager;
import common.service.RoleManager;
import common.webapp.converter.FileConverterStrategy;

/**
 * アプリケーション初期化処理を実行するクラス.
 *
 * @author hide6644
 */
@Component
public class StartupListener implements ServletContextListener {

    private static final Log log = LogFactory.getLog(StartupListener.class);

    /*
     * (非 Javadoc)
     *
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent event) {
        ServletContext context = event.getServletContext();

        // Orion starts Servlets before Listeners, so check if the config
        // object already exists
        @SuppressWarnings("unchecked")
        Map<String, Object> config = (HashMap<String, Object>) context.getAttribute(Constants.CONFIG);

        if (config == null) {
            config = new HashMap<String, Object>();
        }

        context.setAttribute(Constants.CONFIG, config);

        setupContext(context);

        // Determine version number for CSS and JS Assets
        String appVersion = null;
        try {
            InputStream is = context.getResourceAsStream("/META-INF/MANIFEST.MF");
            if (is == null) {
                log.warn("META-INF/MANIFEST.MF not found.");
            } else {
                Manifest mf = new Manifest();
                mf.read(is);
                Attributes atts = mf.getMainAttributes();
                appVersion = atts.getValue("Implementation-Version");
            }
        } catch (IOException e) {
            log.error("I/O Exception reading manifest: " + e.getMessage());
        }

        // If there was a build number defined in the war, then use it for
        // the cache buster. Otherwise, assume we are in development mode
        // and use a random cache buster so developers don't have to clear
        // their browser cache.
        if (appVersion == null || appVersion.contains("SNAPSHOT")) {
            appVersion = "" + new Random().nextInt(100000);
        }

        log.info("Application version set to: " + appVersion);
        context.setAttribute(Constants.ASSETS_VERSION, appVersion);
    }

    /*
     * (非 Javadoc)
     *
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        LogFactory.release(Thread.currentThread().getContextClassLoader());
    }

    /**
     * 初期化する.
     *
     * @param sc
     *            {@link ServletContext}
     */
    public static void setupContext(ServletContext context) {
        List<LabelValue> fileTypeList = new ArrayList<LabelValue>();
        fileTypeList.add(new LabelValue(ResourceBundle.getBundle(Constants.BUNDLE_KEY).getString("fileType.xml"), FileConverterStrategy.FILE_TYPE_XML));
        fileTypeList.add(new LabelValue(ResourceBundle.getBundle(Constants.BUNDLE_KEY).getString("fileType.xls"), FileConverterStrategy.FILE_TYPE_XLS));
        fileTypeList.add(new LabelValue(ResourceBundle.getBundle(Constants.BUNDLE_KEY).getString("fileType.csv"), FileConverterStrategy.FILE_TYPE_CSV));
        context.setAttribute("fileTypeList", fileTypeList);

        ApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(context);

        RoleManager mgr = (RoleManager) ctx.getBean("roleManager");
        // get list of possible roles
        context.setAttribute(Constants.AVAILABLE_ROLES, mgr.getLabelValues());

        // Any manager extending GenericManager will do:
        @SuppressWarnings("unchecked")
        GenericManager<User, Long> manager = (GenericManager<User, Long>) ctx.getBean("userManager");
        manager.reindexAll(false);
    }
}
