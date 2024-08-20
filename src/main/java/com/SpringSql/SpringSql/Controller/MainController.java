package com.SpringSql.SpringSql.Controller;


import org.springframework.web.bind.annotation.RestController;

import com.SpringSql.SpringSql.DTO.ChangeManagerRequest;
import com.SpringSql.SpringSql.DTO.ManagerResponse;
import com.SpringSql.SpringSql.DTO.Response;
import com.SpringSql.SpringSql.Model.EmployeeManager;
import com.SpringSql.SpringSql.Service.EmployeeService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class MainController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/addEmployee")
    public Response addEmployee(@Valid @RequestBody EmployeeManager employee) {
        return employeeService.addEmployee(employee);
    }

    @GetMapping("/getEmployee")
    public List<ManagerResponse> getEmployee(
            @RequestParam(required = false) Integer managerId,
            @RequestParam(required = false) Integer yearsOfExperience) {
        return employeeService.getEmployee(managerId, yearsOfExperience);
    }

    @DeleteMapping("/deleteEmployee")
    public Response deleteEmployee(@RequestParam Integer employeeId) {
        return employeeService.deleteEmployee(employeeId);
    }

    @PutMapping("/changeEmployeeManager")
    public Response changeEmployeeManager(
            @RequestBody ChangeManagerRequest request) {
        return employeeService.changeEmployeeManager(request.getEmployeeId(), request.getManagerId());
    }
}