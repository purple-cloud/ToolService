package no.ntnu.toolservice.mapper;

import no.ntnu.toolservice.entity.Employee;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EmployeeRowMapper implements RowMapper<Employee> {
	@Override
	public Employee mapRow(ResultSet r, int i) throws SQLException {
		Employee e = new Employee();

		e.setId(r.getInt("employee_id"));
		e.setName(r.getString("name"));
		e.setUsername(r.getString("username"));
		e.setEmail(r.getString("email"));
		e.setPassword(r.getString("password"));
		e.setPhone(r.getInt("phone"));
		e.setImage(r.getString("image"));

		return e;
	}
}
