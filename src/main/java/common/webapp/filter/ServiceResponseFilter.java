package common.webapp.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

/**
 * ダウンロードダイアログを表示するクラス.
 */
public class ServiceResponseFilter extends OncePerRequestFilter {

    /**
     * {@inheritDoc}
     */
    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String requestURI = request.getRequestURI();

        if (requestURI.indexOf(".csv") > 0) {
            response.setContentType("Application/Octet-Stream");
            response.setHeader("Content-Disposition", "attachment;filename=\"" + requestURI.substring(requestURI.lastIndexOf("/") + 1) + "\"");
        } else if (requestURI.indexOf(".xlsx") > 0) {
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=\"" + requestURI.substring(requestURI.lastIndexOf("/") + 1) + "\"");
        } else if (requestURI.indexOf(".xml") > 0) {
            response.setContentType("Application/Octet-Stream");
            response.setHeader("Content-Disposition", "attachment;filename=\"" + requestURI.substring(requestURI.lastIndexOf("/") + 1) + "\"");
        }

        chain.doFilter(request, response);
    }
}
