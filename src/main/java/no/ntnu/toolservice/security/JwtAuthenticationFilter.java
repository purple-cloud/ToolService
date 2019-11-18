package no.ntnu.toolservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.security.Key;
import java.util.Date;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	private String secret = "keepthissecret";
	private String issuer = "toolservice";
	private int tokenTimeout = 30;
	private String tokenPrefix = "Bearer ";

	private AuthenticationManager authManager;

	@Autowired
	public JwtAuthenticationFilter(AuthenticationManager authManager) {
		this.authManager = authManager;
	}

	@Override   // Filter for when client tries to login
	public Authentication attemptAuthentication(HttpServletRequest request,
	                                            HttpServletResponse response) throws AuthenticationException {
		UsernamePasswordAuthenticationToken authToken = null;

		try {
			// Get the credentials from input stream and convert it to credential container
			LoginViewModel credentials = new ObjectMapper().readValue(request.getInputStream(), LoginViewModel.class);

			// Create login token
			authToken = new UsernamePasswordAuthenticationToken(
					credentials.getUsername(),
					credentials.getPassword()
			);
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}

		// Authenticate user
		return authManager.authenticate(authToken);

	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request,
	                                        HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
		// Grab user credentials provided from the method above
		UserPrincipal principal = (UserPrincipal) authResult.getPrincipal();
		JSONObject employeeJson = new JSONObject(principal.getUser());

		SignatureAlgorithm alg = SignatureAlgorithm.HS512;
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secret);
		Key signingKey = new SecretKeySpec(apiKeySecretBytes, alg.getJcaName());

		// Create token
		String token = Jwts.builder()
				.setIssuer(issuer)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + (tokenTimeout * 1000)))
				.setSubject(principal.getUsername())
				.claim("employee", principal.getUser())
				.signWith(alg, signingKey)
				.compact();

		// Add token in response
		response.addHeader("Authorization", tokenPrefix + token);
	}
}
