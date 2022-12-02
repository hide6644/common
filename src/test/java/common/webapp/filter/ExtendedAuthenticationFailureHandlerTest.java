package common.webapp.filter;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.ServletException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import common.Constants;
import common.service.UserManager;

@ExtendWith(MockitoExtension.class)
class ExtendedAuthenticationFailureHandlerTest {

    @Mock
    private UserManager userManager;

    @InjectMocks
    private ExtendedAuthenticationFailureHandler filter = new ExtendedAuthenticationFailureHandler();

    @Test
    void testOnAuthenticationFailure() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/");
        MockHttpServletResponse response = new MockHttpServletResponse();
        BadCredentialsException badCredentialsException = new BadCredentialsException("");
        CredentialsExpiredException credentialsExpiredException = new CredentialsExpiredException("");

        try {
            filter.onAuthenticationFailure(request, response, badCredentialsException);
        } catch (IOException | ServletException e) {
            fail("Exception thrown");
        }

        Map<String, String> failureUrlMap = new HashMap<>();
        failureUrlMap.put("org.springframework.security.authentication.BadCredentialsException", "/login/badCredentials");
        failureUrlMap.put("org.springframework.security.authentication.CredentialsExpiredException", "/login/credentialsExpired");
        filter.setExceptionMappings(failureUrlMap);

        response = new MockHttpServletResponse();
        try {
            filter.onAuthenticationFailure(request, response, badCredentialsException);
        } catch (IOException | ServletException e) {
            fail("Exception thrown");
        }
        response = new MockHttpServletResponse();
        try {
            filter.onAuthenticationFailure(request, response, credentialsExpiredException);
        } catch (IOException | ServletException e) {
            fail("Exception thrown");
        }

        request.addParameter(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY, "user");

        for (int i = 0; i <= Constants.LOGIN_FAILURE_UPPER_LIMIT; i++) {
            response = new MockHttpServletResponse();
            try {
                filter.onAuthenticationFailure(request, response, badCredentialsException);
            } catch (IOException | ServletException e) {
                fail("Exception thrown");
            }
        }
    }
}
