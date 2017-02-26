package common.webapp.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MockFilterChain implements FilterChain {

    private Logger log = LogManager.getLogger(getClass());

    private String forwardURL;

    public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        String uri = ((HttpServletRequest) request).getRequestURI();
        String requestContext = ((HttpServletRequest) request).getContextPath();

        if (StringUtils.isNotEmpty(requestContext) && uri.startsWith(requestContext)) {
            uri = uri.substring(requestContext.length());
        }

        this.forwardURL = uri;
        log.debug("Forwarding to: " + uri);

        RequestDispatcher dispatcher = request.getRequestDispatcher(uri);
        dispatcher.forward(request, response);
    }

    public String getForwardURL() {
        return this.forwardURL;
    }
}
