package no.ntnu.toolservice.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Loan {

    private Long toolId;

    private String toolName;

    private String dueDate;

    private Long employee_id;

}
