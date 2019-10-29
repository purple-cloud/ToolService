package no.ntnu.toolservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Project {

    @NotNull
    private Long id;

    @NotEmpty
    private String name;

    @NotEmpty
    private String location;

    public Project(@NotEmpty String name, @NotEmpty String location) {
        this.name = name;
        this.location = location;
    }

}
