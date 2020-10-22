package ch.usi.hse.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import ch.usi.hse.db.repositories.AdministratorRepository;
import ch.usi.hse.db.repositories.ExperimenterRepository;
import ch.usi.hse.db.repositories.ParticipantRepository;
import ch.usi.hse.db.repositories.RoleRepository;
import ch.usi.hse.db.repositories.UserRepository;

/**
 * Service class for accessing the User persistence layer
 * 
 * @author robert.jans@usi.ch
 *
 */
@Service
public class UserService {

	private UserRepository userRepository;
	private AdministratorRepository administratorRepository;
	private ExperimenterRepository experimenterRepository;
	private ParticipantRepository participantRepository;
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	public UserService(UserRepository userRepository,
					   AdministratorRepository administratorRepository,
					   ExperimenterRepository experimenterRepository,
					   ParticipantRepository participantRepository,
					   RoleRepository roleRepository,
					   BCryptPasswordEncoder bCryptPasswordEncoder) {
		
		this.userRepository = userRepository;
		this.administratorRepository = administratorRepository;
		this.experimenterRepository = experimenterRepository;
		this.participantRepository = participantRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}
}














