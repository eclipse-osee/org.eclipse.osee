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
package org.eclipse.osee.framework.jdk.core.util.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;

/**
 * Thread specifically used for connecting to minicom via the serial port and setting up an input and output stream.
 * 
 * @author Michael P. Masterson
 */
public class MinicomOutputThread extends Thread {
   private Writer output;
   private BufferedReader input;
   private boolean resetFinished;

   /**
    * saves input and output streams then waits for minicom to load completely
    */
   public MinicomOutputThread(Writer output, BufferedReader input) {
      super();
      this.output = output;
      this.input = input;
      waitForWelcomScreen();
      resetFinished = false;
   }

   /**
    * reads input from the minicom until it sees a common line singifying it has completely loaded
    */
   private void waitForWelcomScreen() {
      try {
         String welcomeLine = "Press CTRL-A Z for help on special keys";
         String outLine = null;
         while ((outLine = input.readLine()) != null) {
            output.write("\nWELCOME: " + outLine);
            if (outLine.contains(welcomeLine)) break;

         }
      } catch (IOException ex) {
         ex.printStackTrace();
      }
   }

   /**
    * @return True if the text for the completion of a reset has been seen on the line
    */
   public boolean isResetFinished() {
      return this.resetFinished;
   }

   /**
    * Common run command for the thread. Sits on the serial line reading input from the minicom and printing it to the
    * the output stream. If the expected end of a reset is found, it sets that field to indicate the reset is finished.
    */
   public void run() {
      String outLine = null;
      try {
         while ((outLine = input.readLine()) != null) {
            if (!outLine.contains("[")) output.write(outLine + "\n");

            if (outLine.contains("Start of wp_periodic_task")) {
               output.write("Found end of reset\n");
               this.resetFinished = true;
            }

         }
      } catch (IOException ex) {
         ex.printStackTrace();
      } finally {
         try {
            input.close();
            output.flush();
         } catch (IOException ex) {
            ex.printStackTrace();
         }
      }
   }

   /**
    * Waits to either read in the expected reset completion line OR for the run command to see the completion line
    */
   public void waitForReset() {
      try {
         String outLine;
         while ((outLine = input.readLine()) != null && !isResetFinished()) {
            if (!outLine.contains("[")) output.write(outLine + "\n");

            if (outLine.contains("Start of wp_periodic_task")) {
               output.write("Found end of reset\n");
               break;
            }
         }
      } catch (IOException ex) {
         ex.printStackTrace();
      }
   }

}
