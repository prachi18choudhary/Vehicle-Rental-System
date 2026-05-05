package com.vrs.booking.event;

import com.vrs.common.event.BookingCancelledEvent;
import com.vrs.common.event.BookingCreatedEvent;

public interface BookingEventPublisher {
    void publishBookingCreated(BookingCreatedEvent event);
    void publishBookingConfirmed(BookingCreatedEvent event);
    void publishBookingCancelled(BookingCancelledEvent event);
}
