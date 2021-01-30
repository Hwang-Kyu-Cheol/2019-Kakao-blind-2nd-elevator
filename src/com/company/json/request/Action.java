package com.company.json.request;

import com.company.domain.Command;

import java.util.ArrayList;
import java.util.List;

public class Action {

    private List<Command> commands = new ArrayList<>();

    //===================GETTER, SETTER===================//
    public List<Command> getCommands() {
        return commands;
    }

    public void setCommands(List<Command> commands) {
        this.commands = commands;
    }
    //====================================================//

    //======================METHOD======================//
    public void addCommandToList(Command command) {
        this.commands.add(command);
    }
    //===================================================//
}
