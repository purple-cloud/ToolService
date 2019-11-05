package no.ntnu.toolservice.service;

import no.ntnu.toolservice.entity.Employee;
import no.ntnu.toolservice.entity.UserPrincipal;
import no.ntnu.toolservice.repository.EmployeeRepository;
import no.ntnu.toolservice.repository.RolePermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	private final EmployeeRepository employeeRepository;
	private final RolePermissionRepository rolePermissionRepository;

	@Autowired
	public UserDetailsServiceImpl(EmployeeRepository employeeRepository, RolePermissionRepository rolePermissionRepository) {
		this.employeeRepository = employeeRepository;
		this.rolePermissionRepository = rolePermissionRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Employee employee = employeeRepository.findEmployeeByUsername(username);

		return new UserPrincipal(employee);
	}
}
