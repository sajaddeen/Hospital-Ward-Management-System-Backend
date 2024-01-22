package group17.HospitalWardManagementSystem.Model.Dto;

import group17.HospitalWardManagementSystem.Model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {
    private String username;
    private String password;
    private String email;
    private UserRole position;
}
