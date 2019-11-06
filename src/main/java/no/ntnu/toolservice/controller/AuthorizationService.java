package no.ntnu.toolservice.controller;

import no.ntnu.toolservice.security.JwtTokenUtil;
import no.ntnu.toolservice.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

//@Service
public class AuthorizationService {
	private JdbcTemplate jdbc;
	private EmployeeRepository empRepo;
	private JwtTokenUtil jwt;

	// Get id of employees with the correct user/pass combination (but limit the result to 1 if it exists)
	private static final String VALIDATE_EMP =
			"SELECT employee_id FROM employees WHERE username = ? AND password = ? LIMIT 1";

	@Autowired
	public AuthorizationService(JdbcTemplate jdbc, EmployeeRepository empRepo, JwtTokenUtil jwt) {
		this.jdbc = jdbc;
		this.empRepo = empRepo;
		this.jwt = jwt;
	}

	public int validate(String username, String password) {
		// Check for matching credentials using username and password
		int employeeId = jdbc.queryForObject(VALIDATE_EMP, new Object[]{username, password}, Integer.class);

		// If a employee with matching credential was found, return the employee id
		// If user/pass combination does not exist, it returns 0
		return employeeId;
	}

	// TODO implement issuing of token
	public String issueToken(long employeeId) {
		return jwt.generateToken(empRepo.findEmployeeById(employeeId));
	}

	public boolean employeeExists(String username) {
		return empRepo.employeeExists(username);
	}

	public int registerEmployee(String name, String username, String email, String password, int phone) {
		return empRepo.addEmployee(name, username, email, password, phone);
	}
}
