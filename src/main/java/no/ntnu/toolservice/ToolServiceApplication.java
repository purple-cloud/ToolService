package no.ntnu.toolservice;

import no.ntnu.toolservice.files.StorageProperties;
import no.ntnu.toolservice.files.StorageService;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class ToolServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ToolServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner init(StorageService storageService) {
        return (args) -> {
            storageService.init();
        };
    }

    @Bean
    public ServletWebServerFactory servletContainer() {
        // Enable SSL Traffic
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                SecurityConstraint sConstraint = new SecurityConstraint();
                sConstraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection sCollection = new SecurityCollection();
                sCollection.addPattern("/*");
                sConstraint.addCollection(sCollection);
                context.addConstraint(sConstraint);
            }
        };

        tomcat.addAdditionalTomcatConnectors(httpToHttpsRedirectConnector());
        return tomcat;
    }

    private Connector httpToHttpsRedirectConnector() {
        Connector con = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
        con.setScheme("http");
        con.setPort(8080);
        con.setSecure(false);
        con.setRedirectPort(8443);
        return con;
    }

}
