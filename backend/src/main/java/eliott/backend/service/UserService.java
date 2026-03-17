package eliott.backend.service;

import eliott.backend.model.role.Role;
import eliott.backend.model.spotify.Tab;
import eliott.backend.model.user.User;
import eliott.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService
{
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleService roleService;

    public Set<Role> getRoles(User user)
    {
        return user.getRoles();
    }

    public boolean signUpUser(String email, String password, String repeatPass)
    {
        if (email == null || email.isEmpty())
            return false;

        if (password == null || password.isEmpty())
            return false;

        if (!password.equals(repeatPass))
            return false;

        Role role = roleService.findRole("ROLE_USER");

        Set<Role> roles = new HashSet<>(){{ add(role); }};
        createUser(email, password, roles);


        return true;
    }

    public void createUser(String email, String password, Set<Role> roles)
    {
        User user = new User(email, passwordEncoder.encode(password), roles);
        userRepository.save(user);
    }

    public void addTab(User user, Tab tab)
    {
        List<Tab> tabList = user.getSpotifyUser().getLastVisitedTabs();
        if (tabList.size() > 2)
            tabList.removeFirst();

        Tab newTab = new Tab()
        {{
            setTab(tab.isTab());
            setTabId(tab.getTabId());
            setSongArtist(tab.getSongArtist());
            setSongName(tab.getSongName());
        }};
        tabList.add(newTab);

        user.getSpotifyUser().setLastVisitedTabs(tabList);
        userRepository.save(user);
    }

    public boolean changePassword(User user, String password, String newPassword, String repeatPassword)
    {
        if (newPassword == null || newPassword.isEmpty())
            return false;

        if (!newPassword.equals(repeatPassword))
            return false;

        if (newPassword.equals(password))
            return false;

        String oldPasswordEncoded = user.getPassword();
        if (!passwordEncoder.matches(password, oldPasswordEncoded))
            return false;

        String newPasswordHash = passwordEncoder.encode(newPassword);
        user.setPassword(newPasswordHash);
        userRepository.save(user);
        return true;
    }

    public void removeUser(User user)
    {
        userRepository.delete(user);
    }
}
