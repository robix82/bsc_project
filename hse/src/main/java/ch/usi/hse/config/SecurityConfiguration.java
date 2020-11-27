package ch.usi.hse.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import ch.usi.hse.services.HseUserDetailsService;

/**
 * Configuration for user authentication settings
 * 
 * @author robert.jans@usi.ch
 *
 */
@Configuration
@EnableWebSecurity

public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	private HseUserDetailsService userDetailsService;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private HseAuthenticationSuccessHandler loginHandler;
	
	@Autowired
	private HseLogoutSuccessHandler logoutHandler;
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		auth.userDetailsService(userDetailsService)
			.passwordEncoder(bCryptPasswordEncoder);
	}
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		
		web.ignoring()
		   .antMatchers("/resources/**", "/static/**", "/lib/**", "/favicon.ico",
				   		"/bootstrap/**", "/css/**", "/js/**");
	}
	
	@Override 

	public void configure(HttpSecurity http) throws Exception {
		
		http.csrf().disable()
			.authorizeRequests()
			
			/*
			// unrestricted access during development
			.antMatchers("/").permitAll() 
			.antMatchers("/experiments/**").permitAll()
			.antMatchers("/indexing/**").permitAll()
			.antMatchers("/admin/**").permitAll();

			*/
			
			.antMatchers("/participantLogout").permitAll()
			.antMatchers("/test").permitAll() // temporary
			.antMatchers("/experiments/**").hasAnyAuthority("EXPERIMENTER", "ADMIN")
			.antMatchers("/indexing/**").hasAnyAuthority("EXPERIMENTER", "ADMIN")
			.antMatchers("/admin/**").hasAuthority("ADMIN")
			.anyRequest()
			.authenticated().and().csrf().disable()
			.formLogin()
			.successHandler(loginHandler)
			.and()
			.logout()
			.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
			.logoutSuccessHandler(logoutHandler);
	}
	
	
}
