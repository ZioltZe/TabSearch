package eliott.backend.controller;

import eliott.backend.model.role.Role;
import eliott.backend.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/roles")
public class RoleController
{
    @Autowired
    private RoleRepository roleRepository;

    @GetMapping()
    public Iterable<Role> getRoles()
    {
        return roleRepository.findAll();
    }
}
