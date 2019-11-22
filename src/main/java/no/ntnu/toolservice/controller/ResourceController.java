package no.ntnu.toolservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.ntnu.toolservice.entity.Tool;
import no.ntnu.toolservice.files.StorageService;
import no.ntnu.toolservice.repository.ResourceRepository;
import no.ntnu.toolservice.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    @RequestMapping(value = "/newToolWithImage", method = RequestMethod.POST)
    public ResponseEntity<String> newToolWithImage(@RequestParam("name") String name,
                                                   @RequestParam("desc") String desc,
                                                   @RequestParam("location") String location,
                                                   @RequestParam("file") MultipartFile multipartFile) {
        // First stores the file in the file system
        this.storageService.store(multipartFile);
        try {
            String filename = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            if (filename.contains(".jpg")) {
                filename = filename.split("j", 2)[0].concat("png");
            }
            // Get the file path of the newly added file
            String filePath = this.storageService.loadAsResource(filename).getURL().toString();
            return this.resourceService.newTool(new Tool(
                    name, desc, filePath, location
            ));
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error while creating tool", HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/searchTool/{toolName}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<String> getAllToolsByToolName(@PathVariable String toolName) {
        List<Tool> listOfTools = this.resourceRepository.searchToolsByToolName(toolName);
        ResponseEntity<String> response;
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
        return response;
    }

    /*------------------------------
    File relevant endpoints
    ----------------------------*/

    // Method for testing file upload
/*    @RequestMapping(value = "/fileUpload")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile multipartFile) {
        return this.storageService.store(multipartFile);
    }*/

    @RequestMapping(value = "/files/{filename}", method = RequestMethod.POST, produces = "image/png")
    public ResponseEntity<byte[]> getFile(@PathVariable String filename) {
        Resource file = this.storageService.loadAsResource(filename);
        ArrayList<Byte> blist = null;
        try {
            return new ResponseEntity<>(Files.readAllBytes(Paths.get(file.getURI())), HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity("", HttpStatus.BAD_REQUEST);
        }
    }
}
