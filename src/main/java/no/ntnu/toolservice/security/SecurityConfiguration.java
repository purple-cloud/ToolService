package no.ntnu.toolservice.security;

import no.ntnu.toolservice.Encoders;
import no.ntnu.toolservice.controller.AuthorizationService;
import no.ntnu.toolservice.repository.EmployeeRepository;
import no.ntnu.toolservice.repository.RolePermissionRepository;
import no.ntnu.toolservice.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@Import(Encoders.class)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	private UserDetailsServiceImpl userDetailsService;
	private EmployeeRepository employeeRepository;
	private RolePermissionRepository rolePermissionRepository;

	@Autowired
	public SecurityConfiguration(UserDetailsServiceImpl userDetailsService, EmployeeRepository employeeRepository,
	                             RolePermissionRepository rolePermissionRepository) {
		this.userDetailsService = userDetailsService;
		this.employeeRepository = employeeRepository;
		this.rolePermissionRepository = rolePermissionRepository;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.authorizeRequests()
				.antMatchers("/employees/*").hasAuthority("user:view_single")
				.antMatchers("/employees").hasAuthority("user:view_all")

				.and()

				.httpBasic()

				.and()

				.logout()
				.logoutUrl("/logout");
	}

	/*
	@Bean(BeanIds.AUTHENTICATION_MANAGER)
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	} */

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	@Override
	public UserDetailsService userDetailsService() {
		return new UserDetailsServiceImpl(employeeRepository, rolePermissionRepository);
	}

	@Bean
	DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider dao = new DaoAuthenticationProvider();
		dao.setPasswordEncoder(passwordEncoder());
		dao.setUserDetailsService(userDetailsService);
		return dao;
	}
}
