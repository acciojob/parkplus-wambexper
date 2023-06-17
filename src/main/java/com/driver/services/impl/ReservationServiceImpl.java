package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    UserRepository userRepository3;
    @Autowired
    SpotRepository spotRepository3;
    @Autowired
    ReservationRepository reservationRepository3;
    @Autowired
    ParkingLotRepository parkingLotRepository3;
    @Override
    public Reservation reserveSpot(Integer userId, Integer parkingLotId, Integer timeInHours, Integer numberOfWheels) throws Exception {
        //Reserve a spot in the given parkingLot such that the total price is minimum. Note that the price per hour for each spot is different
        //Note that the vehicle can only be parked in a spot having a type equal to or larger than given vehicle
        //If parkingLot is not found, user is not found, or no spot is available, throw "Cannot make reservation" exception.

        ParkingLot parkingLot;
        Spot spot;
        User user;

        try{
            parkingLot=parkingLotRepository3.findById(parkingLotId).get();
        }catch (Exception e){

            throw new Exception("Cannot make reservation");
        }
        try{
            user=userRepository3.findById(userId).get();
        }catch (Exception e){
            throw new Exception("Cannot make reservation");
        }

        List<Spot> list = parkingLot.getSpotList();
        int f=0;
        int bill=Integer.MAX_VALUE;
        Spot sellectedSpot = null;


        for(Spot s : list){

            int temp = s.getPricePerHour() * timeInHours;
            if(numberOfWheels==2){
                if(bill>temp && s.getOccupied().equals(false)  ){
                    bill=temp;
                    sellectedSpot=s;
                    f=1;
                }
            }
            else if(numberOfWheels==4){
                if(bill>temp && s.getOccupied().equals(false)&& !s.getSpotType().equals(SpotType.TWO_WHEELER)){
                    bill=temp;
                    sellectedSpot=s;
                    f=1;
                }
            }else{
                if(bill>temp && s.getOccupied()==false && s.getSpotType().equals(SpotType.OTHERS));
                bill=temp;
                sellectedSpot=s;
                f=1;
            }

        }


        if(f==0){
            throw new Exception("Cannot make reservation");
        }


        Reservation reservation = new Reservation();
        reservation.setSpot(sellectedSpot);
        reservation.setUser(user);
        reservation.setNumberOfHours(timeInHours);
        reservation.setPayment(new Payment());

        List<Reservation> urList = user.getReservationList();
        urList.add(reservation);

        List<Reservation> spList = sellectedSpot.getReservationList();
        spList.add(reservation);

        sellectedSpot.setOccupied(true);

        userRepository3.save(user);
        spotRepository3.save(sellectedSpot);

        return reservation;
    }
}