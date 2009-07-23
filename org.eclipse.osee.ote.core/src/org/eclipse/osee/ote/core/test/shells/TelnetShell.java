/*******************************************************************************
 * Copyright (c) 2004, 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.core.test.shells;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.logging.Level;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import org.apache.commons.net.telnet.EchoOptionHandler;
import org.apache.commons.net.telnet.InvalidTelnetOptionException;
import org.apache.commons.net.telnet.SuppressGAOptionHandler;
import org.apache.commons.net.telnet.TelnetClient;
import org.apache.commons.net.telnet.TelnetNotificationHandler;
import org.apache.commons.net.telnet.TelnetOption;
import org.apache.commons.net.telnet.TerminalTypeOptionHandler;
import org.eclipse.osee.framework.jdk.core.util.io.InputBufferThread;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import sun.nio.cs.US_ASCII;

/**
 * Created on Aug 15, 2005
 */
public class TelnetShell implements TelnetNotificationHandler {

   private static final int MAX_RESPONSE_TIME = 10000;
   private static final byte[] NEWLINE = "\n".getBytes(new US_ASCII());
   public final static class Piper extends Thread {
      private final InputStream inStream;
      private final OutputStream outStream;

      public final ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
      private boolean done = false;

      public Piper(InputStream in, OutputStream out) {
         super("Stream Piper");
         this.inStream = in;
         this.outStream = out;
      }

      @Override
      public void run() {
         final ReadableByteChannel in = Channels.newChannel(inStream);
         final WritableByteChannel out = Channels.newChannel(outStream);
         try {
            buffer.clear();
            while (!done) {
               in.read(buffer);
               buffer.flip();
               out.write(buffer);
               buffer.compact();
            }
         } catch (IOException e) {
            e.printStackTrace(System.err);
         }
      }
   }

   private TelnetClient telnet;
   private InputStream in;
   private OutputStream out;
   private InputBufferThread inputBuffer;
   private String prompt = "$ ";
   private int currentOutput = 0;

   /**
    * Connects telnet to the specified ipAddress and port
    * 
    * @param ipAddress
    * @param port
    * @throws IOException
    * @throws SocketException
    */
   public TelnetShell(String ipAddress, int port) throws SocketException, IOException {
      this(ipAddress, port, true);
   }

   public TelnetShell(String ipAddress, int port, boolean start) throws SocketException, IOException {
      telnet = new TelnetClient();
      telnet.registerNotifHandler(this);
      SuppressGAOptionHandler sgaOpt = new SuppressGAOptionHandler(true, true, true, true);
      TerminalTypeOptionHandler ttOpt = new TerminalTypeOptionHandler("VT100", false, false, true, false);
      EchoOptionHandler eOPt = new EchoOptionHandler(true, false, true, false);
      try {
         telnet.addOptionHandler(ttOpt);
         telnet.addOptionHandler(sgaOpt);
         telnet.addOptionHandler(eOPt);
      } catch (InvalidTelnetOptionException e) {
         throw new IllegalStateException("invalid telnet options", e);
      }
      telnet.connect(ipAddress, port);
      try {
         Thread.sleep(1500);
      } catch (InterruptedException ex) {
         ex.printStackTrace();
      }

      /*
      try {
         if (!telnet.sendAYT(5000)) {
            throw new SocketException("server appears to be in use");
         }
      } catch (IllegalArgumentException ex) {
         ex.printStackTrace();
      } catch (InterruptedException ex) {
         ex.printStackTrace();
      }
      */
      //printOptionStates();
      in = telnet.getInputStream();
      out = telnet.getOutputStream();
      if (start) {
         inputBuffer = new InputBufferThread(in);
         inputBuffer.start();

      }
   }

   /**
    * writes the command given to the output stream ( telnet )
    * 
    * @param string The command to give
    */
   public void write(String string) {
      // currentOutput = inputBuffer.getLength() + string.length() + 1;
      try {
         for (byte b : string.getBytes("us-ascii")) {
            out.write(b);
            out.flush();

            try {
               Thread.sleep(10);
            } catch (InterruptedException ex) {
               ex.printStackTrace();
            }

         }
         out.write(NEWLINE);
         out.flush();
      } catch (UnsupportedEncodingException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }

   }

   public String writeAndGetPrompt(String string, int timeout) throws Exception {
      inputBuffer.clear();
      // currentOutput = inputBuffer.getLength() + string.length() + 1;
      for (byte b : string.getBytes("us-ascii")) {
         out.write(b);
         out.flush();

         try {
            Thread.sleep(10);
         } catch (InterruptedException ex) {
            ex.printStackTrace();
         }

      }
      out.write(NEWLINE);
      out.flush();
      Thread.sleep(timeout);
      return inputBuffer.getBuffer();

   }

