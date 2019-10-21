package no.ntnu.toolservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.ntnu.toolservice.entity.Tool;
import no.ntnu.toolservice.service.ToolService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
public class ToolController {

    private final ToolService toolService;
    private ObjectMapper objectMapper;

    @Autowired
    public ToolController(ToolService toolService) {
        this.toolService = toolService;
        this.objectMapper = new ObjectMapper();
    }

    /*------------------------------
    Tool relevant endpoints
    ----------------------------*/

    @RequestMapping(value = "/getAllTools", method = RequestMethod.GET)
    public ResponseEntity<String> getAllTools() {
        try {
            String list = this.objectMapper.writeValueAsString(this.toolService.getAllTools());
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
                return this.toolService.newTool(new Tool(
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

}
