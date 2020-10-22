package ch.usi.hse.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import ch.usi.hse.db.entities.Administrator;
import ch.usi.hse.db.entities.Experimenter;
import ch.usi.hse.db.entities.Participant;
import ch.usi.hse.db.entities.User;
import ch.usi.hse.db.repositories.AdministratorRepository;
import ch.usi.hse.db.repositories.ExperimenterRepository;
import ch.usi.hse.db.repositories.ParticipantRepository;
import ch.usi.hse.db.repositories.RoleRepository;
import ch.usi.hse.db.repositories.UserRepository;
import ch.usi.hse.exceptions.NoSuchUserException;

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
	
	// count and find 
	
	/**
	 * 
	 * @return long total number of saved users
	 */
	public long userCount() {
		
		return userRepository.count();
	}
	
	/**
	 * 
	 * @return long total number of saved administrators
	 */
	public long administratorCount() {
		
		return administratorRepository.count();
	}
	
	/**
	 * 
	 * @return long total number of saved experimenters
	 */
	public long experimenterCount() {
		
		return experimenterRepository.count();
	}
	
	/**
	 * 
	 * @return long total number of saved participants
	 */
	public long participantCount() {
		
		return participantRepository.count();
	}
	
	/**
	 * 
	 * @return List<User> containing all saved users
	 */
	public List<User> allUsers() {
		
		return userRepository.findAll();
	}
	
	/**
	 * 
	 * @return List<Administrator> containing all saved administrators
	 */
	public List<Administrator> allAdministrators() {
		
		return administratorRepository.findAll();
	}
	
	/**
	 * 
	 * @return List<Experimenter> containing all saved experimenters
	 */
	public List<Experimenter> allExperimenters() {
		
		return experimenterRepository.findAll();
	}
	
	/**
	 * 
	 * @return List<Particcipant> containing all saved participants
	 */
	public List<Participant> allParticipants() {
		
		return participantRepository.findAll();
	}
	
	public User findUser(int id) throws NoSuchUserException {
		
		if (! idExists(id)) {
			throw new NoSuchUserException(id);
		}
		
		return userRepository.findById(id);
	}
	
	public User findUser(String userName) throws NoSuchUserException {
		
		if (! userNameExists(userName)) {
			throw new NoSuchUserException(userName);
		}
		
		return userRepository.findByUserName(userName);
	}
	
	// save new users
	
	
	
	// private utility methods
	
	private boolean idExists(int id) {
		
		return userRepository.existsById(id);
	}
	
	private boolean userNameExists(String userName) {
		
		return userRepository.existsByUserName(userName);
	}
}














