package model;

import java.io.Serializable;

public class Command implements Serializable {

    private final CommandType type;
    private final String[] args;

    public Command(CommandType type, String... args) {
        this.type = type;
        this.args = args;
    }

    public CommandType getType() { return type; }
    public String[] getArgs()    { return args; }

    public String getArg(int index) {
        if (args == null || index >= args.length)
            throw new IllegalArgumentException("Missing arg[" + index + "]");
        return args[index];
    }
}