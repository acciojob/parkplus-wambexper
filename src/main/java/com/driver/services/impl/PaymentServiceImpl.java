package com.driver.services.impl;

import com.driver.model.Payment;
import com.driver.model.PaymentMode;
import com.driver.model.Reservation;
import com.driver.model.Spot;
import com.driver.repository.PaymentRepository;
import com.driver.repository.ReservationRepository;
import com.driver.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    ReservationRepository reservationRepository2;
    @Autowired
    PaymentRepository paymentRepository2;

    @Override
    public Payment pay(Integer reservationId, int amountSent, String mode) throws Exception {

        Reservation reservation = reservationRepository2.findById(reservationId).get();
        Spot spot = reservation.getSpot();
        Payment payment=reservation.getPayment();


        if(mode.equals("card")) payment.setPaymentMode(PaymentMode.CARD);
        else if(mode.equals("cash")) payment.setPaymentMode(PaymentMode.CASH);
        else if (mode.equals("upi")) payment.setPaymentMode(PaymentMode.UPI);
        else throw new Exception("Payment mode not detected");

        int bill = spot.getPricePerHour() * reservation.getNumberOfHours();
        if(bill>amountSent) throw new Exception("Insufficient Amount");

        payment.setPaymentCompleted(true);
        amountSent = amountSent-bill;
        spot.setOccupied(false);

        reservation.setPayment(payment);
        payment.setReservation(reservation);
        paymentRepository2.save(payment);
        reservationRepository2.save(reservation);

        return payment;
    }
}