package group17.HospitalWardManagementSystem.Model.Dto.ApproveLeave;

import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class PreviousLeaveDetailsDto {
    private int leaveId;
    private String name;
    private LocalDate leaveBeginDate;
    private String Reason;

}
