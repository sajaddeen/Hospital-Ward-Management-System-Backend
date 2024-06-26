package group17.HospitalWardManagementSystem.Service.WardDetails;

import group17.HospitalWardManagementSystem.Model.Domain.Matron;
import group17.HospitalWardManagementSystem.Model.Domain.Staff;
import group17.HospitalWardManagementSystem.Model.Domain.Ward;
import group17.HospitalWardManagementSystem.Model.Dto.WardDto.EditWardDto;
import group17.HospitalWardManagementSystem.Repository.MatronRepository;
import group17.HospitalWardManagementSystem.Repository.StaffRepository;
import group17.HospitalWardManagementSystem.Repository.WardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EditWardService {

    private final MatronRepository matronRepository;


    private final WardRepository wardRepository;
    @Autowired
    public EditWardService(MatronRepository matronRepository, WardRepository wardRepository) {
        this.matronRepository = matronRepository;
        this.wardRepository = wardRepository;
    }


    @Autowired
    private StaffRepository staffRepository;

    public String EditWard(String wardNo,EditWardDto editWardDto){
        //Ward ward=new Ward();

        Ward willUpdateWard=findWard(wardNo);

        willUpdateWard.setWardNo(editWardDto.getWardNo());
        willUpdateWard.setWardName(editWardDto.getWardName());
        willUpdateWard.setNumberOfNurses(editWardDto.getNumberOfNurses());

        willUpdateWard.setMatron(findMatron(editWardDto));

        wardRepository.save(willUpdateWard);

        return "Ward Details are successfully Updated";
    }

    public Matron findMatron(EditWardDto editWardDto){
        return matronRepository.findByNic(editWardDto.getMatron());
    }


    public Ward findWard(String wardNo){
        return wardRepository.findByWardNo(wardNo);
    }


}
