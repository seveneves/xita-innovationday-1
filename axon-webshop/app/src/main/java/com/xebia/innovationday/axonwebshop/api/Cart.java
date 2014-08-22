package com.xebia.innovationday.axonwebshop.api;

import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;

public class Cart extends AbstractAnnotatedAggregateRoot<Cart> {

    @AggregateIdentifier
    private String id;

    Cart() {
    }

    public Cart(String id) {
        apply(new CartCreatedEvent(id));
    }

    @CommandHandler
    public void handle(AddItemCommand command) {
        apply(new ItemAddedEvent(id, command.getItem()));
    }

    @EventHandler
    public void on(CartCreatedEvent event) {
        id = event.getCartId();
    }

}
