package com.example.employeemanagement.controller;

import com.example.employeemanagement.model.Employee;
import com.example.employeemanagement.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "*") // Allow CORS for external frontend
public class EmployeeApiController {
    
    @Autowired
    private EmployeeService employeeService;
    
    // Get all employees
    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }
    
    // Get employee by ID
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        Optional<Employee> employee = employeeService.getEmployeeById(id);
        if (employee.isPresent()) {
            return ResponseEntity.ok(employee.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Create new employee
    @PostMapping
    public ResponseEntity<?> createEmployee(@Valid @RequestBody Employee employee) {
        try {
            // Check if email already exists
            if (employeeService.emailExists(employee.getEmail())) {
                return ResponseEntity.badRequest()
                    .body("Email already exists");
            }
            
            Employee savedEmployee = employeeService.saveEmployee(employee);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedEmployee);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body("Error creating employee: " + e.getMessage());
        }
    }
    
    // Update employee
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable Long id, 
                                          @Valid @RequestBody Employee employee) {
        try {
            Optional<Employee> existingEmployee = employeeService.getEmployeeById(id);
            if (existingEmployee.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            // Check email uniqueness (excluding current employee)
            Optional<Employee> emailCheck = employeeService.getAllEmployees().stream()
                    .filter(e -> e.getEmail().equals(employee.getEmail()) && !e.getId().equals(id))
                    .findFirst();
            
            if (emailCheck.isPresent()) {
                return ResponseEntity.badRequest()
                    .body("Email already exists");
            }
            
            employee.setId(id);
            employee.setHireDate(existingEmployee.get().getHireDate()); // Keep original hire date
            Employee updatedEmployee = employeeService.updateEmployee(employee);
            return ResponseEntity.ok(updatedEmployee);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body("Error updating employee: " + e.getMessage());
        }
    }
    
    // Delete employee
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
        try {
            Optional<Employee> employee = employeeService.getEmployeeById(id);
            if (employee.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            employeeService.deleteEmployee(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body("Error deleting employee: " + e.getMessage());
        }
    }
    
    // Search employees
    @GetMapping("/search")
    public ResponseEntity<List<Employee>> searchEmployees(@RequestParam String q) {
        List<Employee> employees = employeeService.searchEmployees(q);
        return ResponseEntity.ok(employees);
    }
    
    // Get employees by department
    @GetMapping("/department/{department}")
    public ResponseEntity<List<Employee>> getEmployeesByDepartment(@PathVariable String department) {
        List<Employee> employees = employeeService.getEmployeesByDepartment(department);
        return ResponseEntity.ok(employees);
    }
    
    // Get all departments
    @GetMapping("/departments")
    public ResponseEntity<List<String>> getAllDepartments() {
        List<String> departments = employeeService.getAllDepartments();
        return ResponseEntity.ok(departments);
    }
    
    // Get statistics
    @GetMapping("/stats")
    public ResponseEntity<?> getStatistics() {
        try {
            var stats = new Object() {
                public final long totalEmployees = employeeService.getTotalEmployees();
                public final double averageSalary = employeeService.getAverageSalary();
                public final List<String> departments = employeeService.getAllDepartments();
            };
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body("Error getting statistics: " + e.getMessage());
        }
    }
}