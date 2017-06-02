package de.roamingthings.auth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;

/**
 * @author Alexander Sparkowsky [info@roamingthings.de]
 * @version 2017/05/22
 */
@Configuration
@EnableResourceServer
public class OAuth2ResourceServerConfig extends ResourceServerConfigurerAdapter {
    private static final String RESOURCE_ID = "my_rest_api";

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.resourceId(RESOURCE_ID).stateless(false);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and()
//                .anonymous().disable()
                .requestMatchers()
                .antMatchers("/me")
                .and()
                .authorizeRequests()
                .antMatchers("/me").hasRole("USER")
                .and().exceptionHandling().accessDeniedHandler(new OAuth2AccessDeniedHandler());
//                .antMatchers("/user_account").hasRole("USER");

//                .anyRequest().denyAll();
//        http.requestMatchers().antMatchers("/me").and().authorizeRequests().antMatchers("/me").access("#oauth2.hasScope('read')");

    }

}