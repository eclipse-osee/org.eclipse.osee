/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.core.environment.console;

import java.util.Collection;
import java.util.Hashtable;

public class ConsoleCommandManager implements ICommandManager{

	private final Hashtable<String, ConsoleCommand> cmdMap = new Hashtable<String, ConsoleCommand>(64);
		
	private boolean isShutdown = false;
	
	public void registerCommand(ConsoleCommand cmd) {
		if (!isShutdown) {
			cmdMap.put(cmd.getName(), cmd);
		} else {
			throw new IllegalStateException("Can't register command: This manager has been shutdown");
		}
	}
	
	public ConsoleCommand unregisterCommand(ConsoleCommand cmd){
	   if (!isShutdown) {
         return cmdMap.remove(cmd.getName());
      } else {
         throw new IllegalStateException("Can't register command: This manager has been shutdown");
      }
	}

	public ConsoleCommand getCommand(String name) {
		return cmdMap.get(name);
	}
	
    public Collection<ConsoleCommand> getCommands() {
        return cmdMap.values();
    }
    
    public void shutdown() {
    	isShutdown = true;
    	for (ConsoleCommand cmd : cmdMap.values()) {
    	   cmd.dispose();
    	}
    	cmdMap.clear();
    }
}
