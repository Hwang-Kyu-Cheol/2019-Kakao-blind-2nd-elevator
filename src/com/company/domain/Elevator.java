package com.company.domain;

import java.util.ArrayList;
import java.util.List;

public class Elevator {

    private int id;
    private int floor;
    private List<Call> passengers = new ArrayList<>();
    private Status status;
    private transient Direction direction;
    private transient int maxSize;

    //===================CONSTRUCTOR===================//
    public Elevator(Elevator elevator, int maxSize){
        this.setId(elevator.getId());
        this.setFloor(elevator.getFloor());
        this.setPassengers(elevator.getPassengers());
        this.setStatus(elevator.getStatus());
        this.setDirection(Direction.GoingUp);
        this.setMaxSize(maxSize);
    }
    //=================================================//

    //===================GETTER, SETTER===================//
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public List<Call> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<Call> passengers) {
        this.passengers = passengers;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }
    //====================================================//

    //======================METHOD======================//
    //엘리베이터에 탈 수 있는지 확인
    public boolean canEnter(){
        return this.getPassengers().size() < this.getMaxSize();
    }

    //엘리베이터에 내릴 승객이 있는지 확인
    public boolean isExitCalls(){
        for (Call passenger : passengers) {
            if(passenger.getEnd() == this.getFloor()){
                return true;
            }
        }
        return false;
    }

    //엘리베이터가 가는 방향보다 앞에
    public boolean isExitCallsOnDirection(){
        if(this.getDirection() == Direction.GoingUp){
            for (Call passenger : passengers) {
                if(passenger.getEnd() > this.getFloor()){
                    return true;
                }
            }
        }else{
            for (Call passenger : passengers) {
                if(passenger.getEnd() < this.getFloor()){
                    return true;
                }
            }
        }
        return false;
    }

    //엘리베이터에 내릴 승객 반환
    public List<Call> findExitCalls(){
        List<Call> passengerList = new ArrayList<>();
        for (Call passenger : passengers) {
            if(passenger.getEnd() == this.getFloor()){
                passengerList.add(passenger);
            }
        }
        return passengerList;
    }
    //===================================================//
}
