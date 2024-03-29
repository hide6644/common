package common.webapp.filter;

import java.io.IOException;
import java.util.Map;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.filter.OncePerRequestFilter;

/**
 * FlashMapクラスをFilterChainに設定するクラス.
 */
public class FlashMapFilter extends OncePerRequestFilter {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session != null) {
            @SuppressWarnings("unchecked")
            Map<String, Object> flash = (Map<String, Object>) session.getAttribute(FlashMap.FLASH_MAP_ATTRIBUTE);

            if (flash != null) {
                flash.entrySet().stream().filter(entry -> request.getAttribute(entry.getKey()) == null)
                        .forEach(entry -> request.setAttribute(entry.getKey(), entry.getValue()));

                session.removeAttribute(FlashMap.FLASH_MAP_ATTRIBUTE);
            }
        }

        filterChain.doFilter(request, response);
    }
}
