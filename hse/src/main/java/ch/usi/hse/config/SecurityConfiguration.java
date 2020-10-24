package ch.usi.hse.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Configuration for user authentication settings
 * 
 * @author robert.jans@usi.ch
 *
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Override
	public void configure(HttpSecurity http) throws Exception {
		
		http  .csrf().disable()
			.authorizeRequests()
			.antMatchers("/").permitAll() 
			
			// unrestricted access during development
			.antMatchers("/experiments/**").permitAll()
			.antMatchers("/indexing/**").permitAll()
			.antMatchers("/admin/**").permitAll();
			
			/*
			.antMatchers("/experiments/**").hasAuthority("EXPERIMENTER")
			.antMatchers("/indexing/**").hasAuthority("PARTICIPANT")
			.antMatchers("/admin/**").hasAuthority("ADMIN");
			*/
		
		/*
			.anyRequest()
			.authenticated().and().csrf().disable().formLogin()
			.loginPage("/login").failureUrl("/login?error=true")
			.defaultSuccessUrl("/", true)
			.usernameParameter("user_name")
			.passwordParameter("password")
			.and().logout()
			.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
			.logoutSuccessUrl("/logoutDone").and().exceptionHandling()
			.accessDeniedPage("/accessDenied");
			*/
	}
	
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoderEncoder() {
		return new BCryptPasswordEncoder();
	}
}
