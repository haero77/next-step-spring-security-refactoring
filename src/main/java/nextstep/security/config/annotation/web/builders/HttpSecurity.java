package nextstep.security.config.annotation.web.builders;

import jakarta.servlet.Filter;
import nextstep.security.access.hierarchicalroles.RoleHierarchy;
import nextstep.security.authentication.AuthenticationManager;
import nextstep.security.authorization.AuthorizationFilter;
import nextstep.security.config.Customizer;
import nextstep.security.config.DefaultSecurityFilterChain;
import nextstep.security.config.SecurityFilterChain;
import nextstep.security.config.annotation.web.SecurityConfigurer;
import nextstep.security.config.annotation.web.configurers.*;
import nextstep.security.context.SecurityContextHolderFilter;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

import java.util.*;

public class HttpSecurity {

    private final LinkedHashMap<Class<? extends SecurityConfigurer>, SecurityConfigurer> configurers = new LinkedHashMap<>();
    private final List<Filter> filters = new ArrayList<>();
    private final Map<Class<?>, Object> sharedObjects = new HashMap<>();

    public HttpSecurity(AuthenticationManager authenticationManager, Map<Class<?>, Object> sharedObjects) {
        setSharedObject(AuthenticationManager.class, authenticationManager);
        this.sharedObjects.putAll(sharedObjects);
    }

    public <C> C getSharedObject(Class<C> sharedType) {
        return (C) this.sharedObjects.get(sharedType);
    }

    public <C> void setSharedObject(Class<C> sharedType, C object) {
        this.sharedObjects.put(sharedType, object);
    }

    public SecurityFilterChain build() {
        init();
        configure();
        return new DefaultSecurityFilterChain(orderFilters());
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

    public HttpSecurity csrf(Customizer<CsrfConfigurer> csrfCustomizer) {
        csrfCustomizer.customize(getOrApply(new CsrfConfigurer()));
        return this;
    }

    public HttpSecurity formLogin(Customizer<FormLoginConfigurer> formLoginCustomizer) {
        formLoginCustomizer.customize(getOrApply(new FormLoginConfigurer()));
        return this;
    }

    public HttpSecurity httpBasic(Customizer<HttpBasicConfigurer> httpBasicCustomizer) {
        httpBasicCustomizer.customize(getOrApply(new HttpBasicConfigurer()));
        return this;
    }

    public HttpSecurity oauth2Login(Customizer<OAuth2LoginConfigurer> oauth2LoginCustomizer) {
        oauth2LoginCustomizer.customize(getOrApply(new OAuth2LoginConfigurer()));
        return this;
    }

    public HttpSecurity authorizeHttpRequests(Customizer<AuthorizeHttpRequestsConfigurer> requestsCustomizer) {
        RoleHierarchy roleHierarchy = getSharedObject(ApplicationContext.class).getBean(RoleHierarchy.class);
        Assert.notNull(roleHierarchy, "roleHierarchy must not be null");
        requestsCustomizer.customize(getOrApply(new AuthorizeHttpRequestsConfigurer(roleHierarchy)));
        return this;
    }

    public HttpSecurity securityContext(Customizer<SecurityContextConfigurer> securityContextCustomizer) {
        securityContextCustomizer.customize(getOrApply(new SecurityContextConfigurer()));
        return this;
    }

    public void addFilter(Filter filter) {
        this.filters.add(filter);
    }

    private <C extends SecurityConfigurer> C getOrApply(C configurer) {
        Class<? extends SecurityConfigurer> clazz = configurer.getClass();

        SecurityConfigurer existingConfig = this.configurers.get(clazz);
        if (existingConfig != null) {
            return (C) existingConfig;
        }

        this.configurers.put(clazz, configurer);
        return configurer;
    }

    private List<Filter> orderFilters() {
        List<Filter> orderedFilters = new ArrayList<>(this.filters);

        // SecurityContext는 인증 전부터 관리되므로, SecurityContextHolder 필터를 인증 필터보다 앞에 위치.
        orderedFilters.stream()
                .filter(filter -> filter instanceof SecurityContextHolderFilter)
                .findFirst()
                .ifPresent(filter -> {
                    orderedFilters.remove(filter);
                    orderedFilters.add(0, filter);
                });

        // 인증 후에 인가를 실행해야하므로, 인가 필터를 가장 마지막에 위치
        orderedFilters.stream()
                .filter(filter -> filter instanceof AuthorizationFilter)
                .findFirst()
                .ifPresent(filter -> {
                    orderedFilters.remove(filter);
                    orderedFilters.add(filter);
                });

        return orderedFilters;
    }
}
