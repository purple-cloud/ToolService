package no.ntnu.toolservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(value = {"roles", "permissions"})
@JsonPropertyOrder(value = { "id", "name", "username", "email", "password", "phone"})
public class Employee {
	private int id;
	private String name;
	private String username;
	private String email;
	private String password;
	private int phone;

	// Security
	private String roles = "";
	private String permissions = "";

	public Employee(String name, String username, String email, String password, int phone) {
		this.name = name;
		this.username = username;
		this.email = email;
		this.password = password;
		this.phone = phone;
	}

	public List<String> getRolesList() {
		if (roles.length() > 0) {
			return Arrays.asList(roles.split(","));
		}
		return new ArrayList<>();
	}

	public List<String> getPermissionsList() {
		if (permissions.length() > 0) {
			return Arrays.asList(permissions.split(","));
		}
		return new ArrayList<>();
	}
}
