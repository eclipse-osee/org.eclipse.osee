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

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.PrintWriter;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * Specifically used for connecting to minicom via the serial port and setting up an input and output stream.
 * 
 * @author Michael P. Masterson
 */
public class MinicomConnection {
   private static final int MAX_RESPONSE_TIME = 100000;
   private static final int ITERATION_TIME = 2000;

   private InputBufferThread inputBuffer;
   private PrintWriter commandLine;

   /**
    * saves input and output streams then waits for minicom to load completely
    */
   public MinicomConnection() {
      super();
      setupConnection();
      waitForWelcomeScreen();
   }

   public static void main(String[] args) {
      for (int i = 0; i < 20; i++) {
         System.out.println("i = " + i + ", Making Connection...");
         MinicomConnection connection = new MinicomConnection();
         System.out.println("Connection made");
         System.out.println("Restarting...");
         connection.resetPizzaBox();
      }
      System.out.println("Done with main");

   }

   /**
    * Uses a process builder to set up the minicom process executable. Using this it will create a thread for inputting
    * commands to minicom.
    */
   private void setupConnection() {
      try {
         ProcessBuilder pb = new ProcessBuilder();

         // the path to the minicom program through linux.
         String[] minicomExe = new String[] {"/usr/bin/minicom"};

         // sets up the operating system command
         pb.command(minicomExe);

         // starts a new process based on the minicom command
         Process minicomProc = pb.start();

         // the following three writers and readers will be used to issue minicom commands and read its responses
         PipedWriter pw = new PipedWriter();
         commandLine = new PrintWriter(pw);
         PipedReader pr = new PipedReader(pw);

         // send the textual output of minicom to the trash
         PrintWriter outputFromTheMinicom = new PrintWriter(System.out);

         // sets up the minicom thread and directs minicom output to the two streams passed
         inputBuffer = (InputBufferThread) Lib.handleMinicomProcess(minicomProc, outputFromTheMinicom, pr)[1];
      } catch (IOException ex) {
         ex.printStackTrace();
      }

   }

   /**
    * @return The printWriter for issuing commands to minicom
    */
   public PrintWriter getCommandLine() {
      return this.commandLine;
   }

   /**
    * reads input from the minicom until it sees a common line singifying it has completely loaded
    */
   private void waitForWelcomeScreen() {
      waitFor("Press CTRL-A Z for help on special keys");
   }

   /**
    * Waits to either read in the expected reset completion line OR for the run command to see the completion line
    */
   public void waitForReset() {
      waitFor("Decompression complete");
   }

   /**
    * Waits for the parameter passed to appear in the minicom output. Uses the InputBufferThread that should have been
    * started before this.
    * 
    * @param matcher The string to look for in the minicom's output stream
    */
   public synchronized void waitFor(String matcher) {
      try {
         int elapsedTime = 0;
         while (elapsedTime <= MAX_RESPONSE_TIME) {
            if (inputBuffer.contains(matcher) >= 0) break;

            System.out.println("Input buffer did not contain " + matcher + " after " + elapsedTime + " milisecs");
            this.wait(ITERATION_TIME);
            elapsedTime += ITERATION_TIME;
         }
         if (elapsedTime > MAX_RESPONSE_TIME) {
            throw new InterruptedException(
                  "Waiting for '" + matcher + "' took longer then " + MAX_RESPONSE_TIME + " miliseconds.");
         }
         wait(4000);

      } catch (InterruptedException ex) {
         ex.printStackTrace();
      }

   }

   /**
    * Stops the buffering thread from running and closes the commandLine
    */
   public void disconnect() {
      System.out.println("Disconnecting...");
      inputBuffer.stopOnNextRun(true);
      commandLine.close();
      System.out.println("Disconnect Finished");
   }

   /**
    * Runs through the commands given through minicom that will reset the pizzabox. Once the command/s are given, it
    * waits for the reset to complete before exitting minicom.
    */
   public void resetPizzaBox() {
      System.out.println("Issuing rset Command");
      // Issues the reset command to the OFP
      commandLine.println("rset");

      System.out.println("Waiting for reset");
      waitForReset();
      System.out.println("Reset Finished");

      System.out.println("Issuing CTRL-A Q command");
      // quits minicom
      commandLine.println((char) 1 + "q");

      System.out.println("hitting enter on 'you sure you want to quit'");
      // hit enter on the "Are you sure you want to quit?" popup
      commandLine.println();

      disconnect();
      System.out.println("Done with resetPizzaBox");
   }

}