   /**
    * Sits on the line, reading in characters, and waits for the expected output from telnet
    * 
    * @param string The String this function will stop on and return
    * @throws InterruptedException
    */
   public synchronized void waitFor(String string) throws InterruptedException {
      if (inputBuffer.waitFor(string, true, MAX_RESPONSE_TIME) < 0) {
         throw new InterruptedException(
               "Waiting for '" + string + "' took longer then " + MAX_RESPONSE_TIME + " miliseconds.");
      }
   }

   public synchronized MatchResult waitForPattern(Pattern pattern, int millis) throws InterruptedException {
      MatchResult index = inputBuffer.waitFor(pattern, false, millis);
      if (index == null) {
         throw new InterruptedException(
               "Waiting for '" + pattern.pattern() + "' took longer then " + millis + " miliseconds.");
      }
      return index;
   }

   public synchronized void waitForTransmission(int millis) throws InterruptedException {
      if (!inputBuffer.waitFor(millis)) {
         throw new InterruptedException("Waiting for transmission took longer then " + millis + " miliseconds.");
      }
   }

   public synchronized String getBuffer(int start, int end) {
      return inputBuffer.subString(start, end);
   }

   public synchronized String captureTo(String string) throws InterruptedException {
      int index = inputBuffer.waitFor(string, false, MAX_RESPONSE_TIME);
      if (index < 0) {
         throw new InterruptedException(
               "Waiting for '" + string + "' took longer then " + MAX_RESPONSE_TIME + " miliseconds.");
      }
      return inputBuffer.subString(0, index);
   }

   /**
    * Writes the command to telnet and waits for the normal command prompt
    * 
    * @param string The command to issue
    * @throws InterruptedException
    */
   public void sendCommand(String string) throws InterruptedException {
      currentOutput = inputBuffer.getLength() + string.length() + 1;
      write(string);
      waitFor(prompt);
   }

   /**
    * disconnects from telnet
    */
   public void disconnect() {
      try {
         try {
            inputBuffer.stopNow();
         } finally {
            telnet.disconnect();
         }
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   public static void main(String[] args) {
      try {
         BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
         System.out.println("Enter host name or ip address (#.#.#.#))");
         String host = reader.readLine();
         System.out.println("Enter port");
         int port = Integer.parseInt(reader.readLine());
         TelnetShell shell = new TelnetShell(host, port, false);
         Piper piper = new Piper(shell.in, System.out);
         piper.start();
         try {
            boolean done = false;
            while (!done) {
               String in = reader.readLine();
               if (in.equals("quit")) {
                  done = true;
               } else {
                  shell.write(in);
               }
            }
            shell.disconnect();
         } finally {
            piper.interrupt();
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void setPrompt(String prompt) {
      this.prompt = prompt;
   }

   public String getBuffer() {
      return inputBuffer.getBuffer();
   }

   public String getCurrentBuffer() {
      try {
         inputBuffer.waitFor(MAX_RESPONSE_TIME);
      } catch (InterruptedException e) {
         OseeLog.log(TestEnvironment.class, Level.SEVERE, e.getMessage(), e);
      }
      return inputBuffer.subString(currentOutput);
   }

   public InputStream getInputStream() {
      return in;
   }

   public TelnetClient getTelnet() {
      return telnet;
   }

   public void clearBuffer() {
      inputBuffer.clear();
   }

   public void receivedNegotiation(int negotiationCode, int option) {
      final String negotiationCodeStr;
      switch (negotiationCode) {
         case TelnetNotificationHandler.RECEIVED_DO:
            negotiationCodeStr = "DO";
            break;
         case TelnetNotificationHandler.RECEIVED_DONT:
            negotiationCodeStr = "DONT";
            break;
         case TelnetNotificationHandler.RECEIVED_WILL:
            negotiationCodeStr = "WILL";
            break;
         case TelnetNotificationHandler.RECEIVED_WONT:
            negotiationCodeStr = "WONT";
            break;
         default:
            throw new Error("unhandled negotiation code of " + negotiationCode);
      }
      System.out.printf("Negotiation recieved: %s for option %s\n", negotiationCodeStr, TelnetOption.getOption(option));
   }

   public void printOptionStates() {
      for (int i = 0; i < TelnetOption.MAX_OPTION_VALUE; i++) {
         String str = TelnetOption.getOption(i);
         if (!str.equals("UNASSIGNED")) {
            boolean local = telnet.getLocalOptionState(i);
            boolean remote = telnet.getRemoteOptionState(i);
            if (local || remote) {
               System.out.printf("%s (%02d): local %b, remote %b\n", TelnetOption.getOption(i), i, local, remote);
            }
         }
      }
   }
}
