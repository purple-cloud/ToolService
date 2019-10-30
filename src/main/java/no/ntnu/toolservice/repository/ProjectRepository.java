package no.ntnu.toolservice.repository;

import no.ntnu.toolservice.entity.Employee;
import no.ntnu.toolservice.entity.Project;
import no.ntnu.toolservice.entity.Tool;
import no.ntnu.toolservice.mapper.ProjectRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProjectRepository {

    // For creating named queries
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    // For creating basic queries
    private final JdbcTemplate jdbcTemplate;
    // Row Mapper
    private final RowMapper<Project> rowMapper;

    @Autowired
    public ProjectRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                             JdbcTemplate jdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = new ProjectRowMapper();
    }

    /**
     * Return a list containing all the projects
     *
     * @return a list containing all the projects
     */
    public List<Project> findAll() {
        return this.jdbcTemplate.query("SELECT * FROM public.projects", this.rowMapper);
    }

    /**
     * Add a new project
     *
     * @param project the project to add
     */
    public void addProject(Project project) {
        this.namedParameterJdbcTemplate.update(
                "INSERT INTO public.projects (name, location) VALUES (:name, :location)",
                new MapSqlParameterSource()
                        .addValue("name", project.getName())
                        .addValue("location", project.getLocation())
        );
    }

    /**
     * Return the project found by the project id
     *
     * @param projectId the project id
     * @return the project found by the project id
     */
    public Project findToolById(Long projectId) {
        return this.namedParameterJdbcTemplate.queryForObject(
                "SELECT * FROM public.projects WHERE project_id = :id",
                new MapSqlParameterSource("id", projectId),
                this.rowMapper
        );
    }

    /**
     * Add an employee to a project
     *
     * @param employeeId employee id
     * @param projectId project id
     */
    public void addEmployeeToProject(Long employeeId, Long projectId) {
        this.namedParameterJdbcTemplate.update(
                "INSERT INTO public.project_employees (employee_id, project_id) " +
                        "VALUES (:employee_id, :project_id)",
                new MapSqlParameterSource()
                        .addValue("employee_id", employeeId)
                        .addValue("project_id", projectId)
        );
    }

    /**
     * Remove an employee from a project by project id
     *
     * @param employeeId employee id
     * @param projectId project id
     */
    public void removeEmployeeFromProject(Long employeeId, Long projectId) {
        this.namedParameterJdbcTemplate.update(
                "DELETE FROM project_employees WHERE employee_id = :employee_id AND project_id = :project_id",
                new MapSqlParameterSource()
                        .addValue("employee_id", employeeId)
                        .addValue("project_id", projectId)
        );
    }

}
