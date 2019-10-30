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
public class Material {

    @NotNull
    private Long id;

    @NotEmpty
    private String name;

    @NotEmpty
    private String description;

    private String image;

    @NotEmpty
    private String location;

    @NotNull
    private Date dateCreated;

    // Constructor for stringified date (Used for testing with REST clients)
    public Material(@NotEmpty String name, @NotEmpty String description, String image, @NotEmpty String location, @NotEmpty String dateCreated) {
        try {
            this.name = name;
            this.description = description;
            this.image = image;
            this.location = location;
            this.dateCreated = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(dateCreated);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
