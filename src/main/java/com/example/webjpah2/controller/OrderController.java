package com.example.webjpah2.controller;

import com.example.webjpah2.component.OrderModelAssembler;
import com.example.webjpah2.exception.OrderNotFoundException;
import com.example.webjpah2.payroll.Order;
import com.example.webjpah2.payroll.OrderRepository;
import com.example.webjpah2.payroll.Status;
import org.apache.coyote.Response;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class OrderController {

    @Autowired
    private OrderRepository repository;

    @Autowired
    private OrderModelAssembler modelAssembler;

    @GetMapping("/orders")
    public CollectionModel<EntityModel<Order>> all() {

        List<EntityModel<Order>> orders = repository.findAll().stream().map(modelAssembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(orders,
                linkTo(methodOn(OrderController.class).all()).withSelfRel());
    }

    @GetMapping("/orders/{id}")
    public EntityModel<Order> one(@PathVariable Long id) {
        Order order = repository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));

        return modelAssembler.toModel(order);
    }

    @PostMapping("/orders")
    public ResponseEntity<EntityModel<Order>> newOrder(@RequestBody Order order) {
        order.setStatus(Status.IN_PROGRESS);
        Order newOrd = repository.save(order);

        return ResponseEntity.created(linkTo(methodOn(OrderController.class).one(newOrd.getId())).toUri())
                            .body(modelAssembler.toModel(newOrd));
    }


    @DeleteMapping("/orders/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable Long id) {

        return inprogressSwitch(id, Status.CANCELLED);
    }

    @PutMapping("/orders/{id}/complete")
    public ResponseEntity<?> complete(@PathVariable Long id) {

        return inprogressSwitch(id, Status.COMPLETED);
    }

    private ResponseEntity<?> inprogressSwitch(Long ordId, Status newStatus) {

        Order order = repository.findById(ordId).orElseThrow(() -> new OrderNotFoundException(ordId));

        if (order.getStatus() == Status.IN_PROGRESS) {
            order.setStatus(newStatus);
            return ResponseEntity.ok(modelAssembler.toModel(repository.save(order)));
        }

        return methodNotAllowed(order.getStatus());
    }

    private ResponseEntity<?> methodNotAllowed(Status status) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
                .body(Problem.create()
                        .withTitle("Method not allowed")
                        .withDetail("You can't cancel an order that is in the " + status + "status!"));
    }
}
