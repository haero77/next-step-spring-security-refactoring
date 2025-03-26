package nextstep.security.config.annotation.web.configuration;

import nextstep.security.authentication.AuthenticationManager;
import nextstep.security.config.Customizer;
import nextstep.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.Map;

@Configuration
public class HttpSecurityConfiguration {

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    HttpSecurity httpSecurity(
            ApplicationContext applicationContext,
            AuthenticationManager authenticationManager
    ) {
        HttpSecurity http = new HttpSecurity(authenticationManager, createSharedObjects(applicationContext));

        http
                .securityContext(Customizer.withDefaults());

        return http;
    }

    private Map<Class<?>, Object> createSharedObjects(ApplicationContext applicationContext) {
        return Map.of(ApplicationContext.class, applicationContext);
    }
}
