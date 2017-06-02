package de.roamingthings.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * @author Alexander Sparkowsky [info@roamingthings.de]
 * @version 2017/04/29
 */
@Configuration
@EnableWebSecurity(debug = true)
//@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
//@Order(Ordered.HIGHEST_PRECEDENCE)
//@Order(-2)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private AuthenticationProvider authenticationProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    @Qualifier("daoAuthenticationProvider")
    public void setAuthenticationProvider(AuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/webjars/**", "/images/**", "/vendor/**", "/assets/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.requestMatchers().antMatchers("/login", "/oauth/**", "/user_account")
            .and()
//            .anonymous().disable()
            .authorizeRequests()
                .antMatchers("/login").permitAll()
                .antMatchers("/oauth/**").permitAll()
                .antMatchers("/user_account").hasRole("USER")
            .and()
                .exceptionHandling()
                .accessDeniedPage("/login?authorization_error=true")
            .and()
                // TODO: put CSRF protection back into this endpoint
                .csrf()
                .requireCsrfProtectionMatcher(new AntPathRequestMatcher("/oauth/authorize"))
                .requireCsrfProtectionMatcher(new AntPathRequestMatcher("/console/**"))
                .disable()
            .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
            .and()
                .formLogin()
                .loginProcessingUrl("/login")
                .failureUrl("/login?authentication_error=true")
                .loginPage("/login");
//            .and().headers().frameOptions().sameOrigin();


//        http
//            .authorizeRequests()
//                .antMatchers("/console/**").permitAll()
//                .antMatchers("/webjars/**", "/assets/**", "/vendor/**").permitAll()
//                .anyRequest().authenticated()
//            .and()
//                .formLogin()
//                .loginPage("/login")
//                .permitAll()
//            .and()
//                .logout()
//                .permitAll()
//            .and().csrf().ignoringAntMatchers("/console/**")
//            .and().headers().frameOptions().sameOrigin();
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }


    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(PasswordEncoder passwordEncoder,
                                                               UserDetailsService userDetailsService){

        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        return daoAuthenticationProvider;
    }

    @Autowired
    public void configureAuthManager(AuthenticationManagerBuilder authenticationManagerBuilder){
        authenticationManagerBuilder.authenticationProvider(authenticationProvider);
    }
}
