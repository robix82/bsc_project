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
	
	@Autowired
	private javax.sql.DataSource dataSource;
	
	@Override 
	public void configure(HttpSecurity http) throws Exception {
		
		http  .csrf().disable()
			.authorizeRequests()
			
			
			
			
			// unrestricted access during development

	/*
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
	//		.loginPage("/login").failureUrl("/login?error=true")
			.defaultSuccessUrl("/", true)
	//		.usernameParameter("user_name")
	//		.passwordParameter("password")
			.and().logout()
			.logoutRequestMatcher(new AntPathRequestMatcher("/logout"));
	//		.logoutSuccessUrl("/logoutDone").and().exceptionHandling()
	//		.accessDeniedPage("/accessDenied");

	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		auth.userDetailsService(userDetailsService)
			.passwordEncoder(bCryptPasswordEncoder());
		
		auth.jdbcAuthentication().dataSource(dataSource);

	}
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		
		web.ignoring()
		   .antMatchers("/resources/**", "/static/**", "/lib/**", "/favicon.ico",
				   		"/bootstrap/**", "/css/**", "/js/**");
	}
	
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
