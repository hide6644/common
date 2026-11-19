package common.webapp.config;

import java.nio.charset.StandardCharsets;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;

import common.webapp.converter.util.CsvViewResolver;
import common.webapp.converter.util.JxlsViewResolver;
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;

/**
 * Spring MVCを設定するクラス.
 */
@Configuration
@EnableWebMvc
@ComponentScan("common.webapp.controller")
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * {@inheritDoc}
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/scripts/**").addResourceLocations("/scripts/");
        registry.addResourceHandler("/styles/**").addResourceLocations("/styles/");
    }

    /**
     * マルチパートリゾルバの設定.
     *
     * @return StandardServletMultipartResolver
     */
    @Bean
    public StandardServletMultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }

    /**
     * XML用のViewResolverの設定.
     *
     * @return BeanNameViewResolver
     */
    @Bean
    public BeanNameViewResolver beanNameViewResolver() {
        var resolver = new BeanNameViewResolver();
        resolver.setOrder(1);
        return resolver;
    }

    /**
     * Excel用のViewResolverの設定.
     *
     * @return JxlsViewResolver
     */
    @Bean
    public JxlsViewResolver jxlsViewResolver() {
        var resolver = new JxlsViewResolver();
        resolver.setPrefix("/WEB-INF/pages/");
        resolver.setSuffix(".xlsx");
        resolver.setOrder(2);
        return resolver;
    }

    /**
     * CSV用のViewResolverの設定.
     *
     * @return CsvViewResolver
     */
    @Bean
    public CsvViewResolver csvViewResolver() {
        var resolver = new CsvViewResolver();
        resolver.setPrefix("/WEB-INF/pages/");
        resolver.setSuffix(".csv");
        resolver.setOrder(3);
        return resolver;
    }

    /**
     * Thymeleaf用のTemplateResolverの設定.
     *
     * @return SpringResourceTemplateResolver
     */
    @Bean
    public SpringResourceTemplateResolver templateResolver() {
        var resolver = new SpringResourceTemplateResolver();
        resolver.setPrefix("/WEB-INF/pages/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCacheable(false);
        return resolver;
    }

    /**
     * Thymeleaf用のTemplateEngineの設定.
     *
     * @param templateResolver TemplateResolver
     * @return SpringTemplateEngine
     */
    @Bean
    public SpringTemplateEngine templateEngine(SpringResourceTemplateResolver templateResolver) {
        var engine = new SpringTemplateEngine();
        engine.setTemplateResolver(templateResolver);
        engine.setEnableSpringELCompiler(true);
        engine.setAdditionalDialects(Set.of(
                new SpringSecurityDialect(),
                new LayoutDialect()
        ));
        return engine;
    }

    /**
     * Thymeleaf用のViewResolverの設定.
     *
     * @param templateEngine TemplateEngine
     * @return ThymeleafViewResolver
     */
    @Bean
    public ThymeleafViewResolver thymeleafViewResolver(SpringTemplateEngine templateEngine) {
        var resolver = new ThymeleafViewResolver();
        resolver.setTemplateEngine(templateEngine);
        resolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resolver.setOrder(4);
        return resolver;
    }
}
