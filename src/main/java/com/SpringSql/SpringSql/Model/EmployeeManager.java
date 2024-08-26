package com.SpringSql.SpringSql.Model;

import java.time.Instant;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


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

    private Instant dateOfJoining;

    private Instant createdTime;

    private Instant updatedTime;

}
