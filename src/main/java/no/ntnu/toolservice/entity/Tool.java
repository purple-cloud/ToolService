package no.ntnu.toolservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tool {

    private Long id;

    private String name;

    private String description;

    private String location;

    // TODO Change this to be a User later
    private String borrower;

    private Date dateCreated;

    private Date expiryDate;

    public Tool(String toolName, String description, String toolLocation) {
        this.name = toolName;
        this.description = description;
        this.location = toolLocation;
    }

}
