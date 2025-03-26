package nextstep.security.config.annotation.web.configurers;

import nextstep.security.authentication.AuthenticationManager;
import nextstep.security.authentication.UsernamePasswordAuthenticationFilter;
import nextstep.security.config.annotation.web.SecurityConfigurer;
import nextstep.security.config.annotation.web.builders.HttpSecurity;

public class FormLoginConfigurer implements SecurityConfigurer {

    private AuthenticationManager authenticationManager;

    @Override
    public void init(HttpSecurity http) {
        this.authenticationManager = http.getSharedObject(AuthenticationManager.class);
    }

    @Override
    public void configure(HttpSecurity http) {
        http.addFilter(new UsernamePasswordAuthenticationFilter(this.authenticationManager));
    }
}
