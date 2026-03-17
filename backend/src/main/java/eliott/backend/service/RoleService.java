package eliott.backend.service;

import eliott.backend.model.role.Role;
import eliott.backend.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RoleService
{
    @Autowired
    private RoleRepository roleRepository;

    public Role createRole(String roleName)
    {
        Role role = new Role(roleName);
        return roleRepository.save(role);
    }

    public Role findRole(String roleName)
    {
        return roleRepository.findByName(roleName).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role Not Found"));
    }
}
