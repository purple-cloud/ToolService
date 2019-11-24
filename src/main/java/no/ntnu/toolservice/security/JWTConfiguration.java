package no.ntnu.toolservice.security;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class JWTConfiguration {
	@Value("${jwt.secret}")
	private String secret;
	@Value("${jwt.issuer}")
	private String issuer;
	@Value("#{new Integer('${jwt.token.timeout}')}")
	private int tokenTimeout;
	@Value("${jwt.token.prefix}")
	private String tokenPrefix;
}
