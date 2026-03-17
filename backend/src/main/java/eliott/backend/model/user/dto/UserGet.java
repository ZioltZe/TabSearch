package eliott.backend.model.user.dto;

import eliott.backend.model.user.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserGet
{
    private long id;
    private String email;

    public UserGet() {}

    public UserGet(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
    }
}
