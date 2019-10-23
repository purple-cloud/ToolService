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
                "SELECT * FROM public.tools",
                this.rowMapper
        );
    }

    public void addTool(Tool tool) {
        this.namedParameterJdbcTemplate.update(
                "INSERT INTO public.tools (name, \"desc\", location, image, date_created) VALUES (:name, :desc, :location, :image, :date_created)",
                new MapSqlParameterSource().addValue("name", tool.getName())
                        .addValue("desc", tool.getDescription()).addValue("location", tool.getLocation())
                        .addValue("image", tool.getImage()).addValue("date_created", tool.getDateCreated()));
    }

    public Tool findToolById(Long toolId) {
        return this.namedParameterJdbcTemplate.queryForObject(
                "SELECT * FROM public.tools WHERE tool_id = :id",
                new MapSqlParameterSource("id", toolId),
                this.rowMapper
        );
    }

}
