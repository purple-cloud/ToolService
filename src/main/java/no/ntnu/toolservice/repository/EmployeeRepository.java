package no.ntnu.toolservice.repository;

import no.ntnu.toolservice.entity.Employee;
import no.ntnu.toolservice.mapper.EmployeeRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EmployeeRepository {
	private JdbcTemplate jdbc;
	private RowMapper<Employee> mapper;

	// SQL Queries
	private static final String INSERT_EMP =
			"INSERT INTO employees(name, email, username, password, phone) VALUES (?, ?, ?, ?, ?)";
	private static final String GET_ALL_EMPS = "SELECT * FROM employees";
	private static final String GET_EMP_FROM_ID = "SELECT * FROM employees e WHERE e.employee_id = ? LIMIT 1";

	// Find employee by username
	private static final String GET_EMP_FROM_USERNAME = "SELECT employee_id FROM employees WHERE username = ?";

	// Insert/register new employee
	private static final String REGISTER_EMP =
			"INSERT INTO employees (name, username, email, password, phone) VALUES (?, ?, ?, ?, ?)";

	@Autowired
	public EmployeeRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
		this.mapper = new EmployeeRowMapper();
	}

	public int addEmployee(Employee e) {
		// Return the id of employee
		return jdbc.update(INSERT_EMP, e.getName(), e.getEmail(), e.getUsername(), e.getPassword(),
				String.valueOf(e.getPhone()));
	}

	public int addEmployee(String name, String username, String email, String password, int phone) {
		// Return the id of employee
		return jdbc.update(REGISTER_EMP, name, username, email, password, phone);
	}

	public Employee findEmployeeById(Long id) {
		Employee e = null;
		try {
			e = jdbc.queryForObject(GET_EMP_FROM_ID, new Object[]{id}, mapper);
		} catch (Exception ex) {
			return null;
		}

		return e;
	}

	public List<Employee> findAll() {
		return jdbc.query(GET_ALL_EMPS, mapper);
	}

	public boolean employeeExists(String username) {
		int employeeId = 0;

		try {
			employeeId = jdbc.queryForObject(GET_EMP_FROM_USERNAME, new Object[]{username}, Integer.class);
		} catch (EmptyResultDataAccessException ex) {} // We are expecting the result set to be empty
		return (employeeId != 0);
	}
}
