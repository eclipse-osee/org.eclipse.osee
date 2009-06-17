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

import java.util.Map;
import java.util.Properties;
import org.eclipse.osee.ote.core.environment.console.ConsoleCommand;
import org.eclipse.osee.ote.core.environment.console.ConsoleShell;


/**
 * 
 * @author Ken J. Aguilar
 *
 */
public class ListSystemPropertiesCmd extends ConsoleCommand{
    private static final String NAME = "sys_props";
    private static final String DESCRIPTION = "list JVM system properties";


    public ListSystemPropertiesCmd() {
	super(NAME, DESCRIPTION);
    }


    protected void doCmd(ConsoleShell shell, String[] switches, String[] args) {
	Properties props = System.getProperties();
	if (args.length == 0) {
	    for (Map.Entry<Object, Object> entry : props.entrySet()) {
		println(entry.getKey() + "=" + entry.getValue());
	    }
	} else {
	    for (String arg : args) {
		String val = System.getProperty(arg);
		if (val != null) {
		    println(arg + "=" + val);
		} else {
		    println("no property defined for " + arg);
		}
	    }
	}
	
    }
}
