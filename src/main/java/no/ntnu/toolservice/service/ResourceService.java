package no.ntnu.toolservice.service;

import no.ntnu.toolservice.entity.Tool;
import no.ntnu.toolservice.repository.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResourceService {

    private final ResourceRepository resourceRepository;

    @Autowired
    public ResourceService(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    /*------------------------------
    Tool relevant endpoints
    ----------------------------*/

    public List<Tool> getAllTools() {
        return this.resourceRepository.findAllTools();
    }

    public ResponseEntity<String> newTool(Tool tool) {
        this.resourceRepository.addTool(tool);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

}
