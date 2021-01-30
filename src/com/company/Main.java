package com.company;

import com.company.api.Api;
import com.company.domain.*;
import com.company.json.request.Action;
import com.company.domain.Command;
import com.company.domain.Instruction;
import com.company.json.response.OnCalls;
import com.company.json.response.Start;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Main {

    private static List<Elevator> elevatorList = new ArrayList<>(); //엘리베이터 리스트
    private static List<Call> callList = new ArrayList<>(); //요청 리스트

    public static void main(String[] args) {

        //GSON : Json-Parser
        Gson gson = new Gson();

        //문제 세팅
        String user_key = "tester2";
        int problem_id = 2;
        int number_of_elevators = 4;
        int elevator_maxSize = 8;

        //api 세팅
        Api api = new Api(user_key, problem_id, number_of_elevators);

        //=============== START ===============//
        String responseStartJson = api.start();
        Start start = gson.fromJson(responseStartJson, Start.class);
        api.setToken(start.getToken());
        System.out.println("Token for user is " + start.getToken());
        //=====================================//

        //=============== 엘리베이터 초기화 ===============//
        for (Elevator elevator : start.getElevators()) {
            elevatorList.add(new Elevator(elevator, elevator_maxSize));
        }
        //=============================================//

        //=============== SIMULATION ===============//
        while(true) {

            //=============== ON_CALLS ===============//
            String responseOnCallsJson = api.on_calls();
            System.out.println("ON_CALLS = " + responseOnCallsJson);
            OnCalls onCalls = gson.fromJson(responseOnCallsJson, OnCalls.class);

            if(onCalls.isIs_end()){
                break;
            }
            //========================================//

            //============ SETTING ============//
            setting(onCalls.getElevators(), onCalls.getCalls());
            Action action = new Action();
            //=================================//

            for (Elevator elevator : elevatorList) {

                int elevator_id = elevator.getId();
                int elevator_floor = elevator.getFloor();
                List<Call> elevator_passengers = elevator.getPassengers();
                Status elevator_status = elevator.getStatus();
                Direction elevator_direction = elevator.getDirection();

                Instruction instruction = Instruction.STOP; //명령어
                List<Integer> call_ids = new ArrayList<>(); //요청 id

                switch(elevator_status){

                    //=====================================================================//
                    case STOPPED: //-> 명령 : STOP, OPEN, UP, DOWN

                        //엘리베이터에 사람X and 요청X -> STOP
                        if(elevator_passengers.isEmpty() && callList.isEmpty()){
                            instruction = Instruction.STOP;
                        }
                        //엘리베이터에서 내릴 사람이 있는 경우 or (엘리베이터가 가는 방향에 타는 사람이 있고 and 탈 수 있는 경우) -> OPEN
                        else if(elevator.isExitCalls() || (isEnterCallsWithSameDirection(elevator_floor, elevator_direction) && elevator.canEnter())){
                            instruction = Instruction.OPEN;
                        }
                        //엘리베이터 가는 방향으로 출발
                        else{
                            instruction = elevator.getDirection() == Direction.GoingUp ? Instruction.UP : Instruction.DOWN;
                        }
                        break;

                    //=====================================================================//
                    case OPENED: //-> 명령 : OPEN, ENTER, EXIT, CLOSE

                        //엘리베이터에서 내릴 사람이 있는 경우 -> EXIT
                        if(elevator.isExitCalls()){
                            instruction = Instruction.EXIT;
                            List<Call> exitCalls = elevator.findExitCalls();
                            for (Call exitCall : exitCalls) {
                                call_ids.add(exitCall.getId());
                            }
                        }
                        //엘리베이터에 탈 수 있고, 엘리베이터가 가는 방향에 타고 싶은 사람이 있는 경우 -> ENTER
                        else if(isEnterCallsWithSameDirection(elevator_floor, elevator_direction) && elevator.canEnter()){
                            instruction = Instruction.ENTER;
                            call_ids = findEnterCallsWithSameDirection(elevator_floor, elevator_direction, elevator_maxSize - elevator_passengers.size());
                        }
                        else{
                            instruction = Instruction.CLOSE;
                        }
                        break;

                    //=====================================================================//
                    case UPWARD: //-> 명령 : UP, STOP

                        //엘리베이터에 타고 있는 사람 중에 올라가고 싶은 사람이 있는 경우 or 엘리베이터보다 위에 요청이 있는 경우 -> 방향 유지
                        if(elevator.isExitCallsOnDirection() || isEnterCallsOnDirection(elevator_floor, elevator_direction)){
                            //내리고 싶은 사람이 있는 경우 or 엘리베이터에 탈 수 있고, 같은 방향에 타고 싶은 사람이 있는 경우 -> STOP
                            if(elevator.isExitCalls() || (isEnterCallsWithSameDirection(elevator_floor, elevator_direction) && elevator.canEnter())){
                                instruction = Instruction.STOP;
                            }else{
                                instruction = Instruction.UP;
                            }
                        }
                        //방향 : GoingDown 으로 바꿔야 하므로 -> STOP
                        else{
                            Direction elevator_opposite_direction = (elevator_direction == Direction.GoingUp) ? Direction.GoingDown : Direction.GoingUp;
                            elevator.setDirection(elevator_opposite_direction);

                            instruction = Instruction.STOP;
                        }
                        break;

                    //=====================================================================//
                    case DOWNWARD: //-> 명령 : DOWN, STOP

                        //엘리베이터에 타고 있는 사람 중에 내려가고 싶은 사람이 있는 경우 or 엘리베이터보다 아래에 요청이 있는 경우 -> 방향 유지
                        if(elevator.isExitCallsOnDirection() || isEnterCallsOnDirection(elevator_floor, elevator_direction)){
                            //내리고 싶은 사람이 있는 경우 or 엘리베이터에 탈 수 있고, 같은 방향에 타고 싶은 사람이 있는 경우 -> STOP
                            if(elevator.isExitCalls() || (isEnterCallsWithSameDirection(elevator_floor, elevator_direction) && elevator.canEnter())){
                                instruction = Instruction.STOP;
                            }else{
                                instruction = Instruction.DOWN;
                            }
                        }
                        //방향 : GoingUp 으로 바꿔야 하므로 -> STOP
                        else{
                            Direction elevator_opposite_direction = (elevator_direction == Direction.GoingUp) ? Direction.GoingDown : Direction.GoingUp;
                            elevator.setDirection(elevator_opposite_direction);

                            instruction = Instruction.STOP;
                        }
                        break;
                }

                //현재 엘리베이터 명령 생성
                Command command = Command.createCommand(elevator_id, instruction, call_ids);

                //액션에 추가
                action.addCommandToList(command);
            }

            //=============== ACTION ===============//
            String requestActionJson = gson.toJson(action);
            System.out.println("ACTION = " + requestActionJson);
            System.out.println("=======================================================");
            api.action(requestActionJson);
            //=====================================//

            //=============== SLEEP(초당 40번) ===============//
            try{
                Thread.sleep(25);
            }catch(Exception e){
                e.printStackTrace();
            }
            //===============================================//
        }

    }

    //초기 세팅
    public static void setting(List<Elevator> elevators, List<Call> calls){
        //Call 세팅
        for (Call call : calls) {
            Direction direction = call.getStart() < call.getEnd() ? Direction.GoingUp : Direction.GoingDown;
            call.setDirection(direction);
        }
        callList = calls; // <- 리스트에 담아주기

        //Elevator 세팅
        for (int i = 0; i < elevators.size(); i++) {
            Direction direction = elevatorList.get(i).getDirection();
            int maxSize = elevatorList.get(i).getMaxSize();

            elevators.get(i).setDirection(direction);
            elevators.get(i).setMaxSize(maxSize);

            //Call 리스트에서 현재 엘리베이터에 타고 있는 승객 없애기
            callList.removeAll(elevators.get(i).getPassengers());
        }
        elevatorList = elevators; // <- 리스트에 담아주기
    }

    //해당 층에 엘리베이터와 같은 방향의 요청이 있는지 확인
    public static boolean isEnterCallsWithSameDirection(int floor, Direction direction){
        for (Call call : callList) {
            if(call.getStart() == floor && call.getDirection() == direction){
                return true;
            }
        }
        return false;
    }

    //해당 층에 엘리베이터와 같은 방향의 요청 리스트 반환
    public static List<Integer> findEnterCallsWithSameDirection(int floor, Direction direction, int remain){
        List<Integer> passengerList = new ArrayList<>();

        int index = 0;
        for (Iterator<Call> iter = callList.iterator(); iter.hasNext();) {
            if(index == remain){
                break;
            }
            Call call = iter.next();
            if(call.getStart() == floor && call.getDirection() == direction){
                iter.remove();
                passengerList.add(call.getId());
                index++;
            }
        }
        return passengerList;
    }

    //엘리베이터가 가는 방향보다 앞에 요청이 있는지 확인
    public static boolean isEnterCallsOnDirection(int floor, Direction direction){
        boolean output = false;
        if(direction == Direction.GoingUp){
            for (Call call : callList) {
                if(call.getStart() > floor){
                    output = true;
                }
            }
        }else{
            for (Call call : callList) {
                if(call.getStart() < floor){
                    output = true;
                }
            }
        }
        return output;
    }
}
