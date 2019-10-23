package no.ntnu.toolservice.mapper;

import no.ntnu.toolservice.entity.Tool;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * The purpose of this class is to map query results
 * to java objects (in case it is needed)
 */
public class ToolRowMapper implements RowMapper<Tool> {

    @Override
    public Tool mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Tool tool = new Tool();
        tool.setId(resultSet.getLong("tool_id"));
        tool.setName(resultSet.getString("name"));
        tool.setLocation(resultSet.getString("location"));
        tool.setDescription(resultSet.getString("desc"));
        tool.setImage(resultSet.getString("image"));
        tool.setDateCreated(resultSet.getDate("date_created"));
        return tool;
    }
}
