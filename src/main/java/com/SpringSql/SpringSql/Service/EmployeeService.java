package com.SpringSql.SpringSql.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.SpringSql.SpringSql.DTO.ManagerResponse;
import com.SpringSql.SpringSql.DTO.Response;
import com.SpringSql.SpringSql.Model.EmployeeManager;
import com.SpringSql.SpringSql.Repository.EmployeeManagerSqlRepository;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.management.openmbean.KeyAlreadyExistsException;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeManagerSqlRepository employeeSqlRepo;

    public Response addEmployee(EmployeeManager employee) {

        String email = employee.getEmail();
        String designation = employee.getDesignation();
        String mobileNumber = employee.getMobile();
        String department = employee.getDepartment();


        // Validate employee data
        validateEmployeeData(email, designation, mobileNumber, department);
        
        // Find the maximum ID of employee
        Integer maxId = employeeSqlRepo.findMaxId();
        if(maxId != null) {
            employee.setId(maxId + 1);
        } else {
            employee.setId(1);
        }

        // Check if employee with the same ID already exists
        if (employeeSqlRepo.existsById(employee.getId())) {
            throw new KeyAlreadyExistsException("Employee ID already exists.");
        }

        // Set created time and updated time to the current time
        Instant currentTime = Instant.now();
        employee.setCreatedTime(currentTime);
        employee.setUpdatedTime(currentTime);

        // Handle special case for Account Manager
        if ("Account Manager".equalsIgnoreCase(employee.getDesignation())) {
            if (employee.getManagerId() != 0) {
                throw new IllegalArgumentException("Account Manager must have Manager ID set to 0. Employee cannot be added.");
            }

            // Check if a manager already exists in the department            
            List<EmployeeManager> existingManagers = employeeSqlRepo.findByDepartment(employee.getDepartment());

            if (!existingManagers.isEmpty()) {
                throw new KeyAlreadyExistsException("A manager already exists in the department: " + employee.getDepartment());
            }

            employeeSqlRepo.save(employee);
            return new Response("Employee added as Manager successfully with ID: " + employee.getId());
        } else {
            // Handle non-Account Manager 
            if (employee.getManagerId() == 0) {
                throw new IllegalArgumentException("Manager ID 0 should have designation as Account Manager. Employee cannot be added.");
            }
        }

        // Handle normal employee
        EmployeeManager manager = employeeSqlRepo.findById(employee.getManagerId());
        if (manager == null) {
                throw new NoSuchElementException("Manager with ID " + employee.getManagerId() + " not found. Employee cannot be added.");
        }
        if (manager.getManagerId() != 0) {
            throw new NoSuchElementException("Employee with ID " + employee.getManagerId() + " is not a manager. Employee cannot be added.");
        }

        // Check if employee and manager are in the same department
        if (!employee.getDepartment().equalsIgnoreCase(manager.getDepartment())) {
            throw new IllegalArgumentException("Employee and manager must belong to the same department. Employee cannot be added.");
        }

        // Save the employee
        employeeSqlRepo.save(employee);

        return new Response("Employee added successfully under Manager with ID: " + manager.getId());
    }

    public void validateEmployeeData(String email, String designation, String mobileNumber, String department) {
        List<String> errors = new ArrayList<>();
        
        if (!"Account Manager".equalsIgnoreCase(designation) && !"associate".equalsIgnoreCase(designation)) {
            errors.add("Designation can only be Account Manager or associate.");
        }
     
        if (!"sales".equalsIgnoreCase(department) &&
                !"delivery".equalsIgnoreCase(department) &&
                !"QA".equalsIgnoreCase(department) &&
                !"engineering".equalsIgnoreCase(department) &&
                !"BA".equalsIgnoreCase(department)) {
            errors.add("Invalid department. Must be one of: sales,delivery,QA,engineering,BA.");
        }
     
        if (!isValidEmail(email)) {
            errors.add("Invalid email format.");
        }
        
        if (mobileNumber.length() != 10 || !mobileNumber.matches("\\d+")) {
            errors.add("Invalid mobile number. It must be a 10-digit number.");
        }
        
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join(", ", errors));
        }
    }

    private Instant dateConverter(String yearOfExperience) {
        return OffsetDateTime.now().minusYears(Long.parseLong(yearOfExperience)).toInstant();
    }

    public List<ManagerResponse> getEmployee(Integer managerId, Integer yearsOfExperience) {
        List<EmployeeManager> employees;

        if (managerId != null && yearsOfExperience != null) 
        {
            Instant minJoiningDate =  dateConverter(yearsOfExperience.toString());
            employees = employeeSqlRepo.findByManagerIdAndDateOfJoiningBefore(managerId, minJoiningDate);
        } 
        else if (managerId != null) 
        {
            employees = employeeSqlRepo.findByManagerId(managerId);
        } 
        else if (yearsOfExperience != null) 
        {
            
            Instant minJoiningDate =  dateConverter(yearsOfExperience.toString());
            employees = employeeSqlRepo.findByDateOfJoiningBefore(minJoiningDate);
        } 
        else 
        {
            employees = employeeSqlRepo.findAll();
        }

        // Filter out employee with managerId of 0
        employees = employees.stream()
                .filter(emp -> emp.getManagerId() != 0)
                .collect(Collectors.toList());

        if (employees.isEmpty()) {
            throw new NoSuchElementException("No Employee found.");
        }

        // Group employees by manager ID
        List<ManagerResponse> responseList = employees.stream()
                .collect(Collectors.groupingBy(EmployeeManager::getManagerId))
                .entrySet().stream()
                .map(entry -> {
                    Integer currentManagerId = entry.getKey();
                    List<EmployeeManager> employeeList = entry.getValue();
                    EmployeeManager manager = employeeSqlRepo.findById(currentManagerId);

                    return new ManagerResponse(
                            "Successfully fetched",
                            manager.getName(),
                            manager.getDepartment(),
                            currentManagerId,
                            employeeList
                    );
                })
                .collect(Collectors.toList());

        return responseList;
    }

    public Response changeEmployeeManager(Integer employeeId, Integer newManagerId) {
        // Fetch the employee
        EmployeeManager employee = employeeSqlRepo.findById(employeeId);
        if (employee == null) {
            throw new NoSuchElementException("Employee with ID " + employeeId + " not found.");
        }
    
        if (employee.getManagerId() == 0) {
            throw new IllegalStateException("Employee is a manager cannot be changed");
        }

        
        if (employee.getManagerId().equals(newManagerId)) {
            throw new IllegalStateException("Employee is currently under the given manager. No changes required.");
        }
    
        // Fetch the new manager
        EmployeeManager newManager = employeeSqlRepo.findById(newManagerId);
        if (newManager == null || newManager.getManagerId()!=0) {
            throw new NoSuchElementException("New manager with ID " + newManagerId + " not found.");
        }
    
        if (!employee.getDepartment().equalsIgnoreCase(newManager.getDepartment())) {
            employee.setDepartment(newManager.getDepartment());
        }
    
        // Build the response
        String originalManagerName = employeeSqlRepo.findById(employee.getManagerId()).getName();
        String newManagerName = newManager.getName();

        // Update the employee manager ID and updatedTime
        employee.setManagerId(newManagerId);
        employee.setUpdatedTime(Instant.now());
        employeeSqlRepo.save(employee);
    
        return new Response(
                employee.getName() + "'s manager has been successfully changed from " + originalManagerName + " to " + newManagerName + "."
        );
    }
    
    public Response deleteEmployee(Integer employeeId) {
        // Check if the employee exists
        EmployeeManager employee = employeeSqlRepo.findById(employeeId);
        if (employee == null) {
            throw new NoSuchElementException("Employee with ID " + employeeId + " not found.");
        }
    
        List<EmployeeManager> subordinates = employeeSqlRepo.findByManagerId(employeeId);
        if (!subordinates.isEmpty()) {
            throw new IllegalStateException("Cannot delete Employee with ID " + employeeId + " as they are a manager with subordinates.");
        }
    
        // Delete the employee
        employeeSqlRepo.delete(employee);
        return new Response("Successfully deleted " + employee.getName() + " from the organization.");
    }    

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
