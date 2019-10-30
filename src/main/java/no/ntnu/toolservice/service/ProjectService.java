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

    public ResponseEntity<String> addNewProjectLeader(Long employeeId) {
        Employee foundLeader = this.projectRepository.findProjectLeaderByEmployeeId(employeeId);
        if (foundLeader == null) {
            this.projectRepository.addProjectLeader(employeeId);
            return new ResponseEntity<>("OK", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Project leader with that id already exist", HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<String> addProjectLeaderToProject(Long leader_id, Long project_id) {
        Employee foundLeader = this.projectRepository.findProjectLeaderInProject(leader_id, project_id);
        if (foundLeader == null) {
            this.projectRepository.addProjectLeaderToProject(leader_id, project_id);
            return new ResponseEntity<>("OK", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Project leader is already assigned to that project", HttpStatus.BAD_REQUEST);
        }
    }

}
