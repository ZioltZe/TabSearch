package eliott.backend.repository;

import eliott.backend.model.role.Role;
import eliott.backend.model.user.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends CrudRepository<Role, Long>
{
    Optional<Role> findByName(String email);

}
