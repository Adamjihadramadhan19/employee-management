package com.example.employeemanagement.repository;

import com.example.employeemanagement.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    
    // Find by email
    Optional<Employee> findByEmail(String email);
    
    // Find by department
    List<Employee> findByDepartment(String department);
    
    // Find by first name or last name containing (case insensitive)
    @Query("SELECT e FROM Employee e WHERE " +
           "LOWER(e.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(e.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Employee> findByNameContaining(@Param("searchTerm") String searchTerm);
    
    // Find employees with salary greater than
    List<Employee> findBySalaryGreaterThan(Double salary);
    
    // Get all departments
    @Query("SELECT DISTINCT e.department FROM Employee e ORDER BY e.department")
    List<String> findAllDepartments();
    
    // Count employees by department
    @Query("SELECT e.department, COUNT(e) FROM Employee e GROUP BY e.department")
    List<Object[]> countEmployeesByDepartment();
    
    // Find employees ordered by hire date (newest first)
    List<Employee> findAllByOrderByHireDateDesc();
}