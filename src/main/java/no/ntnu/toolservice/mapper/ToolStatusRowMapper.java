package no.ntnu.toolservice.mapper;

import no.ntnu.toolservice.entity.ToolStatus;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ToolStatusRowMapper implements RowMapper<ToolStatus> {
	@Override
	public ToolStatus mapRow(ResultSet rs, int i) throws SQLException {
		return new ToolStatus(rs.getInt("tool_id"),
				rs.getString("name"),
				rs.getString("location"),
				rs.getBoolean("isAvailable"));
	}
}
