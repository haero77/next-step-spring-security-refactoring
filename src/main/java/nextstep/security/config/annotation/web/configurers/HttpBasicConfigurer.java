package nextstep.security.config.annotation.web.configurers;

import nextstep.security.authentication.AuthenticationManager;
import nextstep.security.authentication.BasicAuthenticationFilter;
import nextstep.security.config.annotation.web.SecurityConfigurer;
import nextstep.security.config.annotation.web.builders.HttpSecurity;

public class HttpBasicConfigurer implements SecurityConfigurer {

    @Override
    public void init(HttpSecurity http) {
    }

    @Override
    public void configure(HttpSecurity http) {
        AuthenticationManager manager = http.getSharedObject(AuthenticationManager.class);
        BasicAuthenticationFilter filter = new BasicAuthenticationFilter(manager);
        http.addFilter(filter);
    }
}
