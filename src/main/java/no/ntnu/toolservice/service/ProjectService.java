package no.ntnu.toolservice.service;

import no.ntnu.toolservice.entity.Employee;
import no.ntnu.toolservice.entity.Project;
import no.ntnu.toolservice.repository.EmployeeRepository;
import no.ntnu.toolservice.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    /*------------------------------
    Employee and project leader specific
    ----------------------------*/

    public List<Project> findAllProjects() {
        return this.projectRepository.findAll();
    }

    public List<Project> findAllProjectsThatEmployeeIsInByEmployeeId(Long employeeId) {
        return this.projectRepository.findAllProjectsThatEmployeeIsInByEmployeeId(employeeId);
    }

    public Project findProjectById(Long projectId) {
        return this.projectRepository.findProjectById(projectId);
    }

    public ResponseEntity<String> addEmployeeToProject(Long employeeId, Long projectId) {
        Employee foundEmployee = this.projectRepository.findEmployeeInProjectByEmployeeId(employeeId, projectId);
        if (foundEmployee == null) {
            this.projectRepository.addEmployeeToProject(employeeId, projectId);
            return new ResponseEntity<>("OK", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Employee already exist", HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<String> removeEmployeeFromProject(Long employeeId, Long projectId) {
        Employee foundEmployee = this.projectRepository.findEmployeeInProjectByEmployeeId(employeeId, projectId);
        if (foundEmployee != null) {
            this.projectRepository.removeEmployeeFromProject(employeeId, projectId);
            return new ResponseEntity<>("OK", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Employee in specified project not found", HttpStatus.BAD_REQUEST);
        }
    }

    /*------------------------------
    Admin specific
    ----------------------------*/

    public ResponseEntity<String> addNewProject(Project project) {
        Project foundProject = this.projectRepository.findProjectByProjectName(project.getName());
        if (foundProject == null) {
            this.projectRepository.addProject(project);
            return new ResponseEntity<>("OK", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Project with that name already exist", HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<String> addProjectLeaderToProject(Long employee_id, Long project_id) {
        // First see if the employee already is a project leader
        Employee isLeader = this.projectRepository.findProjectLeaderByEmployeeId(employee_id);
        // If employee is not a project leader, create an instance
        if (isLeader == null) {
            Long leader_id = this.projectRepository.addProjectLeader();
            // Then link the employee to that project leader id

            this.projectRepository.connectLeaderIdWithEmployeeId(
                    employee_id, leader_id);
            this.projectRepository.addProjectLeaderToProject(
                    employee_id, project_id);
            return new ResponseEntity<>("OK", OK);
        } else {
            // If the employee is a project leader see if employee is in project
            Employee foundLeader = this.projectRepository.findProjectLeaderInProject(
                    employee_id, project_id);
            // If the employee is not a project leader in this project, add as project leader in that project
            if (foundLeader == null) {
                this.projectRepository.addProjectLeaderToProject(
                        employee_id, project_id);
                return new ResponseEntity<>("OK", OK);
            } else {
                return new ResponseEntity<>("Project leader already exists", BAD_REQUEST);
            }
        }
    }

}
