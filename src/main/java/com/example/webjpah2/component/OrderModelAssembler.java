package com.example.webjpah2.component;

import com.example.webjpah2.controller.OrderController;
import com.example.webjpah2.payroll.Order;
import com.example.webjpah2.payroll.Status;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class OrderModelAssembler implements RepresentationModelAssembler<Order, EntityModel<Order>> {

    @Override
    public EntityModel<Order> toModel(Order entity) {

        //Unconditional links to single-item resource and aggregate root
        EntityModel<Order> model = EntityModel.of(entity,
                linkTo(methodOn(OrderController.class).one(entity.getId())).withSelfRel(),
                linkTo(methodOn(OrderController.class).all()).withRel("orders"));

        //Conditional links based on state of the order
        if (entity.getStatus() == Status.IN_PROGRESS) {
            model.add(linkTo(methodOn(OrderController.class).cancel(entity.getId())).withRel("cancel"));
            model.add(linkTo(methodOn(OrderController.class).complete(entity.getId())).withRel("complete"));
        }

        return model;

    }
}
