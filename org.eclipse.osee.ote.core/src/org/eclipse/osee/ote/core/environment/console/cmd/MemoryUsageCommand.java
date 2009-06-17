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
package org.eclipse.osee.ote.core.environment.console.cmd;

import java.text.NumberFormat;
import org.eclipse.osee.ote.core.environment.console.ConsoleCommand;
import org.eclipse.osee.ote.core.environment.console.ConsoleShell;


/**
 * 
 * @author Ken J. Aguilar
 *
 */
public class MemoryUsageCommand extends ConsoleCommand{
	private static final String NAME = "mem";
	private static final String DESCRIPTION = "outputs the system memory used by this JVM";
	
	public MemoryUsageCommand() {
		super(NAME, DESCRIPTION);
	}

	public void doCmd(ConsoleShell shell, String[] switches, String[] args) {
       
       print("JVM Heap space allocated: " + NumberFormat.getInstance().format(Runtime.getRuntime().totalMemory()) + "\n");
       print("JVM Heap space used:" + NumberFormat.getInstance().format(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) + "\n");
	}

}
