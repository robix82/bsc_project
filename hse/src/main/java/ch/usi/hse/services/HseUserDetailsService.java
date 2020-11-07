package ch.usi.hse.services;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import ch.usi.hse.db.entities.Role;
import ch.usi.hse.db.entities.User;
import ch.usi.hse.exceptions.NoSuchUserException;

/**
 * Provides user details for SpringSecurity authentication
 * 
 * @author robert.jans@usi.ch
 *
 */
@Service
public class HseUserDetailsService implements UserDetailsService {

	@Autowired
	private UserService userService;
	
	@Override
	@Transactional
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {

		
		try {
			
			User u = userService.findUser(userName);
			
			List<GrantedAuthority> authorities = new ArrayList<>();
			
			for (Role r : u.getRoles()) { 
				authorities.add(new SimpleGrantedAuthority(r.getRole()));
			}
			
			UserDetails details = new org.springframework.security.core.userdetails.User(
					
					u.getUserName(),
					u.getPassword(),
					u.getActive(),    // -> enabled
					true, true, true, // -> accountNonExpired, credentialsNonExpired, accountNonLocked
					authorities 
			);
			
			return details;
		}
		catch (NoSuchUserException e) {	
			
			throw new UsernameNotFoundException(e.getMessage());
		}
	}
}










