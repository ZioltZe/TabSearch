package eliott.backend.controller;

import eliott.backend.model.role.Role;
import eliott.backend.model.spotify.Tab;
import eliott.backend.model.user.User;
import eliott.backend.model.user.dto.UserGet;
import eliott.backend.model.user.dto.UserNew;
import eliott.backend.model.user.dto.UserPass;
import eliott.backend.repository.UserRepository;
import eliott.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("")
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Iterable<UserGet> getUsers()
    {
        return ((List<User>) userRepository.findAll()).stream().map(UserGet::new).toList();
    }

    @PutMapping("/user/AddTab")
    public void AddTab(@RequestBody Tab tab)
    {
        userService.addTab(getUser(), tab);
    }

    @GetMapping("/user/Tabs")
    public List<Tab> GetTabs()
    {
        return getUser().getSpotifyUser().getLastVisitedTabs();
    }

    @DeleteMapping("/user/delete")
    public void removeUser()
    {
        userService.removeUser(getUser());
    }

    @PutMapping("/user/changePass")
    public boolean changePassword(@RequestBody UserPass pass)
    {
        return userService.changePassword(getUser(), pass.getOldPassword(), pass.getNewPassword(), pass.getRepeatPassword());
    }

    private User getUser()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return  userRepository.findByEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found"));
    }

    @PostMapping("/user/newUser")
    public boolean createNewUser(@RequestBody UserNew user)
    {
        return userService.signUpUser(user.getEmail(), user.getPassword(), user.getRepeatPassword());
    }

    @GetMapping("/user/roles")
    public Set<Role> getRoles()
    {
        return userService.getRoles(getUser());
    }
}
