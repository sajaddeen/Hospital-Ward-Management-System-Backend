package group17.HospitalWardManagementSystem.Model.Dto.PassWord;

import lombok.Data;

@Data
public class PasswordResetRequestDto {

    private String email;

    private String newPassword;

    private String confirmPassword;

    private String otp;
}
