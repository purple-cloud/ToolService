package no.ntnu.toolservice.repository;

import no.ntnu.toolservice.entity.Tool;
import no.ntnu.toolservice.mapper.ToolRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ToolRepository {

    // For creating named queries
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    // For creating basic queries
    private final JdbcTemplate jdbcTemplate;
    // Row Mapper
    private final RowMapper<Tool> rowMapper;

    @Autowired
    public ToolRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                          JdbcTemplate jdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = new ToolRowMapper();
    }

    public List<Tool> findAll() {
        return this.jdbcTemplate.query(
                "SELECT * FROM tools",
                this.rowMapper
        );
    }

    public void addTool(Tool tool) {
        this.jdbcTemplate.update(
                "INSERT INTO tools (name, `desc`, location) VALUES (?, ?, ?)",
                tool.getName(), tool.getDescription(), tool.getLocation()
        );
    }

    public Tool findToolById(Long toolId) {
        return this.namedParameterJdbcTemplate.queryForObject(
                "SELECT * FROM tools WHERE tools.tool_id = :id",
                new MapSqlParameterSource("id", toolId),
                this.rowMapper
        );
    }

}
