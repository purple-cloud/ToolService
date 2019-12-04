package no.ntnu.toolservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.ntnu.toolservice.entity.Employee;
import no.ntnu.toolservice.repository.EmployeeRepository;
import no.ntnu.toolservice.repository.RolePermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@CrossOrigin(origins = "*")
@RequestMapping(value = "employees", produces = "application/json")
public class EmployeeController {
	private EmployeeRepository repository;
	private RolePermissionRepository rRepo;
	private ObjectMapper mapper;

	@Autowired
	public EmployeeController(EmployeeRepository repository, RolePermissionRepository rRepo) {
		this.repository = repository;
		this.rRepo = rRepo;
		this.mapper = new ObjectMapper();
	}

	@RequestMapping(value = "", method = GET)
	public ResponseEntity<String> getAllEmployees() {
		String resp = "";
		try {
			List<Employee> employeeList = repository.findAll();

			employeeList.forEach(employee -> {
				employee.setPermissions(rRepo.getPermissionsByEmployeeIdAsString(employee.getId()));
				employee.setRoles(rRepo.getRolesByEmployeeIdAsString(employee.getId()));
			});

			resp = mapper.writeValueAsString(employeeList);
		} catch (JsonProcessingException e) {
			return new ResponseEntity<String>(e.getMessage(), BAD_REQUEST);
		}
		return new ResponseEntity<String>(resp, OK);
	}

	@RequestMapping(value = "/{id}", method = GET)
	public ResponseEntity<String> getSingleEmployee(@PathVariable(value = "id") long employee_id) {
		String resp = "";

		try {
			Employee employee = repository.findEmployeeById(employee_id);
			resp = mapper.writeValueAsString(employee);
		} catch (EmptyResultDataAccessException e) {
			return new ResponseEntity<String>("Employee does not exist", BAD_REQUEST);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return new ResponseEntity<String>("Something went wrong...", INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<String>(resp, OK);
	}

	/**
	 * Find all employees not in a project. Useful for when adding new employees to a project they are not subscribed
	 * to.
	 *
	 * @param projectId         identity of project to search in
	 * @param employeeSearch    full (or a subset of) user name
	 * @return                  a list of employees not in a project
	 *
	 * @see no.ntnu.toolservice.repository.EmployeeRepository
	 */
	@RequestMapping(value = "/project/nonmembers/{project-id}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity getAllEmployeesNotInProject(@PathVariable("project-id") long projectId) {
		try {
			return ResponseEntity.ok(repository.findEmployeesNotInProject(projectId));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@RequestMapping(value = "/notProjectLeaders/{project_id}", method = RequestMethod.GET)
	public ResponseEntity<String> getAllEmployeesNotProjectLeadersInProject(@PathVariable Long project_id) {
		try {
			return new ResponseEntity<>(this.mapper.writeValueAsString(repository.findEmployeesNotProjectLeaderInProject(project_id)), OK);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), BAD_REQUEST);
		}
	}
}
