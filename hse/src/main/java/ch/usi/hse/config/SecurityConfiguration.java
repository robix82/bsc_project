package ch.usi.hse.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

	@Value("${baseUrl}")
	private String baseUrl;
	
	@Autowired
	private HseUserDetailsService userDetailsService;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private HseAuthenticationSuccessHandler loginHandler;
	
	@Autowired
	private HseAuthenticationFailureHandler loginFailureHandler;
	
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
			.headers().frameOptions().sameOrigin()
			.and()
			.authorizeRequests()			
			.antMatchers("/from_survey/**").permitAll()
			.antMatchers("/login").permitAll()
			.antMatchers("/participantLogout").permitAll()
			.antMatchers("/experiments/**").hasAnyAuthority("EXPERIMENTER", "ADMIN")
			.antMatchers("/indexing/**").hasAnyAuthority("EXPERIMENTER", "ADMIN")
			.antMatchers("/admin/**").hasAuthority("ADMIN")
			.anyRequest()
			.authenticated()
			.and()
			.formLogin()
			.loginPage(baseUrl + "login")
			.loginProcessingUrl("/login")
			.usernameParameter("user_name")
			.passwordParameter("password")
			.successHandler(loginHandler)
			.failureHandler(loginFailureHandler)
			.and()
			.logout()
			.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
			.logoutSuccessHandler(logoutHandler);
	}
	
	
}
