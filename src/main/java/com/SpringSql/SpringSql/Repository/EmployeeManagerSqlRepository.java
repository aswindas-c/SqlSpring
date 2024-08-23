package com.SpringSql.SpringSql.Repository;


import java.time.Instant;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.SpringSql.SpringSql.Model.EmployeeManager;

@Repository
public class EmployeeManagerSqlRepository {

    @Autowired
    private EmployeeManagerRepository empManRepo;

    public List<EmployeeManager> findByManagerIdAndDateOfJoiningBefore(Integer managerId, Instant minJoiningDate) {
        return empManRepo.findByManagerIdAndDateOfJoiningBefore(managerId, minJoiningDate);
    }

    public List<EmployeeManager> findByManagerId(Integer managerId) {
        return empManRepo.findByManagerId(managerId);
    }

    public List<EmployeeManager> findByDateOfJoiningBefore(Instant minJoiningDate) {
        return empManRepo.findByDateOfJoiningBefore(minJoiningDate);
    }

    public List<EmployeeManager> findByDepartment(String department) {
        return empManRepo.findByDepartment(department);
    }

    public Integer findMaxId() {
        return empManRepo.findMaxId();
    }

    public EmployeeManager save(EmployeeManager employeeManager) {
        return empManRepo.save(employeeManager);
    }

    public void delete(EmployeeManager employeeManager) {
        empManRepo.delete(employeeManager);
    }

    public boolean existsById(Integer id) {
        return empManRepo.existsById(id);
    }

    public List<EmployeeManager> findAll() {
        return empManRepo.findAll();
    }

    public EmployeeManager findById(Integer id) {
        return empManRepo.findByIdCustom(id);
    }
}
