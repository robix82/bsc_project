package ch.usi.hse.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import ch.usi.hse.db.entities.Administrator;
import ch.usi.hse.db.entities.Experimenter;
import ch.usi.hse.db.entities.Participant;
import ch.usi.hse.db.entities.Role;
import ch.usi.hse.db.entities.User;
import ch.usi.hse.db.repositories.AdministratorRepository;
import ch.usi.hse.db.repositories.ExperimenterRepository;
import ch.usi.hse.db.repositories.ParticipantRepository;
import ch.usi.hse.db.repositories.RoleRepository;
import ch.usi.hse.db.repositories.UserRepository;
import ch.usi.hse.exceptions.NoSuchUserException;
import ch.usi.hse.exceptions.UserExistsException;

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
	private RoleRepository roleRepository;
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
		this.roleRepository = roleRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}
	
	// COUNT SAVVED USERS
	
	/**
	 * returns the total number of saved users
	 * 
	 * @return long 
	 */
	public long userCount() {
		
		return userRepository.count();
	}
	
	/**
	 * returns the total number of saved administrators
	 * 
	 * @return long
	 */
	public long administratorCount() {
		
		return administratorRepository.count();
	}
	
	/**
	 * return the total number of saved experimenters
	 * 
	 * @return long
	 */
	public long experimenterCount() {
		
		return experimenterRepository.count();
	}
	
	/**
	 * returns the total number of saved participants
	 * 
	 * @return long 
	 */
	public long participantCount() {
		
		return participantRepository.count();
	}
	
	// RETRIEVE USER LISTS
	
	/**
	 * returns all saved users
	 * 
	 * @return List<User>
	 */
	public List<User> allUsers() {
		
		return userRepository.findAll();
	}
	
	/**
	 * returns all saved administrators
	 * 
	 * @return List<Administrator>
	 */
	public List<Administrator> allAdministrators() {
		
		return administratorRepository.findAll();
	}
	
	/**
	 * returns all saved experimenters
	 * 
	 * @return List<Experimenter>
	 */
	public List<Experimenter> allExperimenters() {
		
		return experimenterRepository.findAll();
	}
	
	/**
	 * returns all saved participants
	 * 
	 * @return List<Participant>
	 */
	public List<Participant> allParticipants() {
		
		return participantRepository.findAll();
	}
	
	// RETRIEVE SINGLE USERS
	
	/**
	 * returns a saved User given its id
	 * 
	 * @param id
	 * @return User
	 * @throws NoSuchUserException
	 */
	public User findUser(int id) throws NoSuchUserException {
		
		if (! userRepository.existsById(id)) {
			throw new NoSuchUserException(id);
		}
		
		return userRepository.findById(id);
	}
	
	/**
	 * returns a saved User given its user name
	 * 
	 * @param userName
	 * @return User
	 * @throws NoSuchUserException
	 */
	public User findUser(String userName) throws NoSuchUserException {
		
		if (! userRepository.existsByUserName(userName)) {
			throw new NoSuchUserException(userName);
		}
		
		return userRepository.findByUserName(userName);
	}

	/**
	 * returns a saved Administrator given its id
	 * 
	 * @param id
	 * @return Administrator
	 * @throws NoSuchUserException
	 */
	public Administrator findAdministrator(int id) throws NoSuchUserException {
		
		if (! administratorRepository.existsById(id)) {
			throw new NoSuchUserException("administrator", id);
		}
		
		return administratorRepository.findById(id);
	}
	
	/**
	 * returns a saved Administrator given its user name
	 * 
	 * @param userName
	 * @return Administrator
	 * @throws NoSuchUserException
	 */
	public Administrator findAdministrator(String userName) throws NoSuchUserException {
		
		if (! administratorRepository.existsByUserName(userName)) {
			throw new NoSuchUserException("administrator", userName);
		}
		
		return administratorRepository.findByUserName(userName);
	}
	
	/**
	 * returns a saved Experimenter given its id
	 * 
	 * @param id
	 * @return Experimenter
	 * @throws NoSuchUserException
	 */
	public Experimenter findExperimenter(int id) throws NoSuchUserException {
		
		if (! experimenterRepository.existsById(id)) {
			throw new NoSuchUserException("experimenter", id);
		}
		
		return experimenterRepository.findById(id);
	}
	
	/**
	 * returns a saved Experimenter given its user name
	 * 
	 * @param userName
	 * @return Experimenter
	 * @throws NoSuchUserException
	 */
	public Experimenter findExperimenter(String userName) throws NoSuchUserException {
		
		if (! experimenterRepository.existsByUserName(userName)) {
			throw new NoSuchUserException("experimenter", userName);
		}
		
		return experimenterRepository.findByUserName(userName);
	}
	
	/**
	 * returns a saved Participant given its id
	 * 
	 * @param id
	 * @return Participant
	 * @throws NoSuchUserException
	 */
	public Participant findParticipant(int id) throws NoSuchUserException {
		
		if (! participantRepository.existsById(id)) {
			throw new NoSuchUserException("participant", id);
		}
		
		return participantRepository.findById(id);
	}
	
	/**
	 * returns a saved Participant given its user name
	 * 
	 * @param userName
	 * @return Participant
	 * @throws NoSuchUserException
	 */
	public Participant findParticipant(String userName) throws NoSuchUserException {
		
		if (! participantRepository.existsByUserName(userName)) {
			throw new NoSuchUserException("participant", userName);
		}
		
		return participantRepository.findByUserName(userName);
	}
	

	// SAVE NEW USERS
	
	/**
	 * Creates a new Administrator and adds it to the database
	 * 
	 * @param userName
	 * @param password
	 * @return Administrator
	 * @throws UserExistsException
	 */
	public Administrator addAdministrator(String userName, String password)
		throws UserExistsException {
		
		if (userRepository.existsByUserName(userName)) {
			throw new UserExistsException(userName);
		}
		
		Set<Role> roles = new HashSet<>();
		roles.add(roleRepository.findByRole("ADMIN"));
		
		String pwd = bCryptPasswordEncoder.encode(password);
		
		Administrator newAdmin = new Administrator(userName, pwd, roles);
		
		Administrator saved = administratorRepository.save(newAdmin);
		
		return saved;
	}
	
	/**
	 * Creates a new Experimenter and adds it to the database
	 * 
	 * @param userName
	 * @param password
	 * @return Experimenter
	 * @throws UserExistsException
	 */
	public Experimenter addExperimenter(String userName, String password) 
		throws UserExistsException {
		
		if (userRepository.existsByUserName(userName)) {
			throw new UserExistsException(userName);
		}
		
		Set<Role> roles = new HashSet<>();
		roles.add(roleRepository.findByRole("EXPERIMENTER"));

		String pwd = bCryptPasswordEncoder.encode(password);
		
		Experimenter newExperimenter = new Experimenter(userName, pwd, roles);

		Experimenter saved = experimenterRepository.save(newExperimenter);
		
		return saved;
	}
	
	/**
	 * Creates a new Participant and adds it to the database
	 * 
	 * @param userName
	 * @param password
	 * @return Participant
	 * @throws UserExistsException
	 */
	public Participant addParticipant(String userName, String password) 
		throws UserExistsException {
		
		if (userRepository.existsByUserName(userName)) {
			throw new UserExistsException(userName);
		}
		
		Set<Role> roles = new HashSet<>();
		roles.add(roleRepository.findByRole("PARTICIPANT"));
		
		String pwd = bCryptPasswordEncoder.encode(password);
		
		Participant newParticipant = new Participant(userName, pwd, roles);
		
		Participant saved = participantRepository.save(newParticipant);
		
		return saved;
	}
	
	// UPDATE EXISTING USERS
	
	/**
	 * Updates an administrator given an Administrator instance with matching id
	 * 
	 * @param administrator
	 * @return Administrator
	 * @throws NoSuchUserException
	 * @throws UserExistsException
	 */
	public Administrator updateAdministrator(Administrator administrator) 
		throws NoSuchUserException, UserExistsException {
		
		int id = administrator.getId();
		
		if (! administratorRepository.existsById(id)) {
			throw new NoSuchUserException("administrator", id);
		}
		
		Administrator found = administratorRepository.findById(id);
		
		// Update userName
		
		String name = administrator.getUserName();
		
		if (! name.equals(found.getUserName())) {
			
			if (userRepository.existsByUserName(name)) {
				throw new UserExistsException(name);
			}
			
			found.setUserName(name);
		}
		
		// update password
		
		String password = administrator.getPassword();
		
		if (! password.equals(found.getPassword())) {

			found.setPassword(bCryptPasswordEncoder.encode(password));
		}
		
		Administrator updated = administratorRepository.save(found);
		
		return updated;
	}
	
	/**
	 * Updates an experimenter given an Experimenter instance with matching id
	 * 
	 * @param experimenter
	 * @return Experimenter
	 * @throws NoSuchUserException
	 * @throws UserExistsException
	 */
	public Experimenter updateExperimenter(Experimenter experimenter) 
		throws NoSuchUserException, UserExistsException {
		
		int id = experimenter.getId();
		
		if (! experimenterRepository.existsById(id)) {
			throw new NoSuchUserException("experimenter", id);
		}
		
		Experimenter found = experimenterRepository.findById(id);
		
		// Update userName
		
		String name = experimenter.getUserName();
		
		if (! name.equals(found.getUserName())) {
			
			if (userRepository.existsByUserName(name)) {
				throw new UserExistsException(name);
			}
			
			found.setUserName(name);
		}
		
		// update password
		
		String password = experimenter.getPassword();
		
		if (! password.equals(found.getPassword())) {

			found.setPassword(bCryptPasswordEncoder.encode(password));
		}
		
		Experimenter updated = experimenterRepository.save(found);
		
		return updated;
	}
	
	/**
	 * Updates an participant given a Participant instance with matching id
	 * 
	 * @param participant
	 * @return Participant
	 * @throws NoSuchUserException
	 * @throws UserExistsException
	 */
	public Participant updateParticipant(Participant participant) 
		throws NoSuchUserException, UserExistsException {
		
		int id = participant.getId();
		
		if (! participantRepository.existsById(id)) {
			throw new NoSuchUserException("experimenter", id);
		}
		
		Participant found = participantRepository.findById(id);
		
		// Update userName
		
		String name = participant.getUserName();
		
		if (! name.equals(found.getUserName())) {
			
			if (userRepository.existsByUserName(name)) {
				throw new UserExistsException(name);
			}
			
			found.setUserName(name);
		}
		
		// update password
		
		String password = participant.getPassword();
		
		if (! password.equals(found.getPassword())) {

			found.setPassword(bCryptPasswordEncoder.encode(password));
		}
		
		Participant updated = participantRepository.save(found);
		
		return updated;
	}
	
	// DELETE SAVED USERS
	
	/**
	 * removes the user with the given id
	 * 
	 * @param id
	 * @throws NoSuchUserException
	 */
	public void removeUser(int id) throws NoSuchUserException {
		
		if (! userRepository.existsById(id)) {
			throw new NoSuchUserException(id);
		}
		
		User u = userRepository.findById(id);
		userRepository.delete(u);
	}
	
	/**
	 * removes the user with the given user name
	 * 
	 * @param userName
	 * @throws NoSuchUserException
	 */
	public void removeUser(String userName) throws NoSuchUserException {
		
		if (! userRepository.existsByUserName(userName)) {
			throw new NoSuchUserException(userName);
		}
		
		User u = userRepository.findByUserName(userName);
		userRepository.delete(u);
	}
	
}














