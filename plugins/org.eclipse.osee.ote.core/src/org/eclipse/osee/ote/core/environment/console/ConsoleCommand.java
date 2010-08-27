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

import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.util.Strings;

public abstract class ConsoleCommand {
   private static final Pattern SPLITTER = Pattern.compile("\\s");

   private final String cmdName;
   private final String description;

   private ConsoleShell shell;

   protected ConsoleCommand(String cmdName, String description) {
      this.cmdName = cmdName;
      this.description = description;
   }

   public String getName() {
      return cmdName;
   }

   public String getDescription() {
      return description;
   }

   public final synchronized void execute(ConsoleShell shell, String switches, String args) {
      this.shell = shell;
      final String[] argArray;
      if (Strings.isValid(args)) {
         argArray = SPLITTER.split(args);
      } else {
         argArray = new String[0];
      }
      final String[] switchesArray;
      if (Strings.isValid(switches)) {
         switchesArray = SPLITTER.split(switches);
      } else {
         switchesArray = new String[0];
      }
      doCmd(shell, switchesArray, argArray);
   }

   protected abstract void doCmd(ConsoleShell shell, String[] switches, String[] args);

   public void dispose() {

   }

   protected final void print(String msg) {
      shell.print(msg);
   }

   protected final void println(String msg) {
      shell.println(msg);
   }

   protected final void println() {
      shell.println();
   }

   protected final void printStackTrace(Throwable t) {
      shell.printStackTrace(t);
   }
}
