package common.webapp.filter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

import jakarta.servlet.ServletException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@ExtendWith(MockitoExtension.class)
class ExtendedAuthenticationSuccessHandlerTest {

    @InjectMocks
    private ExtendedAuthenticationSuccessHandler filter = new ExtendedAuthenticationSuccessHandler();

    @InjectMocks
    private ExtendedAuthenticationSuccessHandler filter2 = new ExtendedAuthenticationSuccessHandler("/top");

    @Test
    void testOnAuthenticationFailure() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/");
        MockHttpServletResponse response = new MockHttpServletResponse();
        Authentication authentication = mock(Authentication.class);

        try {
            filter.onAuthenticationSuccess(request, response, authentication);
        } catch (IOException | ServletException e) {
            fail("Exception thrown");
        }

        request.addParameter(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY, "user");
        response = new MockHttpServletResponse();
        try {
            filter.onAuthenticationSuccess(request, response, authentication);
        } catch (IOException | ServletException e) {
            fail("Exception thrown");
        }
    }
}
