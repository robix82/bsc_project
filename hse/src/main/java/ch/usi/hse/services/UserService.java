package ch.usi.hse.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import ch.usi.hse.db.entities.Administrator;
import ch.usi.hse.db.entities.Experiment;
import ch.usi.hse.db.entities.Experimenter;
import ch.usi.hse.db.entities.Participant;
import ch.usi.hse.db.entities.Role;
import ch.usi.hse.db.entities.TestGroup;
import ch.usi.hse.db.entities.HseUser;
import ch.usi.hse.db.repositories.AdministratorRepository;
import ch.usi.hse.db.repositories.ExperimentRepository;
import ch.usi.hse.db.repositories.ExperimenterRepository;
import ch.usi.hse.db.repositories.ParticipantRepository;
import ch.usi.hse.db.repositories.RoleRepository;
import ch.usi.hse.db.repositories.TestGroupRepository;
import ch.usi.hse.db.repositories.UserRepository;
import ch.usi.hse.exceptions.NoSuchTestGroupException;
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
	private TestGroupRepository testGroupRepository;
	private ExperimentRepository experimentRepository;
	private RoleRepository roleRepository;
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	public static String surveyUserPassword = "xxxyyyzzz";
	 
	@Autowired 
	public UserService(UserRepository userRepository, 
					   AdministratorRepository administratorRepository,
					   ExperimenterRepository experimenterRepository,
					   ParticipantRepository participantRepository,
					   TestGroupRepository testGroupRepository,
					   ExperimentRepository experimentRepository,
					   RoleRepository roleRepository,
					   BCryptPasswordEncoder bCryptPasswordEncoder) {
		
		this.userRepository = userRepository;
		this.administratorRepository = administratorRepository;
		this.experimenterRepository = experimenterRepository;
		this.participantRepository = participantRepository;
		this.testGroupRepository = testGroupRepository;
		this.experimentRepository = experimentRepository;
		this.roleRepository = roleRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}
	
	// COUNT SAVED USERS
	
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
	 * returns true if a user with the given id exists, false otherwise
	 * 
	 * @param id
	 * @return
	 */
	public boolean userExists(int id) {
		return userRepository.existsById(id);
	}
	
	/**
	 * returns true if a user with the given username exists, false otherwise
	 * 
	 * @param name
	 * @return
	 */
	public boolean userExists(String name) {
		return userRepository.existsByUserName(name);
	}
	
	/**
	 * returns all saved users
	 * 
	 * @return List<User>
	 */
	public List<HseUser> allUsers() {
		
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
	 * returns a saved user given its id
	 * 
	 * @param id
	 * @return HseUser
	 * @throws NoSuchUserException
	 */
	public HseUser findUser(int id) throws NoSuchUserException {
		
		if (! userRepository.existsById(id)) {
			throw new NoSuchUserException(id);
		}

		if (administratorRepository.existsById(id)) {
			return administratorRepository.findById(id);
		}
		else if (experimenterRepository.existsById(id)) {
			return experimenterRepository.findById(id);
		}
		else if (participantRepository.existsById(id)) {
			return participantRepository.findById(id);
		}
		else {
			return userRepository.findById(id);
		}	
	}
	
	/**
	 * returns a saved User given its user name
	 * 
	 * @param userName
	 * @return User
	 * @throws NoSuchUserException
	 */
	public HseUser findUser(String userName) throws NoSuchUserException {
		
		if (! userRepository.existsByUserName(userName)) {
			throw new NoSuchUserException(userName);
		}
		
		if (administratorRepository.existsByUserName(userName)) {
			return administratorRepository.findByUserName(userName);
		}
		else if (experimenterRepository.existsByUserName(userName)) {
			return experimenterRepository.findByUserName(userName);
		}
		else if (participantRepository.existsByUserName(userName)) {
			return participantRepository.findByUserName(userName);
		}
		else {
			return userRepository.findByUserName(userName);
		}
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
	 * @param administrator
	 * @return Administrator
	 * @throws UserExistsException
	 */
	public Administrator addAdministrator(Administrator administrator)
		throws UserExistsException {
		
		String userName = administrator.getUserName();
		
		if (userRepository.existsByUserName(userName)) {
			throw new UserExistsException(userName);
		}
		
		Set<Role> roles = new HashSet<>();
		roles.add(roleRepository.findByRole("ADMIN"));
		roles.add(roleRepository.findByRole("EXPERIMENTER"));
		
		String pwd = bCryptPasswordEncoder.encode(administrator.getPassword());
		
		Administrator newAdmin = new Administrator(userName, pwd);
		newAdmin.setRoles(roles);
		
		Administrator saved = administratorRepository.save(newAdmin);
		 
		return saved;
	}
	
	/**
	 * Creates a new Experimenter and adds it to the database
	 * 
	 * @param experimenter
	 * @return Experimenter
	 * @throws UserExistsException
	 */
	public Experimenter addExperimenter(Experimenter experimenter) 
		throws UserExistsException {
		
		String userName = experimenter.getUserName();
		
		if (userRepository.existsByUserName(userName)) {
			throw new UserExistsException(userName);
		}
		
		Set<Role> roles = new HashSet<>();
		roles.add(roleRepository.findByRole("EXPERIMENTER"));

		String pwd = bCryptPasswordEncoder.encode(experimenter.getPassword());
		
		Experimenter newExperimenter = new Experimenter(userName, pwd);
		newExperimenter.setRoles(roles);

		Experimenter saved = experimenterRepository.save(newExperimenter);
		
		return saved;
	}
	
	/**
	 * Creates a new Participant and adds it to the database
	 * 
	 * @param participant
	 * @return Participant
	 * @throws UserExistsException
	 */
	public Participant addParticipant(Participant participant) 
		throws UserExistsException {
		
		String userName = participant.getUserName();
		
		if (userRepository.existsByUserName(userName)) {
			throw new UserExistsException(userName);
		}
		
		Set<Role> roles = new HashSet<>();
		roles.add(roleRepository.findByRole("PARTICIPANT"));
		
		String pwd = bCryptPasswordEncoder.encode(participant.getPassword());
		
		Participant newParticipant = new Participant(userName, pwd);
		newParticipant.setRoles(roles);
		newParticipant.setExperimentTitle(participant.getExperimentTitle());
		newParticipant.setTestGroupName(participant.getTestGroupName());
		
		Participant saved = participantRepository.save(newParticipant);
		
		return saved;
	}
	
	/**
	 * adds a new participant to the database and sets its user name and password to defaults
	 * 
	 * @param groupId
	 * @return
	 * @throws UserExistsException
	 * @throws NoSuchTestGroupException
	 */
	public Participant addSurveyParticipant(int groupId) 
			throws UserExistsException, NoSuchTestGroupException {
		
		String userName = "svu_" + RandomStringUtils.random(20, true, true);
		Participant participant;
		
		if (! testGroupRepository.existsById(groupId)) {
			throw new NoSuchTestGroupException(groupId);
		}
		
		if (! participantRepository.existsByUserName(userName)) {
			
			if (userRepository.existsByUserName(userName)) {
				throw new UserExistsException(userName);
			}
			
			String pwd = bCryptPasswordEncoder.encode(surveyUserPassword);
			
			Participant p = new Participant(userName, pwd);
			Set<Role> roles = new HashSet<>();
			roles.add(roleRepository.findByRole("PARTICIPANT"));
			p.setRoles(roles);
			participant = participantRepository.save(p);
		}
		else {
			
			participant = participantRepository.findByUserName(userName);
		}
		
		
		participant.setActive(true);
		participantRepository.save(participant);	
		
		TestGroup g = testGroupRepository.findById(groupId);
		g.addParticipant(participant);
		testGroupRepository.save(g);
		
		Participant updatedParticipant = participantRepository.findById(participant.getId());
		
		return updatedParticipant;
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
	 * Updates a participant given a Participant instance with matching id
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
		
		found.setExperimentTitle(participant.getExperimentTitle());
		found.setTestGroupName(participant.getTestGroupName());
		
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
		
		if (administratorRepository.existsById(id)) {
			
			removeAdministrator(administratorRepository.findById(id));
		}
		else if (experimenterRepository.existsById(id)) {
			
			removeExperimenter(experimenterRepository.findById(id));
		}
		else if (participantRepository.existsById(id)) {
			
			removeParticipant(participantRepository.findById(id));
		}
		else {
		
			HseUser u = userRepository.findById(id);
			userRepository.delete(u);
		}
	}
	
	/**
	 * removes all saved experimenters
	 * 
	 */
	public void clearExperimenters() {
		
		experimenterRepository.deleteAll();
	}
	
	/**
	 * removes all saved participants
	 * 
	 */
	public void clearParticipants() {
		
		for (TestGroup g : testGroupRepository.findAll()) {
			
			g.clearParticipants();
		}
		
		participantRepository.deleteAll();
	}
	
	/**
	 * deletes the given administrator
	 * 
	 * @param a
	 */
	private void removeAdministrator(Administrator administrator) {
		
		administratorRepository.delete(administrator);
	}

	/**
	 * deletes the given experimenter and its experiments
	 * 
	 * @param e
	 */
	private void removeExperimenter(Experimenter experimenter) {
		
		for (Experiment exp : experimenter.getExperiments()) {
			
			Experiment found = experimentRepository.findById(exp.getId());
			experimentRepository.delete(found);
		}
		
		experimenterRepository.delete(experimenter);
	}
	
	/**
	 * deletes the given participant
	 * 
	 * @param participant
	 */
	private void removeParticipant(Participant participant) {
		
		TestGroup g = participant.getTestGroup();
		
		if (g != null) {

			g.removeParticipant(participant);
			testGroupRepository.save(g);
		}
		else {
			
			participantRepository.delete(participant);
		}
	}
}














