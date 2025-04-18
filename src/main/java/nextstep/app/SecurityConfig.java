package nextstep.app;

import nextstep.autoconfigure.EnableSpringBootSecurityConfiguration;
import nextstep.oauth2.OAuth2ClientProperties;
import nextstep.oauth2.authentication.OAuth2LoginAuthenticationProvider;
import nextstep.oauth2.registration.ClientRegistration;
import nextstep.oauth2.registration.ClientRegistrationRepository;
import nextstep.oauth2.userinfo.OAuth2UserService;
import nextstep.security.access.hierarchicalroles.RoleHierarchy;
import nextstep.security.access.hierarchicalroles.RoleHierarchyImpl;
import nextstep.security.authentication.AuthenticationManager;
import nextstep.security.authentication.DaoAuthenticationProvider;
import nextstep.security.authentication.ProviderManager;
import nextstep.security.authorization.SecuredMethodInterceptor;
import nextstep.security.config.Customizer;
import nextstep.security.config.DelegatingFilterProxy;
import nextstep.security.config.FilterChainProxy;
import nextstep.security.config.SecurityFilterChain;
import nextstep.security.config.annotation.web.builders.HttpSecurity;
import nextstep.security.config.annotation.web.configuration.EnableWebSecurity;
import nextstep.security.userdetails.UserDetailsService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableAspectJAutoProxy
@EnableConfigurationProperties(OAuth2ClientProperties.class)
@EnableWebSecurity
@EnableSpringBootSecurityConfiguration
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final OAuth2UserService oAuth2UserService;
    private final OAuth2ClientProperties oAuth2ClientProperties;

    public SecurityConfig(UserDetailsService userDetailsService, OAuth2UserService oAuth2UserService, OAuth2ClientProperties oAuth2ClientProperties) {
        this.userDetailsService = userDetailsService;
        this.oAuth2UserService = oAuth2UserService;
        this.oAuth2ClientProperties = oAuth2ClientProperties;
    }

    @Bean
    public DelegatingFilterProxy delegatingFilterProxy(HttpSecurity http) {
        return new DelegatingFilterProxy(filterChainProxy(List.of(securityFilterChain2(http))));
    }

    @Bean
    public FilterChainProxy filterChainProxy(List<SecurityFilterChain> securityFilterChains) {
        return new FilterChainProxy(securityFilterChains);
    }

    @Bean
    public SecuredMethodInterceptor securedMethodInterceptor() {
        return new SecuredMethodInterceptor();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(List.of(
                new DaoAuthenticationProvider(userDetailsService),
                new OAuth2LoginAuthenticationProvider(oAuth2UserService)));
    }

    @Bean
    public SecurityFilterChain securityFilterChain2(HttpSecurity http) {
        return http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/members").hasRole("ADMIN")
                        .requestMatchers("/members/me").authenticated()
                        .anyRequest().permitAll()
                )
                .csrf(c -> c.ignoringRequestMatchers("/login"))
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults())
                .oauth2Login(Customizer.withDefaults())
                .build();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.with()
                .role("ADMIN").implies("USER")
                .build();
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        Map<String, ClientRegistration> registrations = getClientRegistrations(oAuth2ClientProperties);
        return new ClientRegistrationRepository(registrations);
    }

    private static Map<String, ClientRegistration> getClientRegistrations(OAuth2ClientProperties properties) {
        Map<String, ClientRegistration> clientRegistrations = new HashMap<>();
        properties.getRegistration().forEach((key, value) -> clientRegistrations.put(key,
                getClientRegistration(key, value, properties.getProvider().get(key))));
        return clientRegistrations;
    }

    private static ClientRegistration getClientRegistration(String registrationId,
                                                            OAuth2ClientProperties.Registration registration, OAuth2ClientProperties.Provider provider) {
        return new ClientRegistration(registrationId, registration.getClientId(), registration.getClientSecret(), registration.getRedirectUri(), registration.getScope(), provider.getAuthorizationUri(), provider.getTokenUri(), provider.getUserInfoUri(), provider.getUserNameAttributeName());
    }
}

