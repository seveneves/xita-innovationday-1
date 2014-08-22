package com.xebia.innovationday.axonwebshop.api;

import org.axonframework.commandhandling.annotation.TargetAggregateIdentifier;

public class CreateCartCommand {
    @TargetAggregateIdentifier
    private final String cartId;

    public CreateCartCommand(String cartId) {
        this.cartId = cartId;
    }

    public String getCartId() {
        return cartId;
    }

}
