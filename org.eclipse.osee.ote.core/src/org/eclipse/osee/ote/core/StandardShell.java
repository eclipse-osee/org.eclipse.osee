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
package org.eclipse.osee.ote.core;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.environment.console.ConsoleCommand;
import org.eclipse.osee.ote.core.environment.console.ConsoleShell;
import org.eclipse.osee.ote.core.environment.console.ICommandManager;

/**
 * @author Ken J. Aguilar
 */
public class StandardShell extends ConsoleShell {
   
   private final Thread thread;
   private volatile boolean run = true;
   
   public StandardShell(ICommandManager manager) {
      super(manager);
      thread = new Thread(new Runnable(){
         public void run() {
            InputStreamReader stdin = new InputStreamReader(System.in);
            BufferedReader consoleIn = new BufferedReader(stdin);
            while(run) {
               try {
                  final String line = consoleIn.readLine();
                  if (line == null) {
                      OseeLog.log(StandardShell.class,
                            Level.INFO,  
                            "INput stream closed, standard shell closing...");
                    run = false;
                    break;
                  }
                  if (line.equals("")) {
                     continue;
                  }
                  parseAndExecuteCmd(line);
                  
               } catch (InterruptedIOException ioe) {
                  if (run) {
                     println("Command console interrupted abnormally");
                  }
               } catch (Throwable t) {
                  printStackTrace(t);
               }
            }
         }
      },"LBA Test Server Console"); {

         
      };
   }
   
   /* (non-Javadoc)
    * @see lba.ote.server.console.shell.ICommandShell#println(java.lang.String)
    */
   public void println(String string) {
        System.out.println(string);
   }

   /* (non-Javadoc)
    * @see lba.ote.server.console.shell.ICommandShell#print(java.lang.String)
    */
   public void print(String string) {
        System.out.print(string);
   }

   /* (non-Javadoc)
    * @see lba.ote.server.console.shell.ICommandShell#println()
    */
   public void println() {
      System.out.println();
   }

   public void onCommandComplete(ConsoleCommand cmd) {
      System.out.flush();
   }
   
   public void start() {
      thread.start();
      OseeLog.log(StandardShell.class,
   			 Level.INFO,  
   			"The Standard Command Console has been started");
   }
   
   public void shutdown() {
	   run = false;
	   thread.interrupt();
   }
}
