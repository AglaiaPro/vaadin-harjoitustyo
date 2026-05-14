package com.example.harjoitustyo.config;

import com.example.harjoitustyo.security.DatabaseUserDetailsService;
import com.example.harjoitustyo.security.OAuth2AccountService;
import com.example.harjoitustyo.view.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends VaadinWebSecurity {

    private final DatabaseUserDetailsService userDetailsService;
    private final OAuth2AccountService oAuth2AccountService;
    private final ClientRegistrationRepository clientRegistrationRepository;

    public SecurityConfig(DatabaseUserDetailsService userDetailsService,
                          OAuth2AccountService oAuth2AccountService,
                          @Autowired(required = false) ClientRegistrationRepository clientRegistrationRepository) {
        this.userDetailsService = userDetailsService;
        this.oAuth2AccountService = oAuth2AccountService;
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        new AntPathRequestMatcher("/images/**"),
                        new AntPathRequestMatcher("/uploads/**"),
                        new AntPathRequestMatcher("/h2-console/**")
                ).permitAll()
        );

        http.csrf(csrf -> csrf.ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**")));
        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));
        http.userDetailsService(userDetailsService);

        if (clientRegistrationRepository != null) {
            http.oauth2Login(oauth -> oauth
                    .loginPage("/login")
                    .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2AccountService))
                    .defaultSuccessUrl("/", true)
            );
        }

        super.configure(http);
        setLoginView(http, LoginView.class);
    }
}
