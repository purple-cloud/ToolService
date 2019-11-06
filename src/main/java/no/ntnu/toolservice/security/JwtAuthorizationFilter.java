package no.ntnu.toolservice.security;

import io.jsonwebtoken.*;
import no.ntnu.toolservice.entity.Employee;
import no.ntnu.toolservice.repository.EmployeeRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.security.Key;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
	private EmployeeRepository eRepo;

	public JwtAuthorizationFilter(AuthenticationManager authenticationManager, EmployeeRepository eRepo) {
		super(authenticationManager);
		this.eRepo = eRepo;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
	                                FilterChain chain) throws IOException, ServletException {
		String header = request.getHeader("Authorization");

		if (header == null || !header.startsWith("Bearer ")) {
			chain.doFilter(request, response);
			return;
		}

		Authentication authentication = getUsernamePasswordAuthentication(request);
		SecurityContextHolder.getContext().setAuthentication(authentication);


		chain.doFilter(request, response);
	}

	private Authentication getUsernamePasswordAuthentication(HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		UsernamePasswordAuthenticationToken auth = null;

		try {
			if (token != null) {
				SignatureAlgorithm alg = SignatureAlgorithm.HS512;
				byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary("keepthissecret");
				Key signingKey = new SecretKeySpec(apiKeySecretBytes, alg.getJcaName());

				Claims claims = Jwts.parser()
						.setSigningKey(apiKeySecretBytes)
						.parseClaimsJws(token.replace("Bearer ", ""))
						.getBody();

				String username = claims.getSubject();

				// Search db
				if (username != null) {
					Employee e = eRepo.findEmployeeByUsername(username);
					UserPrincipal principal = new UserPrincipal(e);
					auth = new UsernamePasswordAuthenticationToken(username, null, principal.getAuthorities());
				}
			}
		} catch (ExpiredJwtException eje) {
			//TODO do something when token expires
		} catch (SignatureException se) {
			// TODO do something when token is tampered with
		}

		return auth;
	}
}
