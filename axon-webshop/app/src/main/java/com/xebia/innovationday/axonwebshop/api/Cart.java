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

    @CommandHandler
    public Cart(CreateCardCommand command) {
        apply(new CartCreatedEvent(command.getCartId()));
    }

    @EventHandler
    public void on(CartCreatedEvent event) {
        id = event.getCartId();
    }

}
