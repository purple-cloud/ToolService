package no.ntnu.toolservice.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RolePermissionRepository {
	private JdbcTemplate jdbc;

	private static final String GET_ROLES_BY_EMP_ID =
			"SELECT * FROM roles AS r " +
					"WHERE r.role_id IN (SELECT role_id FROM employee_roles AS er WHERE er.employee_id = ?)";

	private static final String GET_PERMISSIONS_BY_EMP_ID =
			"SELECT * FROM permissions AS p " +
					"WHERE p.permission_id IN (SELECT rp.permission_id FROM role_permissions AS rp " +
					"WHERE rp.role_id IN (SELECT role_id FROM employee_roles AS er WHERE er.employee_id = ?))";

	@Autowired
	public RolePermissionRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public List<String> findRolesByEmployerId(int id) {
		List<String> roles = jdbc.query(GET_ROLES_BY_EMP_ID, new Object[]{id}, (rs, rowNum) -> {
			return rs.getString("name"); // Return role name
		});

		return roles;
	}

	public List<String> findPermissionsByEmployerId(int id) {
		List<String> permissions = jdbc.query(GET_PERMISSIONS_BY_EMP_ID, new Object[]{id}, (rs, rowNum) -> {
			return rs.getString("name"); // Return permission name
		});

		return permissions;
	}

	public String getPermissionsByEmployeeIdAsString(int id) {
		StringBuilder sb = new StringBuilder();
		for (String permission : findPermissionsByEmployerId(id)) {
			sb.append(permission).append(",");
		}

		if (sb.length() < 1) return "";
		return sb.toString().substring(0, sb.length() - 1);
	}

	public String getRolesByEmployeeIdAsString(int id) {
		StringBuilder sb = new StringBuilder();
		for (String roles : findRolesByEmployerId(id)) {
			sb.append(roles).append(",");
		}

		if (sb.length() < 1) return "";
		return sb.toString().substring(0, sb.length() - 1);
	}


}
