package no.ntnu.toolservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.ntnu.toolservice.entity.Employee;
import no.ntnu.toolservice.entity.Project;
import no.ntnu.toolservice.repository.ProjectRepository;
import no.ntnu.toolservice.service.ProjectService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class AdministrationController {

    private final ProjectService projectService;
    private ObjectMapper objectMapper;

    // Repository
    private final ProjectRepository projectRepository;

    @Autowired
    public AdministrationController(ProjectService projectService,
                                    ProjectRepository projectRepository) {
        this.projectService = projectService;
        this.projectRepository = projectRepository;
        // Init ObjectMapper
        this.objectMapper = new ObjectMapper();
    }

    /*------------------------------
    Project relevant endpoints
    ----------------------------*/

    @RequestMapping(value = "/findAllProjects", method = RequestMethod.GET)
    public ResponseEntity<String> findAllProjects() {
        try {
            String list = this.objectMapper.writeValueAsString(this.projectService.findAllProjects());
            return new ResponseEntity<>(list, HttpStatus.OK);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Something went wrong while fetching projects", HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/findAllProjectsThatUsesIsIn/{id}", method = RequestMethod.POST)
    public ResponseEntity<String> findAllProjectsThatEmployeeIsInByEmployeeId(@PathVariable(value = "id") Long employee_id) {
        try {
            String list = this.objectMapper.writeValueAsString(this.projectService.findAllProjectsThatEmployeeIsInByEmployeeId(employee_id));
            return new ResponseEntity<>(list, HttpStatus.OK);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Something went wrong while fetching projects", HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/findProjectById/{id}", method = RequestMethod.POST)
    public ResponseEntity<String> findProjectById(@PathVariable(value = "id") Long project_id) {
        Project foundProject = this.projectService.findProjectById(project_id);
        if (foundProject != null) {
            try {
                String projectAsString = this.objectMapper.writeValueAsString(foundProject);
                return new ResponseEntity<>(projectAsString, HttpStatus.OK);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return new ResponseEntity<>("Something went wrong while converting project", HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>("Project doesn't exist", HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/addEmployeeToProject", method = RequestMethod.POST)
    public ResponseEntity<String> addEmployeeToProject(HttpEntity<String> httpEntity) {
        String body = httpEntity.getBody();
        if (body != null) {
            JSONObject jsonObject = new JSONObject(body);
            return this.projectService.addEmployeeToProject(
                    jsonObject.getLong("employee_id"),
                    jsonObject.getLong("project_id")
            );
        } else {
            return new ResponseEntity<>("Body is null", HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/removeEmployeeFromProject", method = RequestMethod.POST)
    public ResponseEntity<String> removeEmployeeFromProject(HttpEntity<String> httpEntity) {
        String body = httpEntity.getBody();
        if (body != null) {
            JSONObject jsonObject = new JSONObject(body);
            return this.projectService.removeEmployeeFromProject(
                    jsonObject.getLong("employee_id"),
                    jsonObject.getLong("project_id")
            );
        } else {
            return new ResponseEntity<>("Body is null", HttpStatus.BAD_REQUEST);
        }
    }

    /*------------------------------
    Admin relevant endpoints
    ----------------------------*/

    @RequestMapping(value = "/addNewProject", method = RequestMethod.POST)
    public ResponseEntity<String> addNewProject(HttpEntity<String> httpEntity) {
        String body = httpEntity.getBody();
        if (body != null) {
            JSONObject jsonObject = new JSONObject(body);
            return this.projectService.addNewProject(new Project(
                    jsonObject.getString("name"),
                    jsonObject.getString("desc"),
                    jsonObject.getString("location")
            ));
        } else {
            return new ResponseEntity<>("Body is null", HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/addNewProjectLeader/{id}", method = RequestMethod.POST)
    public ResponseEntity<String> addNewProjectLeader(@PathVariable(value = "id") Long employee_id) {
        return this.projectService.addNewProjectLeader(employee_id);
    }

    @RequestMapping(value = "/addProjectLeaderToProject", method = RequestMethod.POST)
    public ResponseEntity<String> addProjectLeaderToProject(HttpEntity<String> httpEntity) {
        String body = httpEntity.getBody();
        if (body != null) {
            JSONObject jsonObject = new JSONObject(body);
            return this.projectService.addProjectLeaderToProject(
                    jsonObject.getLong("employee_id"),
                    jsonObject.getLong("project_id")
            );
        } else {
            return new ResponseEntity<>("Body is null", HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/findProjectLeaders/{projectId}", method = RequestMethod.POST)
    public ResponseEntity<String> findAllProjectLeadersInProjectByProjectId(@PathVariable Long projectId) {
        List<Employee> listOfProjectLeaders = this.projectRepository.findAllProjectLeadersInProjectByProjectId(projectId);
        ResponseEntity<String> responseEntity;
        if (listOfProjectLeaders != null) {
            try {
                String list = this.objectMapper.writeValueAsString(listOfProjectLeaders);
                responseEntity = new ResponseEntity<>(list, HttpStatus.OK);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                responseEntity = new ResponseEntity<>("Something went wrong while parsing project leaders", HttpStatus.BAD_REQUEST);
            }
        } else {
            responseEntity = new ResponseEntity<>("No project leaders for specified project", HttpStatus.BAD_REQUEST);
        }
        return responseEntity;
    }

    @RequestMapping(value = "/searchProject/{projectName}", method = RequestMethod.POST)
    public ResponseEntity<String> getAllToolsByToolName(@PathVariable String projectName) {
        List<Project> listOfProjects = this.projectRepository.searchProjectByProjectName(projectName);
        ResponseEntity<String> response;
        if (listOfProjects != null) {
            try {
                String list = this.objectMapper.writeValueAsString(listOfProjects);
                response = new ResponseEntity<String>(list, HttpStatus.OK);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                response = new ResponseEntity<String>("Something went wrong while parsing projects", HttpStatus.BAD_REQUEST);
            }
        } else {
            response = new ResponseEntity<String>("projects not found", HttpStatus.BAD_REQUEST);
        }
        return response;
    }

}
