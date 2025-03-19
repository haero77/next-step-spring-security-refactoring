package nextstep.autoconfigure;

import nextstep.security.config.Customizer;
import nextstep.security.config.SecurityFilterChain;
import nextstep.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

// 클래스에 선언된 빈 생성 메서드 간 빈 의존성이 없으므로 굳이 프록시 객체를 생성하지 않기 위해 proxyBeanMethods = false 옵션 사용
@Configuration(proxyBeanMethods = false)
public class SpringBootWebSecurityConfiguration {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnMissingBean(SecurityFilterChain.class)
    static class SecurityFilterChainConfiguration {

        @Bean
        @Order(SecurityProperties.BASIC_AUTH_ORDER)
        SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) {
            return http
                    .authorizeHttpRequests((requests) -> requests.anyRequest().authenticated())
                    .formLogin(Customizer.withDefaults())
                    .httpBasic(Customizer.withDefaults())
                    .build();
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class WebSecurityEnablerConfiguration {

    }
}
