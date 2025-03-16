package nextstep.security.config.annotation.web.configuration;

import nextstep.security.authentication.AuthenticationManager;
import nextstep.security.config.Customizer;
import nextstep.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpSecurityConfiguration {

    @Bean
    HttpSecurity httpSecurity(AuthenticationManager authenticationManager) {
        HttpSecurity http = new HttpSecurity(authenticationManager);

        http
                .securityContext(Customizer.withDefaults());

        return http;
    }
}
