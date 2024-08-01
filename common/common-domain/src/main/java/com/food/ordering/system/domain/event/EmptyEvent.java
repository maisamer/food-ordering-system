package com.food.ordering.system.domain.event;

public final class EmptyEvent implements DomainEvent<Void> {

    public static EmptyEvent INSTANT = new EmptyEvent();

    private EmptyEvent() {
    }

    @Override
    public void fire() {

    }
}
