package no.ntnu.toolservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Table
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Tool {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long toolId;

    private String toolName;

    private String toolLocation;

    // TODO Change this to be a User later
    private String borrower;

    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dateCreated;

    private Date expiryDate;

    public Tool(String toolName, String toolLocation) {
        this.toolName = toolName;
        this.toolLocation = toolLocation;
    }

    @PrePersist
    protected void onCreate() {
        dateCreated = new Date();
    }

}
