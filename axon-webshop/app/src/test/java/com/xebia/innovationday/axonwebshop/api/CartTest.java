package com.xebia.innovationday.axonwebshop.api;

import org.axonframework.test.FixtureConfiguration;
import org.axonframework.test.Fixtures;
import org.junit.Before;
import org.junit.Test;

public class CartTest {

    private static final String ITEM1 = "item1";
    private static final String CART_ID = "non-existent-id";
    private FixtureConfiguration fixture;

    @Before
    public void setUp() throws Exception {
        fixture = Fixtures.newGivenWhenThenFixture(Cart.class);
        CreateCartCommandHandler createCartCommandHandler = new CreateCartCommandHandler();
        createCartCommandHandler.setRepository(fixture.getRepository());
        fixture.registerAnnotatedCommandHandler(createCartCommandHandler);

        /*
         * // we'll store Events on the FileSystem, in the "events/" folder EventStore eventStore = new
         * FileSystemEventStore(new SimpleEventFileResolver(new File("./events")));
         * 
         * // a Simple Event Bus will do EventBus eventBus = new SimpleEventBus();
         * 
         * // we need to configure the repository EventSourcingRepository repository = new
         * EventSourcingRepository(Cart.class, eventStore); repository.setEventBus(eventBus);
         * 
         * fixture.registerRepository(repository);
         */}

    @Test
    public void createCart() {

        fixture.given()
            .when(new CreateCartCommand(CART_ID))
            .expectEvents(new CartCreatedEvent(CART_ID));
    }

    @Test
    public void createAlreadyExistingCart() {

        fixture.given(new CartCreatedEvent(CART_ID))
            .when(new CreateCartCommand(CART_ID))
            .expectEvents();
    }

    @Test
    public void addItemToCart() {

        fixture.given(new CartCreatedEvent(CART_ID))
            .when(new AddItemCommand(CART_ID, ITEM1))
            .expectEvents(new ItemAddedEvent(CART_ID, ITEM1));
    }


}
