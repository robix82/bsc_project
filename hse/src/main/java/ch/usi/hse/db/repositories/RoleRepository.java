package ch.usi.hse.db.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ch.usi.hse.db.entities.Role;

@Repository
public interface RoleRepository extends CrudRepository<Role, Integer> {

	public Role findById(int id);
	public Role findByRole(String role);
}
