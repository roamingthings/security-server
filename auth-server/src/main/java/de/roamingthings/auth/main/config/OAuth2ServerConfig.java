package de.roamingthings.auth.main.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.*;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import javax.sql.DataSource;
import java.util.Arrays;

/**
 * @author Alexander Sparkowsky [info@roamingthings.de]
 * @version 2017/05/31
 */
@Configuration
public class OAuth2ServerConfig {

    @Configuration
    @EnableResourceServer
    protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

        @Override
        public void configure(ResourceServerSecurityConfigurer resources) {
            resources.resourceId("rt").stateless(false);
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {
            http
                    // Since we want the protected resources to be accessible in the UI as well we need
                    // session creation to be allowed (it's disabled by default in 2.0.6)
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and()
                    .requestMatchers().antMatchers("/user","/me")
                .and()
                    .authorizeRequests()
                    .antMatchers("/me").access("#oauth2.hasScope('read')")
//                    .antMatchers("/user").access("#oauth2.hasScope('read')")
                    .antMatchers("/user").access("hasRole('ROLE_USER')");
        }

        @Configuration
        @EnableAuthorizationServer
        protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {
            @Value("${oauth.accessToken.expirationTimeInSeconds:86400}")
            private int accessTokenExpirationTimeInSeconds;

            @Value("${oauth.refreshToken.expirationTimeInSeconds:2592000}")
            private int refreshTokenExpirationTimeInSeconds;

            @Value("${oauth.keyStore.password:01Ym/jdXXO0n80Y8zt7Nm9zIXhBrt5TO}")
            private String tokenKeyStorePassword;

//            @Autowired
//            private TokenStore tokenStore;

            @Autowired
            private UserApprovalHandler userApprovalHandler;

            @Autowired
            @Qualifier("authenticationManagerBean")
            private AuthenticationManager authenticationManager;

            @Autowired
            private DataSource dataSource;

            @Autowired
            private AccessTokenEnhancer accessTokenEnhancer;

            @Override
            public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
                clients.jdbc(dataSource);
            }

            @Override
            public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
//                endpoints.tokenStore(tokenStore).userApprovalHandler(userApprovalHandler)
//                        .authenticationManager(authenticationManager);
                endpoints.tokenServices(tokenServices())
                        .authenticationManager(authenticationManager);

            }

            @Bean
            public TokenStore tokenStore() {
                return new JdbcTokenStore(dataSource);
            }

            @Bean
            @Primary
            public AuthorizationServerTokenServices tokenServices() {
                final DefaultTokenServices tokenServices = new DefaultTokenServices();
                tokenServices.setTokenStore(tokenStore());
                tokenServices.setAccessTokenValiditySeconds(accessTokenExpirationTimeInSeconds);
                tokenServices.setRefreshTokenValiditySeconds(refreshTokenExpirationTimeInSeconds);
                tokenServices.setTokenEnhancer(tokenEnhancerChain());
                tokenServices.setSupportRefreshToken(true);
                tokenServices.setReuseRefreshToken(false);

                return tokenServices;
            }

            @Override
            public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
                oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
            }

            @Bean
            protected JwtAccessTokenConverter jwtTokenEnhancer() {
                final JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
                final KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("jwt.jks"), tokenKeyStorePassword.toCharArray());
                converter.setKeyPair(keyStoreKeyFactory.getKeyPair("jwt"));
                return converter;
            }

/*
    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();

        Resource resource = new ClassPathResource("jwt.pub");

        String publicKey;
        try {
            publicKey = new String(Files.readAllBytes(resource.getFile().toPath()), Charset.forName("UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException("Unable to load public key for jwt verification");
        }

        converter.setVerifierKey(publicKey);
        return converter;
    }
*/

            @Bean
            public TokenEnhancerChain tokenEnhancerChain() {
                final TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
                tokenEnhancerChain.setTokenEnhancers(Arrays.asList(accessTokenEnhancer, jwtTokenEnhancer()));
                return tokenEnhancerChain;
            }


            // @Autowired
            // public void init(AuthenticationManagerBuilder auth) throws Exception {
//		auth.jdbcAuthentication().dataSource(dataSource());
            // }

    /*
    @Bean
    public TokenStore tokenStore() {
    return new JwtTokenStore(accessTokenConverter());
    }
    */

            @Bean
            public TokenEnhancer tokenEnhancer() {
                return accessTokenEnhancer;
            }

        }

    }

    protected static class Stuff {

        @Autowired
        private ClientDetailsService clientDetailsService;

        @Autowired
        private TokenStore tokenStore;

        @Bean
        public ApprovalStore approvalStore() throws Exception {
            TokenApprovalStore store = new TokenApprovalStore();
            store.setTokenStore(tokenStore);
            return store;
        }

        @Bean
        @Lazy
        @Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
        public RTUserApprovalHandler userApprovalHandler() throws Exception {
            RTUserApprovalHandler handler = new RTUserApprovalHandler();
            handler.setApprovalStore(approvalStore());
            handler.setRequestFactory(new DefaultOAuth2RequestFactory(clientDetailsService));
            handler.setClientDetailsService(clientDetailsService);
            handler.setUseApprovalStore(true);
            return handler;
        }
    }

}
