package no.ntnu.toolservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.ntnu.toolservice.entity.Tool;
import no.ntnu.toolservice.files.StorageService;
import no.ntnu.toolservice.service.ResourceService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@CrossOrigin(origins = "*")
public class ResourceController {

    private final ResourceService resourceService;
    private final StorageService storageService;
    private ObjectMapper objectMapper;

    @Autowired
    public ResourceController(ResourceService resourceService,
                              StorageService storageService) {
        this.resourceService = resourceService;
        this.storageService = storageService;
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

    @RequestMapping(value = "/newTool", method = RequestMethod.POST)
    public ResponseEntity<String> newTool(HttpEntity<String> httpEntity) {
        String body = httpEntity.getBody();
        if (body != null) {
            try {
                JSONObject jsonObject = new JSONObject(body);
                return this.resourceService.newTool(new Tool(
                        jsonObject.getString("name"),
                        jsonObject.getString("desc"),
                        jsonObject.getString("location")
                ));
            } catch (JSONException je) {
                je.printStackTrace();
                return new ResponseEntity<>(je.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>("Body is null", HttpStatus.BAD_REQUEST);
    }

    /*------------------------------
    File relevant endpoints
    ----------------------------*/

    // Method for testing file upload
    @RequestMapping(value = "/fileUpload")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile multipartFile) {
        return this.storageService.store(multipartFile);
    }

    @RequestMapping(value = "/files/{filename:.+}", method = RequestMethod.POST)
    public ResponseEntity<String> getFile(@PathVariable String filename) {
        Resource file = this.storageService.loadAsResource(filename);
        try {
            return new ResponseEntity<>(file.getURI().toString(), HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("File not found", HttpStatus.BAD_REQUEST);
        }
    }
}
