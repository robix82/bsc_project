package ch.usi.hse.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
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
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		auth.userDetailsService(userDetailsService)
			.passwordEncoder(bCryptPasswordEncoder());
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

			.antMatchers("/experiments/**").hasAnyAuthority("EXPERIMENTER", "ADMIN")
			.antMatchers("/indexing/**").hasAnyAuthority("EXPERIMENTER", "ADMIN")
			.antMatchers("/admin/**").hasAuthority("ADMIN")
			.anyRequest()
			.authenticated().and().csrf().disable().formLogin()
			.defaultSuccessUrl("/", true)
			.and().logout()
			.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
			.logoutSuccessUrl("/login");

		//	.and().exceptionHandling();
		//	.accessDeniedPage("/accessDenied");			
		//	.usernameParameter("user_name")
		//	.passwordParameter("password")
	//		.loginPage("/login").failureUrl("/login?error=true")
	//		.usernameParameter("user_name")
	//		.passwordParameter("password")



	}
	
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
