package group17.HospitalWardManagementSystem.Repository;

import group17.HospitalWardManagementSystem.Model.Domain.Matron;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface MatronRepository extends JpaRepository<Matron,String> {
    Matron findByNic(String nic);

 @Query("SELECT m.nic FROM Matron m")
 List<String> findAllNic();
