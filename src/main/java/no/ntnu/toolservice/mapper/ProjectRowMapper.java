package no.ntnu.toolservice.mapper;

import no.ntnu.toolservice.entity.Project;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The purpose of this class is to map query results
 * to java objects (in case it is needed)
 */
public class ProjectRowMapper implements RowMapper<Project> {

    @Nullable
    @Override
    public Project mapRow(ResultSet resultSet, int i) throws SQLException {
        return new Project(
                resultSet.getLong("project_id"),
                resultSet.getString("name"),
                resultSet.getString("location"),
                resultSet.getString("desc"),
                resultSet.getString("image")
        );
    }

}
