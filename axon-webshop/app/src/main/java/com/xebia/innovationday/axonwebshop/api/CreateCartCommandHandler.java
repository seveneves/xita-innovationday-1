package com.xebia.innovationday.axonwebshop.api;

import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateCartCommandHandler {
    private final static Logger logger = LoggerFactory.getLogger(CreateCartCommandHandler.class);

    private Repository<Cart> repository;

    public void setRepository(Repository<Cart> repository) {
        this.repository = repository;
    }

    @CommandHandler
    public void handle(CreateCartCommand command) {
        logger.warn("received CreateCart {}", command.getCartId());
        try {
            repository.load(command.getCartId());
            logger.warn("cart {} already exists", command.getCartId());
        } catch (Exception e) {
            Cart cart = new Cart(command.getCartId());
            repository.add(cart);
            logger.warn("added cart {} to repository", command.getCartId());
        }
    }
}
