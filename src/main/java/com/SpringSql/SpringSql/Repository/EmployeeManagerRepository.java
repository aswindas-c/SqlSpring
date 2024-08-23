package com.SpringSql.SpringSql.Repository;

import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.SpringSql.SpringSql.Model.EmployeeManager;

@Repository
public interface EmployeeManagerRepository extends JpaRepository<EmployeeManager, Integer>{

    List<EmployeeManager> findByManagerIdAndDateOfJoiningBefore(Integer managerId, Instant minJoiningDate);

    List<EmployeeManager> findByManagerId(Integer managerId);

    List<EmployeeManager> findByDateOfJoiningBefore(Instant minJoiningDate);

    @Query("SELECT e FROM EmployeeManager e WHERE e.department = :department AND e.managerId = 0")
    List<EmployeeManager> findByDepartment(@Param("department") String department);

    @Query("SELECT COALESCE(MAX(e.id), 0) FROM EmployeeManager e")
    Integer findMaxId();

    @Query("SELECT e FROM EmployeeManager e WHERE e.id = :id")
    EmployeeManager findByIdCustom(@Param("id") Integer id);
}
