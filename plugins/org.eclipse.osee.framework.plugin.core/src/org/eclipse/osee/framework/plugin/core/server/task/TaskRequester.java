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
package org.eclipse.osee.framework.plugin.core.server.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import org.eclipse.osee.framework.jdk.core.result.XConsoleLogger;

/**
 * @author Ryan D. Brooks
 */
public class TaskRequester {
   private final NativeCommand nativeCommand;
   private final Socket taskSocket;
   private final BufferedReader fromServer;
   private final ObjectOutputStream toServer;

   public TaskRequester(String machine, int port) throws IOException {
      if (port < 1) {
         throw new IllegalArgumentException("port argument must be a natural number");
      }
      if (machine.equals("")) {
         throw new IllegalArgumentException("machine must be specified");
      }

      taskSocket = new Socket(machine, port);
      fromServer = new BufferedReader(new InputStreamReader(taskSocket.getInputStream()));
      toServer = new ObjectOutputStream(taskSocket.getOutputStream());
      toServer.writeInt(587289473); // magic number
      nativeCommand = new NativeCommand();
   }

   public void close() {
      try {
         toServer.close();
         taskSocket.close();
      } catch (IOException ex) {
         ex.printStackTrace();
      }
   }

   private void executeCommand(String[] callAndArgs) throws IOException {
      nativeCommand.sendNativeCommand(toServer, callAndArgs);
   }

   private static void printUsage() {
      XConsoleLogger.err("Usage: java TaskRequester <cmd> [args]");
   }

   public static void main(String[] args) throws IOException {
      if (args.length < 1) {
         printUsage();
         return;
      }

      TaskRequester app = new TaskRequester(InetAddress.getLocalHost().getHostName(), 8140);
      app.executeCommand(args);

      String line = null;
      while ((line = app.fromServer.readLine()) != null) {
         System.out.printf(line);
      }
      app.close();
   }
}