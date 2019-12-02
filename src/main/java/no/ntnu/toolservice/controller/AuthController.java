package no.ntnu.toolservice.controller;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import no.ntnu.toolservice.entity.Employee;
import no.ntnu.toolservice.repository.EmployeeRepository;
import no.ntnu.toolservice.repository.RolePermissionRepository;
import no.ntnu.toolservice.security.JWTConfiguration;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.net.URI;
import java.security.Key;
import java.util.Date;

@RestController
@CrossOrigin(value = "*")
@RequestMapping(produces = "text/plain")
public class AuthController {
	private EmployeeRepository repo;
	private RolePermissionRepository roleRepo;
	private PasswordEncoder pwEncoder;
	private JWTConfiguration jwtConfig;

	@Autowired
	public AuthController(EmployeeRepository repo, RolePermissionRepository roleRepo, PasswordEncoder pwEncoder, JWTConfiguration jwtConfig) {
		this.repo = repo;
		this.roleRepo = roleRepo;
		this.pwEncoder = pwEncoder;
		this.jwtConfig = jwtConfig;
	}

	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public ResponseEntity register(@RequestBody String credentialsString) {
		JSONObject credentials = new JSONObject(credentialsString);

		// Check if username already exists
		if (repo.employeeExists(credentials.getString("username"))) {
			return ResponseEntity.badRequest().body("Username is unavailable");
		}

		// Encode password
		String password = pwEncoder.encode(credentials.getString("password"));

		// Create employee instance from object (exception if it doesnt have all values)
		Employee e = new Employee(credentials.getString("name"),
				credentials.getString("username"),
				credentials.getString("email"),
				password,
				credentials.getInt("phone"));

		// Insert employee
		repo.addEmployee(e);

		// Set employee id, roles and permissions
		e.setId(repo.findEmployeeByUsername(e.getUsername()).getId());
		e.setRoles(roleRepo.getRolesByEmployeeIdAsString(e.getId()));
		e.setPermissions(roleRepo.getPermissionsByEmployeeIdAsString(e.getId()));

		// Finalize
		URI loc = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/employees/{empId}").buildAndExpand(e.getId()).toUri();

		// Set jwt hashing algorithm and convert secret key to bytes after converting string to base64
		SignatureAlgorithm alg = SignatureAlgorithm.HS512;
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(jwtConfig.getSecret());
		Key signingKey = new SecretKeySpec(apiKeySecretBytes, alg.getJcaName());

		// Create token
		String token = Jwts.builder()
				.setIssuer(jwtConfig.getIssuer())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + (jwtConfig.getTokenTimeout() * 1000 * 60 * 60)))
				.setSubject(e.getUsername())
				.claim("employee", e)
				.signWith(alg, signingKey)
				.compact();

		return ResponseEntity.created(loc)
				.header("Authorization", jwtConfig.getTokenPrefix().concat(token))
				.body("Employee registered successfully");
	}
}
