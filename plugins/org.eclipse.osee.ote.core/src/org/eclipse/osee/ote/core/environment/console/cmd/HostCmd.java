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

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.eclipse.osee.ote.core.environment.console.ConsoleCommand;
import org.eclipse.osee.ote.core.environment.console.ConsoleShell;

/**
 * @author Ken J. Aguilar
 */
public class HostCmd extends ConsoleCommand {

    private static final String NAME = "host";
    private static final String DESCRIPTION = "displays the local host";

    public HostCmd() {
        super(NAME, DESCRIPTION);
    }

    protected void doCmd(ConsoleShell shell, String[] switches, String[] args) {
        try {
            println(InetAddress.getLocalHost().toString());
        } catch (UnknownHostException e) {
            println("problems getting local host");
        }
    }

}
