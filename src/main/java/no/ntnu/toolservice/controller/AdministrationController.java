package no.ntnu.toolservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.ntnu.toolservice.files.StorageService;
import no.ntnu.toolservice.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
public class AdministrationController {

    private final ResourceService resourceService;
    private final StorageService storageService;
    private ObjectMapper objectMapper;

    @Autowired
    public AdministrationController(ResourceService resourceService,
                                    StorageService storageService) {
        this.resourceService = resourceService;
        this.storageService = storageService;
        // Init ObjectMapper
        this.objectMapper = new ObjectMapper();
    }

    /*------------------------------
    Project relevant endpoints
    ----------------------------*/

}
