package com.learn.app.demo.service;

import java.util.List;

import com.learn.app.demo.entity.Employee;
import com.learn.app.demo.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    private final EmployeeRepository repo;

    public EmployeeService(EmployeeRepository repo) {
        this.repo = repo;
    }

    public List<Employee> getAll() {
        return repo.findAll();
    }

    public Employee getById(Long id) {
        return repo.findById(id).orElse(null);
    }

    public Employee save(Employee emp) {
        return repo.save(emp);
    }

    public Employee update(Long id, Employee updated) {
        Employee existing = repo.findById(id).orElse(null);
        if (existing != null) {
            existing.setName(updated.getName());
            existing.setPosition(updated.getPosition());
            existing.setSalary(updated.getSalary());
            return repo.save(existing);
        }
        return null;
    }
}

