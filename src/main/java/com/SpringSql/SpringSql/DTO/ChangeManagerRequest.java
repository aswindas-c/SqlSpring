package com.SpringSql.SpringSql.DTO;


import lombok.Data;

@Data
public class ChangeManagerRequest {
    private Integer employeeId; 
    private Integer managerId;
    public ChangeManagerRequest(Integer employeeId, Integer managerId) {
        this.employeeId = employeeId;
        this.managerId = managerId;
    }
}
