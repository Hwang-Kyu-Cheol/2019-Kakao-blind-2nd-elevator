package com.company.json.request;

import java.util.ArrayList;
import java.util.List;

public class Action {

    private List<Command> commands = new ArrayList<>();

    public List<Command> getCommands() {
        return commands;
    }

    public void setCommands(Command command) {
        this.commands.add(command);
    }
}
