package com.example.employeemanagement.controller;

import com.example.employeemanagement.model.Employee;
import com.example.employeemanagement.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class EmployeeController {
    
    @Autowired
    private EmployeeService employeeService;
    
    // Home page - redirect to employee list
    @GetMapping("/")
    public String home() {
        return "redirect:/employees";
    }
    
    // Show all employees
    @GetMapping("/employees")
    public String listEmployees(Model model, @RequestParam(required = false) String search) {
        List<Employee> employees;
        
        if (search != null && !search.trim().isEmpty()) {
            employees = employeeService.searchEmployees(search);
            model.addAttribute("search", search);
        } else {
            employees = employeeService.getAllEmployees();
        }
        
        model.addAttribute("employees", employees);
        model.addAttribute("totalEmployees", employeeService.getTotalEmployees());
        model.addAttribute("averageSalary", String.format("%.2f", employeeService.getAverageSalary()));
        
        return "employee/list";
    }
    
    // Show employee details
    @GetMapping("/employees/{id}")
    public String showEmployee(@PathVariable Long id, Model model) {
        Optional<Employee> employee = employeeService.getEmployeeById(id);
        if (employee.isPresent()) {
            model.addAttribute("employee", employee.get());
            return "employee/detail";
        } else {
            return "redirect:/employees?error=Employee not found";
        }
    }
    
    // Show add employee form
    @GetMapping("/employees/add")
    public String showAddForm(Model model) {
        model.addAttribute("employee", new Employee());
        model.addAttribute("departments", employeeService.getAllDepartments());
        return "employee/add";
    }
    
    // Process add employee form
    @PostMapping("/employees/add")
    public String addEmployee(@Valid @ModelAttribute Employee employee, 
                             BindingResult result, 
                             Model model, 
                             RedirectAttributes redirectAttributes) {
        
        // Check if email already exists
        if (employeeService.emailExists(employee.getEmail())) {
            result.rejectValue("email", "error.employee", "Email already exists");
        }
        
        if (result.hasErrors()) {
            model.addAttribute("departments", employeeService.getAllDepartments());
            return "employee/add";
        }
        
        Employee savedEmployee = employeeService.saveEmployee(employee);
        redirectAttributes.addFlashAttribute("success", "Employee added successfully!");
        return "redirect:/employees/" + savedEmployee.getId();
    }
    
    // Show edit employee form
    @GetMapping("/employees/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Employee> employee = employeeService.getEmployeeById(id);
        if (employee.isPresent()) {
            model.addAttribute("employee", employee.get());
            model.addAttribute("departments", employeeService.getAllDepartments());
            return "employee/edit";
        } else {
            return "redirect:/employees?error=Employee not found";
        }
    }
    
    // Process edit employee form
    @PostMapping("/employees/{id}/edit")
    public String updateEmployee(@PathVariable Long id,
                                @Valid @ModelAttribute Employee employee,
                                BindingResult result,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        
        // Check if email exists for another employee
        Optional<Employee> existingEmployee = employeeService.getEmployeeById(id);
        if (existingEmployee.isEmpty()) {
            return "redirect:/employees?error=Employee not found";
        }
        
        // Check email uniqueness (excluding current employee)
        Optional<Employee> emailCheck = employeeService.getAllEmployees().stream()
                .filter(e -> e.getEmail().equals(employee.getEmail()) && !e.getId().equals(id))
                .findFirst();
        
        if (emailCheck.isPresent()) {
            result.rejectValue("email", "error.employee", "Email already exists");
        }
        
        if (result.hasErrors()) {
            model.addAttribute("departments", employeeService.getAllDepartments());
            return "employee/edit";
        }
        
        employee.setId(id);
        employee.setHireDate(existingEmployee.get().getHireDate()); // Keep original hire date
        employeeService.updateEmployee(employee);
        redirectAttributes.addFlashAttribute("success", "Employee updated successfully!");
        return "redirect:/employees/" + id;
    }
    
    // Delete employee
    @PostMapping("/employees/{id}/delete")
    public String deleteEmployee(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<Employee> employee = employeeService.getEmployeeById(id);
        if (employee.isPresent()) {
            employeeService.deleteEmployee(id);
            redirectAttributes.addFlashAttribute("success", "Employee deleted successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Employee not found");
        }
        return "redirect:/employees";
    }
    
    // Dashboard
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalEmployees", employeeService.getTotalEmployees());
        model.addAttribute("averageSalary", String.format("%.2f", employeeService.getAverageSalary()));
        model.addAttribute("departments", employeeService.getAllDepartments());
        model.addAttribute("recentEmployees", employeeService.getRecentEmployees().stream().limit(5).toList());
        return "dashboard";
    }
}