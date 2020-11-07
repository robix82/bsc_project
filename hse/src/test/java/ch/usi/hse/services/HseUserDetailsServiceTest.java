package ch.usi.hse.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import ch.usi.hse.db.entities.Administrator;
import ch.usi.hse.db.entities.Role;
import ch.usi.hse.db.entities.User;
import ch.usi.hse.exceptions.NoSuchUserException;

@SpringBootTest
public class HseUserDetailsServiceTest {

	@MockBean
	private UserService userService;
	
	@Autowired
	private HseUserDetailsService testService;
	
	private User testUser;
	private Set<Role> adminRoles;
	private List<GrantedAuthority> adminAuthorities;
	private String validName, badName;
	
	@BeforeEach
	public void setUp() throws NoSuchUserException {
		
		adminRoles = new HashSet<>();
		adminRoles.add(new Role(1, "ADMIN")); 
		
		adminAuthorities = new ArrayList<>();
		adminAuthorities.add(new SimpleGrantedAuthority("ADMIN"));
		
		validName = "tastUser";
		badName = "noSuchUser";
		
		testUser = new Administrator(23, validName, "testPwd", adminRoles);
		
		when(userService.findUser(validName)).thenReturn(testUser);
		when(userService.findUser(badName)).thenThrow(NoSuchUserException.class);
	}
	
	@Test
	public void testLoadUserByUserName1() {
		
		boolean noexc;
		
		try {
			
			UserDetails details = testService.loadUserByUsername(testUser.getUserName());
			
			assertEquals(testUser.getUserName(), details.getUsername());
			assertEquals(testUser.getPassword(), details.getPassword());
			assertTrue(details.isEnabled());
			assertTrue(details.isAccountNonExpired());
			assertTrue(details.isCredentialsNonExpired());
			assertTrue(details.isAccountNonLocked());
			assertIterableEquals(adminAuthorities, details.getAuthorities());
			
			noexc = true;
		}
		catch (UsernameNotFoundException e) {
			noexc = false;
		}
		
		assertTrue(noexc);
	}
	
	@Test
	public void testLoadUserForAuthentication2() {
		
		boolean exc;
		
		try {
			
			testService.loadUserByUsername(badName);
			exc = false;
		}
		catch (UsernameNotFoundException e) {
			exc = true;
		}
		
		assertTrue(exc);
	}
}









