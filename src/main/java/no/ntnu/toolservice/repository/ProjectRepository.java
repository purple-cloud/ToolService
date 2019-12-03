package no.ntnu.toolservice.repository;

import no.ntnu.toolservice.entity.Employee;
import no.ntnu.toolservice.entity.Project;
import no.ntnu.toolservice.entity.Tool;
import no.ntnu.toolservice.mapper.EmployeeRowMapper;
import no.ntnu.toolservice.mapper.ProjectRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ProjectRepository {

    // For creating named queries
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    // For creating basic queries
    private final JdbcTemplate jdbcTemplate;
    // Row Mappers
    private final RowMapper<Project> projectRowMapper;
    private final RowMapper<Employee> employeeRowMapper;

    @Autowired
    public ProjectRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                             JdbcTemplate jdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.jdbcTemplate = jdbcTemplate;
        this.projectRowMapper = new ProjectRowMapper();
        this.employeeRowMapper = new EmployeeRowMapper();
    }

    /*------------------------------
    Employee and project leader DB interactions
    ----------------------------*/

    /**
     * Return a list containing all the projects
     *
     * @return a list containing all the projects
     */
    public List<Project> findAll() {
        return this.jdbcTemplate.query("SELECT * FROM projects", this.projectRowMapper);
    }

    /**
     * Return the project found by the project id
     *
     * @param projectId the project id
     * @return the project found by the project id
     */
    public Project findProjectById(Long projectId) {
        return this.namedParameterJdbcTemplate.queryForObject(
                "SELECT * FROM projects WHERE project_id = :id",
                new MapSqlParameterSource("id", projectId),
                this.projectRowMapper
        );
    }

    /**
     * Returns the project found by the specified project name
     *
     * @param projectName the name of the project to find
     * @return the project found by the specified project name
     */
    public Project findProjectByProjectName(String projectName) {
        try {
            return this.namedParameterJdbcTemplate.queryForObject(
                    "SELECT * FROM projects WHERE name = :name",
                    new MapSqlParameterSource("name", projectName),
                    this.projectRowMapper
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * Add an employee to a project
     *
     * @param employeeId employee id
     * @param projectId project id
     */
    public void addEmployeeToProject(Long employeeId, Long projectId) {
        this.namedParameterJdbcTemplate.update(
                "INSERT INTO project_employees (employee_id, project_id) " +
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

    /**
     * Returns the employee found in the specified project by employee id
     *
     * @param employeeId the employee id
     * @param projectId the project id
     * @return the employee found in the specified project by employee id
     */
    public Employee findEmployeeInProjectByEmployeeId(Long employeeId, Long projectId) {
        return this.namedParameterJdbcTemplate.queryForObject(
                "SELECT * FROM employees " +
                        "INNER JOIN project_employees pe on employees.employee_id = pe.employee_id",
                new MapSqlParameterSource()
                        .addValue("employee_id", employeeId)
                        .addValue("project_id", projectId),
                this.employeeRowMapper
        );
    }

    /**
     * Returns a list containing all the projects
     * that an employee is registered in specified by
     * the employer id
     *
     * @param employeeId the employees' id
     * @return a list containing all the projects
     *         that an employee is registered in specified by
     *         the employer id
     */
    public List<Project> findAllProjectsThatEmployeeIsInByEmployeeId(Long employeeId) {
        return this.namedParameterJdbcTemplate.query(
                "SELECT * FROM projects INNER JOIN project_employees pe on projects.project_id = pe.project_id " +
                        "WHERE pe.employee_id = :employee_id",
                new MapSqlParameterSource("employee_id", employeeId),
                this.projectRowMapper
        );
    }

    /*------------------------------
    Admin restricted DB interactions
    ----------------------------*/

    /**
     * Add a new project
     *
     * @param project the project to add
     */
    public void addProject(Project project) {
        this.namedParameterJdbcTemplate.update(
                "INSERT INTO projects (name, location, `desc`, image) VALUES (:name, :location, :desc, :image)",
                new MapSqlParameterSource()
                        .addValue("name", project.getName())
                        .addValue("location", project.getLocation())
                        .addValue("desc", project.getDescription())
                        .addValue("image", project.getImage())
        );
    }

    /**
     * Add a new project and get the identity of the project.
     *
     * @param p     the new project to add to database
     * @return      the auto generated identity of the new project
     */
    public long addProjectAndGetId(Project p) {
        String sql = "INSERT INTO projects (name, `desc`, location, image) VALUES(?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                new PreparedStatementCreator() {
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        PreparedStatement ps =
                                connection.prepareStatement(sql, new String[]{"project_id"});
                        ps.setString(1, p.getName());
                        ps.setString(2, p.getDescription());
                        ps.setString(3, p.getLocation());
                        ps.setString(4, p.getImage());
                        return ps;
                    }
                },
                keyHolder);
	    System.out.println(keyHolder.getKey().longValue());
        return keyHolder.getKey().longValue();
    }

    /**
     * Create a new project leader
     */
    public void addProjectLeader(Long employeeId) {
        this.namedParameterJdbcTemplate.update(
                "INSERT INTO project_leader (employee_id) VALUES (:employee_id)",
                new MapSqlParameterSource("employee_id", employeeId)
        );
    }

    /**
     * Returns the project leader with specified employee_id
     *
     * @param employeeId the employee id
     * @return the project leader with specified employee_id
     */
    public Employee findProjectLeaderByEmployeeId(Long employeeId) {
        return this.namedParameterJdbcTemplate.queryForObject(
                "SELECT * FROM project_leader WHERE employee_id = :employee_id",
                new MapSqlParameterSource("employee_id", employeeId),
                this.employeeRowMapper
        );
    }

    /**
     * Add project leader to a project
     */
    public void addProjectLeaderToProject(Long employee_id, Long project_id) {
        this.namedParameterJdbcTemplate.update(
                "INSERT INTO project_project_leader(project_id, leader_id) \n" +
                        "VALUES (:project_id, " +
                        "       (SELECT pl.leader_id " +
                        "           FROM project_leader pl " +
                        "           INNER JOIN employee_project_leader epl ON epl.employee_id = pl.leader_id " +
                        "           INNER JOIN employees e ON e.employee_id = epl.employee_id " +
                        "           WHERE e.employee_id = :employee_id " +
                        "       )" +
                        ")",
                new MapSqlParameterSource()
                        .addValue("project_id", project_id)
                        .addValue("employee_id", employee_id)
        );
    }

    /**
     * Returns the employee found that is the
     * project leader in the specified project
     *
     * @return the employee found that is the
     *         project leader in the specified project
     */
    public Employee findProjectLeaderInProject(Long employee_id, Long project_id) {
        return this.namedParameterJdbcTemplate.queryForObject(
                "SELECT * FROM employees " +
                        "INNER JOIN employee_project_leader epl on employees.employee_id = epl.employee_id " +
                        "INNER JOIN project_leader pl on epl.leader_id = pl.leader_id " +
                        "INNER JOIN project_project_leader ppl on epl.leader_id = ppl.leader_id " +
                        "WHERE epl.employee_id = :employee_id AND ppl.project_id = :project_id",
                new MapSqlParameterSource()
                        .addValue("project_id", project_id)
                        .addValue("employee_id", employee_id),
                this.employeeRowMapper
        );
    }

    /**
     * Returns all the project leaders in a specified project
     *
     * @param project_id project id of the project we want leaders from
     * @return all the project leaders in a specified project
     */
    public List<Employee> findAllProjectLeadersInProjectByProjectId(Long project_id) {
        return this.namedParameterJdbcTemplate.query(
                "SELECT * FROM employees " +
                        "INNER JOIN employee_project_leader epl on employees.employee_id = epl.employee_id " +
                        "INNER JOIN project_leader pl on epl.leader_id = pl.leader_id " +
                        "INNER JOIN project_project_leader ppl on epl.leader_id = ppl.leader_id " +
                        "WHERE ppl.project_id = :project_id",
                new MapSqlParameterSource("project_id", project_id),
                this.employeeRowMapper
        );
    }

    public List<Employee> searchAllProjectLeadersInProject(Long project_id, String search) {
        return this.namedParameterJdbcTemplate.query(
                "SELECT * FROM employees " +
                        "INNER JOIN employee_project_leader epl on employees.employee_id = epl.employee_id " +
                        "INNER JOIN project_leader pl on epl.leader_id = pl.leader_id " +
                        "INNER JOIN project_project_leader ppl on epl.leader_id = ppl.leader_id " +
                        "WHERE ppl.project_id = :project_id " +
                        "AND LOWER(employees.name) LIKE CONCAT('%', LOWER(:search), '%')",
                new MapSqlParameterSource()
                    .addValue("project_id", project_id)
                    .addValue("search", search),
                this.employeeRowMapper
        );
    }

    /**
     * Returns a list containing projects that includes the specified name
     *
     * @param projectName name of the project to find
     * @return a list containing projects that includes the specified name
     */
    public List<Project> searchProjectByProjectName(String projectName, int employee_id) {
        return this.namedParameterJdbcTemplate.query(
                "SELECT * FROM projects " +
                        "INNER JOIN project_project_leader ON projects.project_id = project_project_leader.project_id " +
                        "INNER JOIN project_leader ON project_project_leader.leader_id = project_leader.leader_id " +
                        "INNER JOIN employee_project_leader ON project_leader.leader_id = employee_project_leader.leader_id " +
                        "INNER JOIN employees ON employee_project_leader.employee_id = employees.employee_id " +
                        "WHERE employees.employee_id = :employee_id " +
                        "AND LOWER(projects.name) LIKE CONCAT('%', LOWER(:name), '%')",
                new MapSqlParameterSource().addValue("name", projectName).addValue("employee_id", employee_id),
                this.projectRowMapper
        );
    }

    public List<Project> searchAllProjects(String search) {
        return this.namedParameterJdbcTemplate.query(
                "SELECT * FROM projects WHERE LOWER(projects.name) LIKE CONCAT('%', LOWER(:search), '%')",
                new MapSqlParameterSource("search", search),
                this.projectRowMapper
        );
    }

    public List<Project> getAllProjectsAUserIsLeaderFor(Long employee_id) {
        return this.namedParameterJdbcTemplate.query(
                "SELECT * FROM projects " +
                        "INNER JOIN project_project_leader ON projects.project_id = project_project_leader.project_id " +
                        "INNER JOIN project_leader ON project_project_leader.leader_id = project_leader.leader_id " +
                        "INNER JOIN employee_project_leader ON project_leader.leader_id = employee_project_leader.leader_id " +
                        "INNER JOIN employees ON employee_project_leader.employee_id = employees.employee_id " +
                        "WHERE employees.employee_id = :employee_id ",
                new MapSqlParameterSource("employee_id", employee_id),
                this.projectRowMapper
        );
    }

}
