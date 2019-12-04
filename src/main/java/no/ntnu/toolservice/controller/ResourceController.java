package no.ntnu.toolservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.ntnu.toolservice.domain.Loan;
import no.ntnu.toolservice.entity.Tool;
import no.ntnu.toolservice.entity.ToolStatus;
import no.ntnu.toolservice.files.StorageService;
import no.ntnu.toolservice.repository.ResourceRepository;
import no.ntnu.toolservice.service.ResourceService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.font.CharToGlyphMapper;

import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.Response;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class ResourceController {

    private final ResourceService resourceService;
    private final StorageService storageService;
    private ObjectMapper objectMapper;

    // Repositories
    private final ResourceRepository resourceRepository;

	//@Value("${file.image.location}")
	//private String fileLocation;

    @Autowired
    public ResourceController(ResourceService resourceService,
                              StorageService storageService,
                              ResourceRepository resourceRepository) {
        this.resourceService = resourceService;
        this.storageService = storageService;
        this.resourceRepository = resourceRepository;
        // Init ObjectMapper
        this.objectMapper = new ObjectMapper();
    }
    
    /*------------------------------
    Tool relevant endpoints
    ----------------------------*/

    @RequestMapping(value = "/getAllTools", method = RequestMethod.GET)
    public ResponseEntity<String> getAllTools() {
        try {
            String list = this.objectMapper.writeValueAsString(this.resourceService.getAllTools());
            return new ResponseEntity<String>(list, HttpStatus.OK);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/getAllUniqueTools", method = RequestMethod.GET)
    public ResponseEntity getAllToolsUnique() {
        try {
            return ResponseEntity.ok(this.objectMapper.writeValueAsString(this.resourceRepository.findAllUniqueTools()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @RequestMapping(value = "/getAllUniqueToolsByProject/{project_id}", method = RequestMethod.GET)
    public ResponseEntity<String> getAllToolsByProject(@PathVariable Long project_id) {
        try {
            String list = this.objectMapper.writeValueAsString(this.resourceRepository.findAllUniqueToolsByProject(project_id));
            return new ResponseEntity<>(list, HttpStatus.OK);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Something went wrong while parsing tools", HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/newToolWithImage", method = RequestMethod.POST, consumes = "multipart/form-data")
    public ResponseEntity<String> newToolWithImage(@RequestParam("name") String name,
                                                   @RequestParam("desc") String desc,
                                                   @RequestParam("location") String location,
                                                   @RequestParam("file") MultipartFile multipartFile) {

        // First stores the file in the file system
        this.storageService.store(multipartFile);
        try {
            String filename = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            // Get the file path of the newly added file
            return this.resourceService.newTool(new Tool(
                    name, desc, filename, location
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error while creating tool", HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/searchTool", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<String> getAllToolsByToolName(HttpEntity<String> httpEntity) {
        String body = httpEntity.getBody();
        ResponseEntity<String> response;
        if (body != null) {
            JSONObject jsonObject = new JSONObject(body);
            List<Tool> listOfTools = this.resourceRepository.searchToolsByToolName(jsonObject.getString("search"), jsonObject.getInt("project_id"));
            if (listOfTools != null) {
                try {
                    String list = this.objectMapper.writeValueAsString(listOfTools);
                    response = new ResponseEntity<String>(list, HttpStatus.OK);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                    response = new ResponseEntity<String>("Something went wrong while parsing tools", HttpStatus.BAD_REQUEST);
                }
            } else {
                response = new ResponseEntity<String>("Tools not found", HttpStatus.BAD_REQUEST);
            }
        } else {
            response = new ResponseEntity<>("Body can't be null", HttpStatus.BAD_REQUEST);
        }
        return response;
    }

    @RequestMapping(value = "/rentTool", method = RequestMethod.POST)
    public ResponseEntity<String> rentSpecifiedTool(HttpEntity<String> httpEntity) {
        String body = httpEntity.getBody();
        if (body != null) {
            JSONObject jsonObject = new JSONObject(body);
            // See if tool exists
            Tool toolExists = this.resourceRepository.getSpecificTool(jsonObject.getLong("tool_id"));
            if (toolExists != null) {
                Tool toolBorrowed = this.resourceRepository.getBorrowedTool(jsonObject.getLong("tool_id"));
                if (toolBorrowed == null) {
                    this.resourceRepository.borrowTool(
                            jsonObject.getLong("employee_id"),
                            jsonObject.getLong("tool_id")
                    );
                    this.resourceRepository.updateToolAvailability(false, jsonObject.getLong("tool_id"));
                    return new ResponseEntity<>("OK", HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("Tool is already borrowed", HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<>("Tool doesn't exist", HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("Body is null", HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/returnTool", method = RequestMethod.POST)
    public ResponseEntity<String> returnSpecifiedTool(HttpEntity<String> httpEntity) {
        String body = httpEntity.getBody();
        if (body != null) {
            JSONObject jsonObject = new JSONObject(body);
            Tool foundLoan = this.resourceRepository.getBorrowedTool(jsonObject.getLong("tool_id"));
            if (foundLoan != null) {
                this.resourceRepository.returnTool(jsonObject.getLong("tool_id"));
                this.resourceRepository.updateToolAvailability(true, jsonObject.getLong("tool_id"));
                return new ResponseEntity<>("OK", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("The tool isn't on loan", HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("Body can't be empty", HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/findAllBorrows/{employee_id}", method = RequestMethod.GET)
    public ResponseEntity<String> getAllBorrows(@PathVariable Long employee_id) {
        try {
            String list = this.objectMapper.writeValueAsString(this.resourceRepository.findAllLoansByEmployeeId(employee_id));
            return new ResponseEntity<>(list, HttpStatus.OK);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Something went wrong while parsing loans", HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/tools/status/{project-id}/{tool-name}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getToolsWithStatuses(@PathVariable("project-id") Long projectId,
                                               @PathVariable("tool-name") String toolName) {
        ResponseEntity response = null;

        try {
            List<ToolStatus> statuses = this.resourceRepository.findAllToolsWithAvailabilityStatus(projectId, toolName);
            response = ResponseEntity.ok(statuses);
        } catch (Exception e) {
            response = ResponseEntity.badRequest().body(e.getMessage());
        }

        return response;
    }

    @RequestMapping(value = "/searchAllBorrows", method = RequestMethod.POST)
    public ResponseEntity<String> searchAllBorrowsByEmployeeId(HttpEntity<String> httpEntity) {
        String body = httpEntity.getBody();
        if (body != null) {
            JSONObject jsonObject = new JSONObject(body);
            List<Loan> listOfLoans = this.resourceRepository.searchAllLoansByEmployeeId(
                    jsonObject.getString("search"),
                    jsonObject.getLong("employee_id")
            );
            try {
                String list = this.objectMapper.writeValueAsString(listOfLoans);
                return new ResponseEntity<>(list, HttpStatus.OK);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return new ResponseEntity<>("Something went wrong while parsing loans", HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("Body Can't be null", HttpStatus.BAD_REQUEST);
        }
    }

    /*------------------------------
    File relevant endpoints
    ----------------------------*/

    // Method for testing file upload
/*    @RequestMapping(value = "/fileUpload")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile multipartFile) {
        return this.storageService.store(multipartFile);
    }*/


	@RequestMapping(value = "/images/{image-name:.+}", method = RequestMethod.GET)
	public ResponseEntity getImage(@PathVariable("image-name") String imgName, HttpServletResponse response) {
		Path imgPath = this.storageService.load(imgName);
		try {
            // Get file stream
			FileInputStream img = new FileInputStream(imgPath.toFile());

            // Set MIME type of content to render, i.e. images/png
            response.setContentType(Files.probeContentType(imgPath));

            // Dump the content of the file into the response body
			StreamUtils.copy(img, response.getOutputStream());
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok().build();
	}
}
