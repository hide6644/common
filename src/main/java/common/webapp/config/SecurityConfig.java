package common.webapp.config;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import common.webapp.filter.ExtendedAuthenticationFailureHandler;
import common.webapp.filter.ExtendedAuthenticationSuccessHandler;

/**
 * Spring Securityを設定するクラス.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /** ユーザー固有のデータを読み込むインターフェイス */
    private final UserDetailsService userDetailsService;

    /**
     * コンストラクタ.
     * 
     * @param userDetailsService ユーザー固有のデータ
     */
    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * セキュリティの設定.
     * 
     * @param http Webベースのセキュリティを構成するクラス
     * @return FilterChainProxy用のSecurityFilterChain
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/images/**", "/scripts/**", "/styles/**",
                                 "/error", "/login*/**", "/requestRecoveryToken*", 
                                 "/updatePassword*", "/signup*").permitAll()
                .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(authenticationSuccessHandler())
                .failureHandler(authenticationFailureHandler())
                .permitAll()
            )
            .rememberMe(remember -> remember
                .userDetailsService(userDetailsService)
                .key("f2bf2397-4e60-4b7a-8e76-5fddd35a6be9")
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID", "f2bf2397-4e60-4b7a-8e76-5fddd35a6be9")
            );

        return http.build();
    }

    /**
     * パスワードエンコードの設定.
     * 
     * @return PasswordEncoderの実装
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * トークンのエンコードの設定.
     * 
     * @return BCryptPasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordTokenEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * AuthenticationSuccessHandlerの独自拡張.
     * 
     * @return ExtendedAuthenticationSuccessHandler
     */
    AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new ExtendedAuthenticationSuccessHandler("/top");
    }

    /**
     * AuthenticationFailureHandlerの独自拡張.
     * 
     * @return ExtendedAuthenticationFailureHandler
     */
    AuthenticationFailureHandler authenticationFailureHandler() {
        // 例外マッピングの設定
        Properties mappings = new Properties();
        mappings.put(DisabledException.class.getName(), "/login/accountDisabled");
        mappings.put(LockedException.class.getName(), "/login/accountLocked");
        mappings.put(AccountExpiredException.class.getName(), "/login/accountExpired");
        mappings.put(CredentialsExpiredException.class.getName(), "/login/credentialsExpired");
        mappings.put(BadCredentialsException.class.getName(), "/login/badCredentials");

        ExtendedAuthenticationFailureHandler handler = new ExtendedAuthenticationFailureHandler();
        handler.setExceptionMappings(mappings);
        return handler;
    }
}
