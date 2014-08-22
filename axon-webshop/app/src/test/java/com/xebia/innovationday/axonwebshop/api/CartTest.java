package com.xebia.innovationday.axonwebshop.api;

import org.axonframework.test.FixtureConfiguration;
import org.axonframework.test.Fixtures;
import org.junit.Before;
import org.junit.Test;

public class CartTest {

    private static final String ITEM1 = "item1";
    private static final String NON_EXISTENT_ID = "non-existent-id";
    private FixtureConfiguration fixture;

    @Before
    public void setUp() throws Exception {
        fixture = Fixtures.newGivenWhenThenFixture(Cart.class);
    }

    @Test
    public void createCart() {

        fixture.given()
            .when(new CreateCardCommand(NON_EXISTENT_ID))
            .expectEvents(new CartCreatedEvent(NON_EXISTENT_ID));
    }

    @Test
    public void addItemToCart() {

        fixture.given(new CartCreatedEvent(NON_EXISTENT_ID))
            .when(new AddItemCommand(NON_EXISTENT_ID, ITEM1))
            .expectEvents(new ItemAddedEvent(NON_EXISTENT_ID, ITEM1));
    }


}
