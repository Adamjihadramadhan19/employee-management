package com.example.employeemanagement.service;

import com.example.employeemanagement.model.Employee;
import com.example.employeemanagement.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    // Get all employees
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }
    
    // Get employee by ID
    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }
    
    // Save employee
    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }
    
    // Update employee
    public Employee updateEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }
    
    // Delete employee
    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }
    
    // Check if email exists
    public boolean emailExists(String email) {
        return employeeRepository.findByEmail(email).isPresent();
    }
    
    // Search employees by name
    public List<Employee> searchEmployees(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllEmployees();
        }
        return employeeRepository.findByNameContaining(searchTerm.trim());
    }
    
    // Get employees by department
    public List<Employee> getEmployeesByDepartment(String department) {
        return employeeRepository.findByDepartment(department);
    }
    
    // Get all departments
    public List<String> getAllDepartments() {
        return employeeRepository.findAllDepartments();
    }
    
    // Get total number of employees
    public long getTotalEmployees() {
        return employeeRepository.count();
    }
    
    // Get average salary
    public double getAverageSalary() {
        List<Employee> employees = getAllEmployees();
        if (employees.isEmpty()) {
            return 0.0;
        }
        return employees.stream()
                .mapToDouble(Employee::getSalary)
                .average()
                .orElse(0.0);
    }
    
    // Get recent employees (ordered by hire date)
    public List<Employee> getRecentEmployees() {
        return employeeRepository.findAllByOrderByHireDateDesc();
    }
}