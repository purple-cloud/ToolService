package no.ntnu.toolservice.mapper;

import no.ntnu.toolservice.entity.Material;
import no.ntnu.toolservice.entity.Tool;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MaterialRowMapper implements RowMapper<Material> {

    @Override
    public Material mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return new Material(
                resultSet.getLong("tool_id"),
                resultSet.getString("name"),
                resultSet.getString("location"),
                resultSet.getString("desc"),
                resultSet.getString("image"),
                resultSet.getDate("date_created")
        );
    }

}
