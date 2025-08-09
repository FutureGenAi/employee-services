package com.dev.employee.controller;

import com.dev.employee.entity.Employee;
import com.dev.employee.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired
    private EmployeeService service;

    @PostMapping
    public Employee createEmployee(@RequestBody Employee employee) {
        logger.info("Creating employee: {}", employee.getName());
        return service.createEmployee(employee);
    }

    @GetMapping
    public List<Employee> getAllEmployees() {
        logger.info("Fetching all employees");
        return service.getAllEmployees();
    }

    @GetMapping("/{id}")
    public Employee getEmployeeById(@PathVariable Long id) {
        logger.info("Fetching employee with ID: {}", id);
        return service.getEmployeeById(id);
    }

    @PutMapping("/{id}")
    public Employee updateEmployee(@PathVariable Long id, @RequestBody Employee employeeDetails) {
        logger.info("Updating employee with ID: {}", id);
        return service.updateEmployee(id, employeeDetails);
    }

    @DeleteMapping("/{id}")
    public void deleteEmployee(@PathVariable Long id) {
        logger.info("Deleting employee with ID: {}", id);
        service.deleteEmployee(id);
    }
}

