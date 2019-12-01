package no.ntnu.toolservice.repository;

import no.ntnu.toolservice.domain.Loan;
import no.ntnu.toolservice.entity.Material;
import no.ntnu.toolservice.entity.Tool;
import no.ntnu.toolservice.mapper.LoanRowMapper;
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
    private final RowMapper<Loan> loanRowMapper;

    @Autowired
    public ResourceRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                              JdbcTemplate jdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.jdbcTemplate = jdbcTemplate;
        this.toolRowMapper = new ToolRowMapper();
        this.materialRowMapper = new MaterialRowMapper();
        this.loanRowMapper = new LoanRowMapper();
    }

    public List<Tool> findAllTools() {
        return this.jdbcTemplate.query("SELECT * FROM tools", this.toolRowMapper);
    }

    public List<Material> findAllMaterials() {
        return this.jdbcTemplate.query("SELECT * FROM materials", this.materialRowMapper);
    }

    /**
     * Returns a list containing tools that includes the specified name
     *
     * @param toolName name of the tool to find
     * @return a list containing tools that includes the specified name
     */
    public List<Tool> searchToolsByToolName(String toolName, int project_id) {
        return this.namedParameterJdbcTemplate.query(
                "SELECT * FROM tools INNER JOIN project_tools pt on tools.tool_id = pt.tool_id " +
                        "WHERE project_id = :project_id " +
                        "AND LOWER(name) LIKE CONCAT('%', LOWER(:name), '%')",
                new MapSqlParameterSource().addValue("name", toolName).addValue("project_id", project_id),
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
                "SELECT * FROM materials WHERE LOWER(name) LIKE CONCAT('%', LOWER(:name), '%')",
                new MapSqlParameterSource("name", materialName),
                this.toolRowMapper
        );
    }

    public void addTool(Tool tool) {
        this.namedParameterJdbcTemplate.update(
                "INSERT INTO tools (name, \"desc\", location, image) VALUES (:name, :desc, :location, :image)",
                new MapSqlParameterSource().addValue("name", tool.getName())
                        .addValue("desc", tool.getDescription()).addValue("location", tool.getLocation())
                        .addValue("image", tool.getImage()));
    }

    public Tool findToolById(Long toolId) {
        return this.namedParameterJdbcTemplate.queryForObject(
                "SELECT * FROM tools WHERE tool_id = :id",
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
                "INSERT INTO borrows (employee_id, tool_id) VALUES (:employee_id, :tool_id)",
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
    public List<Loan> findAllLoansByEmployeeId(Long employee_id) {
        return this.namedParameterJdbcTemplate.query(
                "SELECT tools.tool_id, tools.name, borrows.expiration_date, borrows.employee_id " +
                        "FROM tools, borrows " +
                        "WHERE tools.tool_id = borrows.tool_id " +
                        "AND borrows.employee_id = :employee_id",
                new MapSqlParameterSource("employee_id", employee_id),
                this.loanRowMapper
        );
    }

    public List<Loan> searchAllLoansByEmployeeId(String search, Long employee_id) {
        return this.namedParameterJdbcTemplate.query(
                "SELECT tools.tool_id, tools.name, borrows.expiration_date, borrows.employee_id " +
                        "FROM tools, borrows " +
                        "WHERE tools.tool_id = borrows.tool_id " +
                        "AND borrows.employee_id = :employee_id " +
                        "AND LOWER(tools.name) LIKE CONCAT('%', LOWER(:search), '%')",
                new MapSqlParameterSource()
                    .addValue("search", search)
                    .addValue("employee_id", employee_id),
                this.loanRowMapper
        );
    }

    public List<Tool> findAllUniqueTools() {
        return jdbcTemplate.query("SELECT * FROM tools GROUP BY(name)", this.toolRowMapper);
    }

    public List<Tool> findAllUniqueToolsByProject(Long project_id) {
        return this.namedParameterJdbcTemplate.query(
                "SELECT * FROM tools INNER JOIN project_tools pt on tools.tool_id = pt.tool_id " +
                        "WHERE project_id = :project_id " +
                        "GROUP BY(name)",
                new MapSqlParameterSource("project_id", project_id),
                this.toolRowMapper
        );
    }
}
