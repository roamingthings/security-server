package de.roamingthings.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.*;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import javax.sql.DataSource;
import java.util.Arrays;

/**
 * @author Alexander Sparkowsky [info@roamingthings.de]
 * @version 2017/05/22
 */
@Configuration
@EnableAuthorizationServer
public class OAuth2AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    @Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private AccessTokenEnhancer accessTokenEnhancer;

    @Value("${oauth.accessToken.expirationTimeInSeconds:86400}")
    private int accessTokenExpirationTimeInSeconds;

    @Value("${oauth.refreshToken.expirationTimeInSeconds:2592000}")
    private int refreshTokenExpirationTimeInSeconds;

    @Value("${oauth.keyStore.password:01Ym/jdXXO0n80Y8zt7Nm9zIXhBrt5TO}")
    private String tokenKeyStorePassword;


//    @Override
//    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
//        oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
//    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.jdbc(dataSource);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.tokenServices(tokenServices())
                .authenticationManager(authenticationManager);
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

/*
    @Bean
    public TokenStore tokenStore() {
    return new JwtTokenStore(accessTokenConverter());
    }
*/

    @Bean
    public TokenEnhancerChain tokenEnhancerChain() {
        final TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer(), jwtTokenEnhancer()));
        return tokenEnhancerChain;
    }


    // @Autowired
    // public void init(AuthenticationManagerBuilder auth) throws Exception {
//		auth.jdbcAuthentication().dataSource(dataSource());
    // }


    @Bean
    public TokenEnhancer tokenEnhancer() {
        return accessTokenEnhancer;
    }

    @Bean
    public TokenStore tokenStore() {
        return new JdbcTokenStore(dataSource);
    }
}