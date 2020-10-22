package ch.usi.hse.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import ch.usi.hse.db.repositories.AdministratorRepository;
import ch.usi.hse.db.repositories.ExperimenterRepository;
import ch.usi.hse.db.repositories.ParticipantRepository;
import ch.usi.hse.db.repositories.RoleRepository;
import ch.usi.hse.db.repositories.UserRepository;

public class UserServiceTest {

	@Mock
	private UserRepository userRepository;
	
	@Mock
	private AdministratorRepository administratorRepository;
	
	@Mock
	private ExperimenterRepository experimenterRepository;
	
	@Mock
	private ParticipantRepository participantRepository;
	
	@Mock
	private RoleRepository roleRepository;
	
	@Mock
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	private UserService testService;
	
	@BeforeEach
	public void setUp() {
		
		initMocks(this);
		
		testService = new UserService( userRepository,
									   administratorRepository,
									   experimenterRepository,
									   participantRepository,
									   roleRepository,
									   bCryptPasswordEncoder );
								
	}
	
	@Test
	public void testSetup() {
		
		assertNotNull(userRepository);
		assertNotNull(administratorRepository);
		assertNotNull(experimenterRepository);
		assertNotNull(participantRepository);
		assertNotNull(roleRepository);
		assertNotNull(bCryptPasswordEncoder);
		assertNotNull(testService);
	}
}

















