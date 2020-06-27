package ru.alfabattle.borisov.atms.config;

import org.apache.catalina.connector.Connector;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.security.KeyStore;

@Configuration
public class RestTemplateConfig {

    @Value("${server.ssl.key-store-password}")
    private String keyStorePass;

    @Value("${http.port}")
    private int httpPort;

    @Bean
    public RestTemplate getRestTemplate(@Autowired Environment env) throws Exception {
        RestTemplate restTemplate = new RestTemplate();

        KeyStore keyStore = KeyStore.getInstance(env.getProperty("server.ssl.key-store-type"));
        ClassPathResource keystoreResource = new ClassPathResource("keystore");
        keyStore.load(keystoreResource.getInputStream(), keyStorePass.toCharArray());

        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(new SSLContextBuilder()
                .loadTrustMaterial(new TrustSelfSignedStrategy())
                .loadKeyMaterial(keyStore, keyStorePass.toCharArray()).build(),
                NoopHostnameVerifier.INSTANCE);

        HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory)
                .setMaxConnTotal(5)
                .setMaxConnPerRoute(5)
                .build();

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(10000);
        requestFactory.setConnectTimeout(10000);

        restTemplate.setRequestFactory(requestFactory);

        return restTemplate;
    }

    @Bean
    public ServletWebServerFactory servletContainer() {
        Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
        connector.setPort(httpPort);

        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        tomcat.addAdditionalTomcatConnectors(connector);
        return tomcat;
    }
}
