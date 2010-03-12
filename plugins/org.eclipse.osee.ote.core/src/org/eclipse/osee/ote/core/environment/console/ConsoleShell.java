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

import java.lang.ref.WeakReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public abstract class ConsoleShell {
	private static final Pattern CMD_PATTERN = Pattern.compile("^(\\S+)\\s*((-[a-zA-Z]\\S*\\s*)*)\\s*((\\S+(\\s+|$))*)");
	
	private final WeakReference<ICommandManager> manager;
	private volatile ConsoleCommand lastCmd;
	private volatile String lastSwitches;
	private volatile String lastArgs;
	private boolean saveLastCmdFlag = true;
	
	public ConsoleShell(ICommandManager manager) {
		this.manager = new WeakReference<ICommandManager>(manager);
	}
	
	public abstract void println(String string);
	public abstract void print(String string);
	public abstract void println();
	
	public ICommandManager getCommandManager() {
		return manager.get();
	}
	
	public void runLast() {
		lastCmd.execute(this, lastSwitches, lastArgs);
	}
	
	public final void parseAndExecuteCmd(String line) throws Throwable{
		final Matcher matcher =  CMD_PATTERN.matcher(line);
		if (matcher.matches()) {
			String cmdName = matcher.group(1);
			String switches = matcher.group(2);
			String args = matcher.group(4);
			final ConsoleCommand cmd = getCommandManager().getCommand(cmdName);
			if (cmd != null) {
				cmd.execute(this, switches, args);
				if (saveLastCmdFlag) {
					lastCmd = cmd;
					lastSwitches = switches;
					lastArgs = args;
				} else {
					// we do not save the last command as instructed but we reset the flag
					saveLastCmdFlag = true;
				}
			} else {
				println(cmdName + " is not recognized as a command");
			}
		} else {
			println("invalid command format");
		}
	}
	
	/**
	 * Prevents the currently running command from being saved as the last executed command. This
	 * is useful for commands that process the last executed command but do not want themselves
	 * to become the last command executed
	 * @param save
	 */
	public void setSaveLastCmdFlag(boolean save) {
		saveLastCmdFlag = save;
	}
    
    public void printStackTrace(Throwable t) {
       println(t.toString() + ":");
       StackTraceElement[] elements = t.getStackTrace();
       for (int i = 0; i < elements.length; i++) {
          println("   " + elements[i].toString());
       }
       t = t.getCause();
       while (t != null) {
          println("Caused by: " + t.toString());
          elements = t.getStackTrace();
          for (int i = 0; i < elements.length; i++) {
             println("   " + elements[i].toString());
          }
          t = t.getCause();
       }
    }
}
