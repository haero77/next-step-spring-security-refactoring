package nextstep.security.config.annotation.web.builders;

import jakarta.servlet.Filter;
import nextstep.security.config.DefaultSecurityFilterChain;
import nextstep.security.config.SecurityFilterChain;
import nextstep.security.config.annotation.web.SecurityConfigurer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class HttpSecurity {

    private final LinkedHashMap<Class<? extends SecurityConfigurer>, SecurityConfigurer> configurers = new LinkedHashMap<>();
    private final List<Filter> filters = new ArrayList<>();

    public SecurityFilterChain build() {
        init();
        configure();
        return new DefaultSecurityFilterChain(filters);
    }

    private void init() {
        for (SecurityConfigurer configurer : this.configurers.values()) {
            configurer.init(this);
        }
    }

    private void configure() {
        for (SecurityConfigurer configurer : this.configurers.values()) {
            configurer.configure(this);
        }
    }

    public HttpSecurity csrf() {
        return this;
    }

    public HttpSecurity httpBasic() {
        return this;
    }

    public HttpSecurity formLogin() {
        return this;
    }

    public HttpSecurity authorizeHttpRequests() {
        return this;
    }

    private SecurityConfigurer getOrApply(SecurityConfigurer configurer) {
        Class<? extends SecurityConfigurer> clazz = configurer.getClass();

        SecurityConfigurer existingConfig = this.configurers.get(clazz);
        if (existingConfig != null) {
            return existingConfig;
        }

        this.configurers.put(clazz, configurer);
        return configurer;
    }
}
