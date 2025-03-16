package nextstep.security.config.annotation.web.configurers;

import nextstep.oauth2.registration.ClientRegistrationRepository;
import nextstep.oauth2.web.OAuth2AuthorizationRequestRedirectFilter;
import nextstep.oauth2.web.OAuth2AuthorizedClientRepository;
import nextstep.oauth2.web.OAuth2LoginAuthenticationFilter;
import nextstep.security.authentication.AuthenticationManager;
import nextstep.security.config.annotation.web.SecurityConfigurer;
import nextstep.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.context.ApplicationContext;

public class OAuth2LoginConfigurer implements SecurityConfigurer {

    private AuthenticationManager authenticationManager;
    private ClientRegistrationRepository clientRegistrationRepository;
    private OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository;

    @Override
    public void init(HttpSecurity http) {
        this.authenticationManager = http.getSharedObject(AuthenticationManager.class);
        this.clientRegistrationRepository = http.getSharedObject(ApplicationContext.class).getBean(ClientRegistrationRepository.class);
        this.oAuth2AuthorizedClientRepository = new OAuth2AuthorizedClientRepository();
    }

    @Override
    public void configure(HttpSecurity http) {
        OAuth2AuthorizationRequestRedirectFilter redirectFilter = new OAuth2AuthorizationRequestRedirectFilter(clientRegistrationRepository);
        http.addFilter(redirectFilter);

        OAuth2LoginAuthenticationFilter authenticationFilter = new OAuth2LoginAuthenticationFilter(
                clientRegistrationRepository,
                oAuth2AuthorizedClientRepository,
                authenticationManager
        );
        http.addFilter(authenticationFilter);
    }
}
