package nextstep.security.config.annotation.web.configurers;

import nextstep.security.authentication.AuthenticationManager;
import nextstep.security.authentication.BasicAuthenticationFilter;
import nextstep.security.config.annotation.web.SecurityConfigurer;
import nextstep.security.config.annotation.web.builders.HttpSecurity;

public class HttpBasicConfigurer implements SecurityConfigurer {

    private AuthenticationManager authenticationManager;

    @Override
    public void init(HttpSecurity http) {
        this.authenticationManager = http.getSharedObject(AuthenticationManager.class);
    }

    @Override
    public void configure(HttpSecurity http) {
        BasicAuthenticationFilter filter = new BasicAuthenticationFilter(this.authenticationManager);
        http.addFilter(filter);
    }
}
