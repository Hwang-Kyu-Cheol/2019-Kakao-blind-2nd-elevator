package com.company.domain;

import java.util.ArrayList;
import java.util.List;

public class Command {
    private int elevator_id;
    private Instruction command;
    private List<Integer> call_ids = new ArrayList<>();

    //===================CONSTRUCTOR===================//
    private Command(int elevator_id, Instruction command, List<Integer> call_ids){
        this.elevator_id = elevator_id;
        this.command = command;
        this.call_ids = call_ids;
    }
    //=================================================//

    //===================GETTER, SETTER===================//
    public int getElevator_id() {
        return elevator_id;
    }

    public void setElevator_id(int elevator_id) {
        this.elevator_id = elevator_id;
    }

    public Instruction getCommand() {
        return command;
    }

    public void setCommand(Instruction command) {
        this.command = command;
    }

    public List<Integer> getCall_ids() {
        return call_ids;
    }

    public void setCall_ids(List<Integer> call_ids) {
        this.call_ids = call_ids;
    }
    //====================================================//

    //======================METHOD======================//
    public static Command createCommand(int elevator_id, Instruction command, List<Integer> call_ids){
        return new Command(elevator_id, command, call_ids);
    }
    //===================================================//
}
