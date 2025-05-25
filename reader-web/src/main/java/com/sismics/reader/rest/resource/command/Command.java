// Command interface
package com.sismics.reader.rest.resource.command;

import java.util.jar.JarException;

/**
 * Command interface for executing operations.
 * 
 * @author Implementation
 */
public interface Command {
    /**
     * Execute the command.
     * 
     * @return Result of the command execution
     */
    Object execute() throws JarException;
}