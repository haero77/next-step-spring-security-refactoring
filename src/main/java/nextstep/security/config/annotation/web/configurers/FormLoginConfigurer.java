package nextstep.security.config.annotation.web.configurers;

import nextstep.security.authentication.AuthenticationManager;
import nextstep.security.authentication.UsernamePasswordAuthenticationFilter;
import nextstep.security.config.annotation.web.SecurityConfigurer;
import nextstep.security.config.annotation.web.builders.HttpSecurity;

public class FormLoginConfigurer implements SecurityConfigurer {

    @Override
    public void init(HttpSecurity http) {
    }

    @Override
    public void configure(HttpSecurity http) {
        AuthenticationManager manager = http.getSharedObject(AuthenticationManager.class);
        UsernamePasswordAuthenticationFilter filter = new UsernamePasswordAuthenticationFilter(manager);
        http.addFilter(filter);
    }
}
