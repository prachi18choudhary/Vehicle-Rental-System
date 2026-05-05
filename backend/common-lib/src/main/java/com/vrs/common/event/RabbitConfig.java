package com.vrs.common.event;

public final class RabbitConfig {

    public static final String BOOKING_EXCHANGE = "booking.exchange";
    public static final String PAYMENT_EXCHANGE = "payment.exchange";
    public static final String NOTIFICATION_EXCHANGE = "notification.exchange";

    public static final String RK_BOOKING_CREATED = "booking.created";
    public static final String RK_BOOKING_CONFIRMED = "booking.confirmed";
    public static final String RK_BOOKING_CANCELLED = "booking.cancelled";

    public static final String RK_PAYMENT_SUCCESS = "payment.success";
    public static final String RK_PAYMENT_FAILED = "payment.failed";

    public static final String Q_NOTIFICATION_BOOKING = "q.notification.booking";
    public static final String Q_NOTIFICATION_PAYMENT = "q.notification.payment";
    public static final String Q_BOOKING_PAYMENT = "q.booking.payment";
    public static final String Q_VEHICLE_BOOKING_CANCELLED = "q.vehicle.booking-cancelled";

    private RabbitConfig() {}
}
