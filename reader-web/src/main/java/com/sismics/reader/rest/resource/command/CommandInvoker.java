// CommandInvoker.java
package com.sismics.reader.rest.resource.command;

import java.util.jar.JarException;

/**
 * Invoker for commands.
 */
public class CommandInvoker {
    /**
     * Execute a command.
     * 
     * @param command Command to execute
     * @return Result of command execution
     */
    public Object executeCommand(Command command) throws JarException {
        return command.execute();
    }
}

