package no.ntnu.toolservice.security;

import lombok.Getter;
import no.ntnu.toolservice.entity.Employee;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
public class UserPrincipal implements UserDetails {

	private Employee user;

	public UserPrincipal(Employee user) {
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> permissions = new ArrayList<>();
		user.getPermissionsList().forEach(p -> permissions.add(new SimpleGrantedAuthority(p)));
		user.getRolesList().forEach(r -> permissions.add(new SimpleGrantedAuthority("ROLE_" + r)));
		return permissions;
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
