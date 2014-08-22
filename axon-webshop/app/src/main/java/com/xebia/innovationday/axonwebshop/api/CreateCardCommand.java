package com.xebia.innovationday.axonwebshop.api;

import org.axonframework.commandhandling.annotation.TargetAggregateIdentifier;

public class CreateCardCommand {
    @TargetAggregateIdentifier
    private final String cartId;

    public CreateCardCommand(String cartId) {
        this.cartId = cartId;
    }

    public String getCartId() {
        return cartId;
    }

}
