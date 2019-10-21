package no.ntnu.toolservice.service;

import no.ntnu.toolservice.entity.Tool;
import no.ntnu.toolservice.repository.ToolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ToolService {

    private final ToolRepository toolRepository;

    @Autowired
    public ToolService(ToolRepository toolRepository) {
        this.toolRepository = toolRepository;
    }

    /*------------------------------
    Tool relevant endpoints
    ----------------------------*/

    public List<Tool> getAllTools() {
        return this.toolRepository.findAll();
    }

    public ResponseEntity<String> newTool(Tool tool) {
        Tool foundTool = this.toolRepository.findToolById(tool.getId());
        if (foundTool == null) {
            this.toolRepository.addTool(tool);
            return new ResponseEntity<>("OK", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Tool already exist", HttpStatus.BAD_REQUEST);
        }
    }

}
