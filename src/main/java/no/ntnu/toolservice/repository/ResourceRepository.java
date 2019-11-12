package no.ntnu.toolservice.repository;

import no.ntnu.toolservice.entity.Material;
import no.ntnu.toolservice.entity.Tool;
import no.ntnu.toolservice.mapper.MaterialRowMapper;
import no.ntnu.toolservice.mapper.ToolRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ResourceRepository {

    // For creating named queries
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    // For creating basic queries
    private final JdbcTemplate jdbcTemplate;
    // Row Mappers
    private final RowMapper<Tool> toolRowMapper;
    private final RowMapper<Material> materialRowMapper;

    @Autowired
    public ResourceRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                              JdbcTemplate jdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.jdbcTemplate = jdbcTemplate;
        this.toolRowMapper = new ToolRowMapper();
        this.materialRowMapper = new MaterialRowMapper();
    }

    public List<Tool> findAllTools() {
        return this.jdbcTemplate.query("SELECT * FROM public.tools", this.toolRowMapper);
    }

    public List<Material> findAllMaterials() {
        return this.jdbcTemplate.query("SELECT * FROM public.materials", this.materialRowMapper);
    }

    /**
     * Returns a list containing tools that includes the specified name
     *
     * @param toolName name of the tool to find
     * @return a list containing tools that includes the specified name
     */
    public List<Tool> searchToolsByToolName(String toolName) {
        return this.namedParameterJdbcTemplate.query(
                "SELECT * FROM public.tools WHERE LOWER(name) LIKE CONCAT('%', LOWER(:name), '%')",
                new MapSqlParameterSource("name", toolName),
                this.toolRowMapper
        );
    }

    /**
     * Returns a list containing materials that includes the specified name
     *
     * @param materialName name of the material to find
     * @return a list containing materials that includes the specified name
     */
    public List<Tool> searchMaterialsByToolName(String materialName) {
        return this.namedParameterJdbcTemplate.query(
                "SELECT * FROM public.materials WHERE LOWER(name) LIKE CONCAT('%', LOWER(:name), '%')",
                new MapSqlParameterSource("name", materialName),
                this.toolRowMapper
        );
    }

    public void addTool(Tool tool) {
        this.namedParameterJdbcTemplate.update(
                "INSERT INTO public.tools (name, \"desc\", location, image) VALUES (:name, :desc, :location, :image)",
                new MapSqlParameterSource().addValue("name", tool.getName())
                        .addValue("desc", tool.getDescription()).addValue("location", tool.getLocation())
                        .addValue("image", tool.getImage()));
    }

    public Tool findToolById(Long toolId) {
        return this.namedParameterJdbcTemplate.queryForObject(
                "SELECT * FROM public.tools WHERE tool_id = :id",
                new MapSqlParameterSource("id", toolId),
                this.toolRowMapper
        );
    }

    /**
     * Adds a tool "loan" to the specified employer
     *
     * @param employee_id the employer id
     * @param tool_id the tool id
     */
    public void borrowTool(Long employee_id, Long tool_id) {
        this.namedParameterJdbcTemplate.update(
                "INSERT INTO public.borrows (employee_id, tool_id) VALUES (:employee_id, :tool_id)",
                new MapSqlParameterSource()
                        .addValue("employee_id", employee_id)
                        .addValue("tool_id", tool_id)
        );
    }

    /**
     * Returns all the tools that an employee is currently borrowing
     *
     * @param employee_id the employers id
     * @return all the tools that an employee is currently borrowing
     */
    public List<Tool> findAllLoansByEmployeeId(Long employee_id) {
        return this.namedParameterJdbcTemplate.query(
                "SELECT * FROM tools INNER JOIN borrows b on tools.tool_id = b.tool_id " +
                        "INNER JOIN employees e on b.employee_id = e.employee_id " +
                        "WHERE e.employee_id = :employee_id",
                new MapSqlParameterSource("employee_id", employee_id),
                this.toolRowMapper
        );
    }

}
