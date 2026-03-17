package eliott.backend.model.user.dto;

import lombok.Getter;

@Getter
public class UserNew
{
    private String email;
    private String password;
    private String repeatPassword;
}
