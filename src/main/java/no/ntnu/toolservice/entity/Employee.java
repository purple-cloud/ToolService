package no.ntnu.toolservice.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonPropertyOrder({ "id", "name", "username", "email", "password", "phone"})
public class Employee {
	private int id;
	private String name;
	private String username;
	private String email;
	private String password;
	private int phone;

	public Employee(String name, String username, String email, String password, int phone) {
		this.name = name;
		this.username = username;
		this.email = email;
		this.password = password;
		this.phone = phone;
	}
}
