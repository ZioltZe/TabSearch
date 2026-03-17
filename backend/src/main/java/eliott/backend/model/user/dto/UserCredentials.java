package eliott.backend.model.user.dto;

import eliott.backend.model.user.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCredentials
{
    private String email;
    private String password;

    public UserCredentials(){}

    public UserCredentials(User user)
    {
        this.email = user.getEmail();
        this.password = user.getPassword();
    }
}
