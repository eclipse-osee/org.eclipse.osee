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
package org.eclipse.osee.framework.ui.plugin.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.osee.framework.jdk.core.util.IConsoleInputListener;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleInputStream;
import org.eclipse.ui.console.IOConsoleOutputStream;

/**
 * Creates an Eclipse Console instance and allows writing output, errors and prompt messages There should be at one
 * instance of a ConsoleWriter per plugin that needs to output messages and errors. *
 * 
 * @author Donald G. Dunne
 */
public class OseeConsole {

   private static Logger logger = ConfigUtil.getConfigFactory().getLogger(OseeConsole.class);

   IOConsoleOutputStream streamOut = null;

   IOConsoleOutputStream streamErr = null;

   IOConsoleOutputStream streamPrompt = null;

   IOConsole console = null;
   private HandleInput inputHandler;
   private boolean time;

   public OseeConsole(String title) {
      this(title, true);
   }

   public OseeConsole(String title, boolean time) {
      console = new IOConsole(title, null);
      this.time = time;
      this.inputHandler = new HandleInput();
      if (console != null) {

         new Thread(inputHandler).start();
         ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] {console});
         Display.getDefault().asyncExec(new Runnable() {
            public void run() {
               streamOut = console.newOutputStream();// newMessageStream();
               streamOut.setColor(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
               streamErr = console.newOutputStream();
               streamErr.setColor(Display.getDefault().getSystemColor(SWT.COLOR_RED));
               streamPrompt = console.newOutputStream();
               streamPrompt.setColor(Display.getDefault().getSystemColor(SWT.COLOR_BLUE));
            }
         });
         // console.n
      }
   }

   public PrintStream getPrintStream() {
      return new PrintStream(streamOut);
   }

   public void shutdown() {
      if (console != null) ConsolePlugin.getDefault().getConsoleManager().removeConsoles(new IConsole[] {console});
   }

   public static final int CONSOLE_ERROR = 0;

   public static final int CONSOLE_OUT = 1;

   public static final int CONSOLE_PROMPT = 2;

   /**
    * Writes string to console without popping console forward
    * 
    * @param str
    */
   public void write(String str) {
      write(str, false);
   }

   /**
    * Writes string to console without popping console forward
    * 
    * @param str
    */
   public void writeError(String str) {
      write(str, CONSOLE_ERROR, true);
   }

   /**
    * Writes string to console
    * 
    * @param str
    * @param popup bring console window forward
    */
   public void write(String str, boolean popup) {
      write(str, CONSOLE_OUT, true);
   }

   /**
    * Write string to console
    * 
    * @param str
    * @param type CONSOLE_ERROR, CONSOLE_OUT, CONSOLE_PROMPT
    */
   public void write(String str, int type) {
      write(str, type, false);
   }

   /**
    * Write string to console
    * 
    * @param str
    * @param type CONSOLE_ERROR, CONSOLE_OUT, CONSOLE_PROMPT
    * @param popup bring console window forward
    */
   public void write(String str, int type, boolean popup) {
      String time = "";
      if (this.time) {
         Calendar cal = Calendar.getInstance();
         cal.setTime(new Date());

         if (cal.get(Calendar.HOUR) == 0)
            time = "12";
         else
            time = "" + cal.get(Calendar.HOUR);
         time = Lib.padLeading(time, '0', 2);
         String minute = "" + cal.get(Calendar.MINUTE);
         minute = Lib.padLeading(minute, '0', 2);
         time += ":" + minute + " => ";
      }
      try {
         if (type == CONSOLE_ERROR && streamErr != null) {
            streamErr.write(time + str);
            streamErr.write("\n");
         }
         if (type == CONSOLE_PROMPT && streamPrompt != null) {
            streamPrompt.write(time + str);
            streamPrompt.write("\n");
         }
         if (type == CONSOLE_OUT && streamOut != null) {
            streamOut.write(time + str);
            streamOut.write("\n");
         }

         if (popup) popup();
      } catch (IOException ex) {
         ex.printStackTrace();
      }
   }

   public void popup() {
      ConsolePlugin.getDefault().getConsoleManager().showConsoleView(console);
   }

   public void addInputListener(IConsoleInputListener listener) {
      inputHandler.addListener(listener);
   }

   private class HandleInput implements Runnable {

      private List<IConsoleInputListener> listeners;

      public HandleInput() {
         listeners = new ArrayList<IConsoleInputListener>();
      }

      public void addListener(IConsoleInputListener listener) {
         synchronized (listeners) {
            listeners.add(listener);
         }
      }

      public void run() {
         BufferedReader input = new BufferedReader(new InputStreamReader(console.getInputStream()));
         try {
            String line = null;
            while ((line = input.readLine()) != null) {
               synchronized (listeners) {
                  for (IConsoleInputListener listener : listeners) {
                     listener.lineRead(line);
                  }
               }
            }
         } catch (IOException e) {
            e.printStackTrace();
         }
         logger.log(Level.INFO, "done with the handling of input");
      }

   }

   public void prompt(String str) {
      @SuppressWarnings("unused")
      IOConsoleInputStream input = console.getInputStream();
      //      input.appendData(str);
   }
}