package eliott.backend.model.user.dto;

import lombok.Getter;

@Getter
public class UserPass
{
    private String oldPassword;
    private String newPassword;
    private String repeatPassword;
}
