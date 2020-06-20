package com.example.webjpah2.controller;

import com.example.webjpah2.component.EmployeeModelAssembler;
import com.example.webjpah2.exception.EmployeeNotFoundException;
import com.example.webjpah2.payroll.Employee;
import com.example.webjpah2.payroll.EmployeeRepository;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;



import java.util.List;
import java.util.stream.Collectors;

@RestController
public class EmployeeController {

    @Autowired
    private EmployeeModelAssembler modelAssembler;

    private final EmployeeRepository employeeRepository;

    public EmployeeController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    //Aggregate root
    @GetMapping("/employees")
    public CollectionModel<EntityModel<Employee>> all() {

        List<EntityModel<Employee>> emps = employeeRepository.findAll().stream().map(
                modelAssembler::toModel
                /*
                employee -> EntityModel.of(employee,
                        linkTo(methodOn(EmployeeController.class).one(employee.getId())).withSelfRel(),
                        linkTo(methodOn(EmployeeController.class).all()).withRel("employees"))
                 */
                ).collect(Collectors.toList());

        return CollectionModel.of(emps,
                linkTo(methodOn(EmployeeController.class).all()).withSelfRel());
    }

    @PostMapping("/employees")
    public ResponseEntity<?> newEmp(@RequestBody Employee employee) {
        EntityModel<Employee> empModel = modelAssembler.toModel(employeeRepository.save(employee));
        return ResponseEntity.created(empModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(empModel);
    }  

    //Single item
    @GetMapping("/employees/{id}")
    public EntityModel<Employee> one(@PathVariable Long id) {
        Employee emp = employeeRepository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));

        return modelAssembler.toModel(emp);
    }

    @PutMapping("/employees/{id}")
    public ResponseEntity<?> replace(@RequestBody Employee emp, @PathVariable Long id) {
        Employee updEmp = employeeRepository.findById(id).map(
            employee -> {
                employee.setName(emp.getName());
                employee.setRole(emp.getRole());
                return employeeRepository.save(employee);
            })
                .orElseGet(() -> {
                   emp.setId(id);
                   return employeeRepository.save(emp);
                });
        EntityModel<Employee> empModel = modelAssembler.toModel(updEmp);
        return ResponseEntity.created(empModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(empModel);
    }

    @DeleteMapping("/employees/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        employeeRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

}
