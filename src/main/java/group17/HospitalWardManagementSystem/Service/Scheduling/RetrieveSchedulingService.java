package group17.HospitalWardManagementSystem.Service.Scheduling;

import group17.HospitalWardManagementSystem.Model.Domain.*;
import group17.HospitalWardManagementSystem.Model.Dto.Scheduling.CandidateListDto;
import group17.HospitalWardManagementSystem.Model.DutyTime;
import group17.HospitalWardManagementSystem.Model.LeaveStatus;
import group17.HospitalWardManagementSystem.Model.UserRole;
import group17.HospitalWardManagementSystem.Repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class RetrieveSchedulingService {

    private final ApprovedLeavesRepository approvedLeavesRepository;
    private final DutyRepository dutyRepository;
    private final WardRepository wardRepository;
    private final UserRepository userRepository;
    private final StaffRepository staffRepository;

    @Autowired
    public RetrieveSchedulingService(ApprovedLeavesRepository approvedLeavesRepository, DutyRepository dutyRepository, WardRepository wardRepository, UserRepository userRepository, StaffRepository staffRepository) {
        this.approvedLeavesRepository = approvedLeavesRepository;
        this.dutyRepository = dutyRepository;
        this.wardRepository = wardRepository;
        this.userRepository = userRepository;
        this.staffRepository = staffRepository;
    }

    public List<CandidateListDto> getCandidateNurseList(String nic, String shift, String date){
        Staff sister = staffRepository.findByNic(nic);
        User sisterU = userRepository.findByNic(nic).orElseThrow(() ->
                new EntityNotFoundException("Cannot find user with user nic: " + nic));
        if(sisterU.getPosition().equals(UserRole.Sister)){
            Ward ward = sister.getWardNo();

            List<Staff> newCandidateStaffMembers = new ArrayList<>();

            Set<Staff> staff = ward.getStaff();

            if(staff.isEmpty()){
                throw new IllegalArgumentException("Staff members are not assigned yet");
            }

            for(Staff selectedStaff : staff){

                if(isAvailable(selectedStaff, LocalDate.parse(date))){
                    if(shift.equals("Morning")){
                        if(checkAvailabilityByPreviousShift(selectedStaff,LocalDate.parse(date).minusDays(1), DutyTime.Evening)
                                && checkAvailabilityByPreviousShift(selectedStaff,LocalDate.parse(date).minusDays(1), DutyTime.Night)){

                            newCandidateStaffMembers.add(selectedStaff);
                        }
                    } else if (shift.equals("Evening")) {
                        if(checkAvailabilityByPreviousShift(selectedStaff,LocalDate.parse(date), DutyTime.Morning)
                                && checkAvailabilityByPreviousShift(selectedStaff,LocalDate.parse(date).minusDays(1), DutyTime.Night)){

                            newCandidateStaffMembers.add(selectedStaff);
                        }
                    } else if (shift.equals("Night")) {
                        if(checkAvailabilityByPreviousShift(selectedStaff,LocalDate.parse(date), DutyTime.Morning)
                                && checkAvailabilityByPreviousShift(selectedStaff,LocalDate.parse(date), DutyTime.Evening)){

                            newCandidateStaffMembers.add(selectedStaff);
                        }
                    }
                }
            }


            return mapToCandidateListDto(newCandidateStaffMembers);
        }else {
            throw new IllegalArgumentException("You are not recognize as a sister, Please contact admin!");
        }

    }

    private Boolean isAvailable(Staff staff, LocalDate date){
        List<ApprovedLeaves> approvedLeaves = approvedLeavesRepository.findLeavesByStaffAndDate(staff,date, LeaveStatus.Approved);
        return approvedLeaves.isEmpty();
    }

    private Boolean checkAvailabilityByPreviousShift(Staff staff, LocalDate date, DutyTime dutyTime){
        List<Duty> duties = dutyRepository.findDutiesByStaffAndDate(staff.getNic(), date, dutyTime);
        return duties.isEmpty();
    }

    private List<CandidateListDto> mapToCandidateListDto(List<Staff> staff){
        List<CandidateListDto> candidateListDtos = new ArrayList<>();
        for(Staff staff1: staff){
            Optional<User> user = userRepository.findById(staff1.getNic());
            String fullName = user.isPresent() ? user.get().getFullName() : "Full name unknown";
            LocalDate date = user.map(User::getCareerStatedDate).orElse(null);
            List <Duty> duties = dutyRepository.findDutiesByStaffAndWeek(staff1.getNic(), DateUtils.getStartOfWeek(), LocalDate.now());
            int totalWorkHours = duties.isEmpty() ? 0 : workingHours(duties);
            candidateListDtos.add(CandidateListDto.builder().nic(staff1.getNic()).fullName(fullName).serviceStartedDate(date).workingHours(totalWorkHours).build());
        }
        return candidateListDtos;
    }

    private int workingHours(List<Duty> duties){
        int totalHourse = 0;
        for (Duty duty: duties){
            if(duty.getDutyTime().equals(DutyTime.Night)){
                totalHourse += 12;
            }else {
                totalHourse += 6;
            }
        }

        return totalHourse;
    }
}


