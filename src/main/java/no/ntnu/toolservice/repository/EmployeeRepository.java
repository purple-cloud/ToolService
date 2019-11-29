package no.ntnu.toolservice.repository;

import no.ntnu.toolservice.entity.Employee;
import no.ntnu.toolservice.mapper.EmployeeRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@SuppressWarnings("Duplicates")
@Repository
public class EmployeeRepository {
	private JdbcTemplate jdbc;
	private RowMapper<Employee> mapper;
	private RolePermissionRepository rRepo;

	// SQL Queries
	private static final String INSERT_EMP =
			"INSERT INTO employees(name, email, username, password, phone, image) VALUES (?, ?, ?, ?, ?, ?)";
	private static final String GET_ALL_EMPS = "SELECT * FROM employees";
	private static final String GET_EMP_FROM_ID = "SELECT * FROM employees e WHERE e.employee_id = ? LIMIT 1";

	// Find employee id by username
	private static final String GET_EMP_ID_FROM_USERNAME = "SELECT employee_id FROM employees WHERE username = ?";

	// Find employee by username
	private static final String GET_EMP_FROM_USERNAME = "SELECT * FROM employees WHERE username = ? LIMIT 1";

	// Insert/register new employee
	private static final String REGISTER_EMP =
			"INSERT INTO employees (name, username, email, password, phone, image) VALUES (?, ?, ?, ?, ?, ?)";

	@Autowired
	public EmployeeRepository(JdbcTemplate jdbc, RolePermissionRepository rRepo) {
		this.jdbc = jdbc;
		this.rRepo = rRepo;
		this.mapper = new EmployeeRowMapper();
	}

	public int addEmployee(Employee e) {
		// Return the id of employee
		return jdbc.update(INSERT_EMP, e.getName(), e.getEmail(), e.getUsername(), e.getPassword(),
				String.valueOf(e.getPhone()));
	}

	public int addEmployee(String name, String username, String email, String password, int phone, String image) {
		// TODO The line under does not return the employees id, therefore another query is used to get it
		jdbc.update(REGISTER_EMP, name, username, email, password, phone, image);
		// Return the id of employee
		return jdbc.queryForObject(GET_EMP_ID_FROM_USERNAME, new Object[]{username}, Integer.class);
	}

	public Employee findEmployeeById(Long id) {
		Employee e = null;
		try {
			e = jdbc.queryForObject(GET_EMP_FROM_ID, new Object[]{id}, mapper);

			if (e != null) {
				e.setPermissions(rRepo.getPermissionsByEmployeeIdAsString(e.getId()));
				e.setRoles(rRepo.getRolesByEmployeeIdAsString(e.getId()));
			} else throw new Exception("Employee not found");
		} catch (Exception ex) {
			return null;
		}

		return e;
	}

	public Employee findEmployeeByUsername(String username) {
		Employee e = null;
		try {
			e = jdbc.queryForObject(GET_EMP_FROM_USERNAME, new Object[]{username}, mapper);
			if (e != null) {
				e.setPermissions(rRepo.getPermissionsByEmployeeIdAsString(e.getId()));
				e.setRoles(rRepo.getRolesByEmployeeIdAsString(e.getId()));
			} else throw new Exception("Employee not found");
		} catch (Exception ex) {
			return null;
		}

		return e;
	}

	public List<Employee> findAll() {
		List<Employee> employees = jdbc.query(GET_ALL_EMPS, mapper);
		employees.forEach(employee -> {
			employee.setPermissions(rRepo.getPermissionsByEmployeeIdAsString(employee.getId()));
			employee.setRoles(rRepo.getRolesByEmployeeIdAsString(employee.getId()));
		});

		return employees;
	}

	public boolean employeeExists(String username) {
		int employeeId = 0;

		try {
			employeeId = jdbc.queryForObject(GET_EMP_ID_FROM_USERNAME, new Object[]{username}, Integer.class);
		} catch (EmptyResultDataAccessException ex) {} // We are expecting the result set to be empty
		return (employeeId != 0);
	}
}
