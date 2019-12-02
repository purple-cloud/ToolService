package no.ntnu.toolservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ToolStatus {
	private int id;
	private String name;
	private String location;
	private boolean isAvailable;
}
