package nextstep.security.config.annotation.web;

import nextstep.security.config.annotation.web.builders.HttpSecurity;

public interface SecurityConfigurer {

    void init(HttpSecurity http);

    void configure(HttpSecurity http);
}
