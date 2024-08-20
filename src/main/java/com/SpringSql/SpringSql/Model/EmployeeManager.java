package com.SpringSql.SpringSql.Model;

import java.time.OffsetDateTime;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//@Document(collection = "trial")
@Entity
@Table(name = "employeeManager")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeManager {
    
    @Id
    private Integer id;

    private String name;

    private String designation;

    private String email;

    private String department;

    private String mobile;

    private String location;

    private Integer managerId;

    private OffsetDateTime dateOfJoining;

    private OffsetDateTime createdTime;

    private OffsetDateTime updatedTime;

    private Integer yearsOfExperience;

}
