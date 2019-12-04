package no.ntnu.toolservice.repository;

import no.ntnu.toolservice.entity.Employee;
import no.ntnu.toolservice.mapper.EmployeeRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@SuppressWarnings("Duplicates")
@Repository
public class EmployeeRepository {
	// For creating named queries
	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
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
	public EmployeeRepository(JdbcTemplate jdbc, NamedParameterJdbcTemplate namedParameterJdbcTemplate,
							  RolePermissionRepository rRepo) {
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
		this.jdbc = jdbc;
		this.rRepo = rRepo;
		this.mapper = new EmployeeRowMapper();
	}

	public int addEmployee(Employee e) {
		// Return the id of employee
		return jdbc.update(INSERT_EMP, e.getName(), e.getEmail(), e.getUsername(), e.getPassword(),
				String.valueOf(e.getPhone()), "");
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

	public List<Employee> findAllEmployeesInProject(Long project_id) {
		return this.namedParameterJdbcTemplate.query(
				"SELECT * FROM employees " +
						"INNER JOIN project_employees pe on employees.employee_id = pe.employee_id " +
						"WHERE pe.project_id = :project_id",
				new MapSqlParameterSource("project_id", project_id),
				this.mapper
		);
	}

	public List<Employee> searchForEmployeesInProject(Long project_id, String search) {
		return this.namedParameterJdbcTemplate.query(
				"SELECT * FROM employees " +
						"INNER JOIN project_employees pe on employees.employee_id = pe.employee_id " +
						"WHERE pe.project_id = :project_id " +
						"AND LOWER(employees.name) LIKE CONCAT('%', LOWER(:search), '%')",
				new MapSqlParameterSource()
						.addValue("project_id", project_id)
						.addValue("search", search),
				this.mapper
		);
	}

	public boolean employeeExists(String username) {
		int employeeId = 0;

		try {
			employeeId = jdbc.queryForObject(GET_EMP_ID_FROM_USERNAME, new Object[]{username}, Integer.class);
		} catch (EmptyResultDataAccessException ex) {} // We are expecting the result set to be empty
		return (employeeId != 0);
	}

	/**
	 * Find all employees not in a project. Useful for when adding new employees to a project they are not subscribed
	 * to.
	 *
	 * @param projectId         identity of project to search in
	 * @return                  a list of employees not in a project
	 */
	public List<Employee> findEmployeesNotInProject(long projectId) {
		String sql = "SELECT e.employee_id, name, email, username, password, phone, e.image, date_created FROM employees e " +
					"    INNER JOIN project_employees pe on e.employee_id = pe.employee_id " +
					"    WHERE e.employee_id NOT IN (SELECT employee_id FROM project_employees WHERE project_id = ?) " +
					"    GROUP BY e.employee_id " +
					"    ORDER BY e.employee_id DESC";

		return this.jdbc.query(sql, new Object[]{projectId}, mapper);
	}
}
