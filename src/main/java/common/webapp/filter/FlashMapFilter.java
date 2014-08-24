package common.webapp.filter;

import java.io.IOException;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.filter.OncePerRequestFilter;

/**
 * FlashMapの設定をするクラス.
 *
 * @author hide6644
 */
public class FlashMapFilter extends OncePerRequestFilter {

    /*
     * (非 Javadoc)
     *
     * @see org.springframework.web.filter.OncePerRequestFilter#doFilterInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, javax.servlet.FilterChain)
     */
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session != null) {
            @SuppressWarnings("unchecked")
            Map<String, Object> flash = (Map<String, Object>) session.getAttribute(FlashMap.FLASH_MAP_ATTRIBUTE);

            if (flash != null) {
                for (Map.Entry<String, Object> entry : flash.entrySet()) {
                    if (request.getAttribute(entry.getKey()) == null) {
                        request.setAttribute(entry.getKey(), entry.getValue());
                    }
                }

                session.removeAttribute(FlashMap.FLASH_MAP_ATTRIBUTE);
            }
        }

        filterChain.doFilter(request, response);
    }
}
