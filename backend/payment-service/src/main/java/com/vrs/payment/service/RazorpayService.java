package com.vrs.payment.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import com.vrs.common.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@Slf4j
public class RazorpayService {

    private final RazorpayClient client;
    private final String keyId;
    private final String keySecret;
    private final String currency;

    public RazorpayService(@Value("${razorpay.key-id}") String keyId,
                           @Value("${razorpay.key-secret}") String keySecret,
                           @Value("${razorpay.currency:INR}") String currency) throws RazorpayException {
        this.keyId = keyId;
        this.keySecret = keySecret;
        this.currency = currency;
        this.client = new RazorpayClient(keyId, keySecret);
        log.info("Razorpay client initialised (test mode: keyId={}...{})",
                keyId.substring(0, Math.min(8, keyId.length())),
                keyId.substring(Math.max(0, keyId.length() - 4)));
    }

    public String getKeyId() { return keyId; }
    public String getCurrency() { return currency; }

    public Order createOrder(BigDecimal amount, String receipt) {
        try {
            JSONObject orderRequest = new JSONObject();
            int paise = amount.setScale(2, RoundingMode.HALF_UP).movePointRight(2).intValue();
            orderRequest.put("amount", paise);
            orderRequest.put("currency", currency);
            orderRequest.put("receipt", receipt);
            orderRequest.put("payment_capture", 1);
            return client.orders.create(orderRequest);
        } catch (RazorpayException e) {
            log.error("Razorpay order creation failed: {}", e.getMessage());
            throw ApiException.badRequest("Failed to create payment order: " + e.getMessage());
        }
    }

    public boolean verifySignature(String orderId, String paymentId, String signature) {
        try {
            JSONObject attributes = new JSONObject();
            attributes.put("razorpay_order_id", orderId);
            attributes.put("razorpay_payment_id", paymentId);
            attributes.put("razorpay_signature", signature);
            return Utils.verifyPaymentSignature(attributes, keySecret);
        } catch (RazorpayException e) {
            log.warn("Signature verification failed: {}", e.getMessage());
            return false;
        }
    }
}
