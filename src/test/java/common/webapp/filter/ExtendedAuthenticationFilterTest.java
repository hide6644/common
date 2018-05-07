package common.webapp.filter;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import common.Constants;
import common.model.User;
import common.service.UserManager;

@RunWith(MockitoJUnitRunner.class)
public class ExtendedAuthenticationFilterTest {

    @Mock
    private UserManager userManager;

    @InjectMocks
    private ExtendedAuthenticationFilter filter = new ExtendedAuthenticationFilter();

    @Test
    public void testNormalOperation() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/");
        request.addParameter(
                UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY,
                "user");
        request.addParameter(
                UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_PASSWORD_KEY,
                "pass");
        request.setSession(new MockHttpSession());

        filter.setAuthenticationManager(createAuthenticationManager());

        Authentication result = filter.attemptAuthentication(request, new MockHttpServletResponse());
        assertNotNull(result);
        assertEquals("127.0.0.1", ((WebAuthenticationDetails) result.getDetails()).getRemoteAddress());
    }

    @Test
    public void testAuthenticationServiceException() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/");
        request.setSession(new MockHttpSession());

        filter.setAuthenticationManager(createAuthenticationManager());

        try {
            filter.attemptAuthentication(request, new MockHttpServletResponse());
            fail("Expected AuthenticationException");
        } catch (AuthenticationException e) {
        }
    }

    @Test
    public void testFailedAuthenticationThrowsException() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/");
        request.addParameter(
                UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY,
                "user");
        request.setSession(new MockHttpSession());

        AuthenticationManager am = mock(AuthenticationManager.class);
        when(am.authenticate(any(Authentication.class))).thenThrow(new BadCredentialsException(""));
        filter.setAuthenticationManager(am);

        given(userManager.getUserByUsername("user")).willReturn(new User("user"));

        for (int i = 0; i <= Constants.LOGIN_FAILURE_UPPER_LIMIT; i++) {
            try {
                filter.attemptAuthentication(request, new MockHttpServletResponse());
                fail("Expected AuthenticationException");
            } catch (AuthenticationException e) {
            }
        }
    }

    @Test
    public void testFailedAuthenticationUsernameNotFound() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/");
        request.setSession(new MockHttpSession());

        AuthenticationManager am = mock(AuthenticationManager.class);
        when(am.authenticate(any(Authentication.class))).thenThrow(new BadCredentialsException(""));
        filter.setAuthenticationManager(am);

        when(userManager.getUserByUsername("")).thenThrow(new UsernameNotFoundException(""));

        for (int i = 0; i <= Constants.LOGIN_FAILURE_UPPER_LIMIT; i++) {
            try {
                filter.attemptAuthentication(request, new MockHttpServletResponse());
                fail("Expected AuthenticationException");
            } catch (AuthenticationException e) {
            }
        }
    }

    private AuthenticationManager createAuthenticationManager() {
        AuthenticationManager am = mock(AuthenticationManager.class);
        when(am.authenticate(any(Authentication.class))).thenAnswer(
                new Answer<Authentication>() {
                    public Authentication answer(InvocationOnMock invocation) throws Throwable {
                        return (Authentication) invocation.getArguments()[0];
                    }
                });

        return am;
    }
}
