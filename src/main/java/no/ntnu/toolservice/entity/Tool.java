package no.ntnu.toolservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tool {

    @NotNull
    private Long id;

    @NotEmpty
    private String name;

    @NotEmpty
    private String description;

    private String image;

    @NotEmpty
    private String location;

    // Constructor for stringified date (Used for testing with REST clients)
    public Tool(@NotEmpty String name, @NotEmpty String description, String image, @NotEmpty String location) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.location = location;
    }

    // Constructor for Date date
    /*public Tool(@NotNull Long id, @NotEmpty String name, @NotEmpty String description, String image, @NotEmpty String location, @NotNull Date dateCreated) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
        this.location = location;
        this.dateCreated = dateCreated;
    }*/
}
