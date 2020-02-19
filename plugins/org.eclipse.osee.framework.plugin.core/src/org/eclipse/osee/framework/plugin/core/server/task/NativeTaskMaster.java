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

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import org.eclipse.osee.framework.jdk.core.result.XConsoleLogger;

/**
 * @author Ryan D. Brooks
 */
public class NativeTaskMaster {
   private boolean listening;
   private ServerSocket serverSocket;

   public static void main(String[] args) {
      if (args.length != 1) {
         XConsoleLogger.err("Usage: java NativeTaskMaster <port>");
         return;
      }

      try {
         NativeTaskMaster app = new NativeTaskMaster(Integer.parseInt(args[0]));
         app.listen();
      } catch (NumberFormatException ex) {
         XConsoleLogger.err(ex);
         return;
      }
   }

   public NativeTaskMaster(int port) {
      super();
      this.listening = true;

      if (port < 1) {
         throw new IllegalArgumentException("port argument must be a natural number");
      }
      try {
         serverSocket = new ServerSocket(port);
      } catch (IOException ex) {
         XConsoleLogger.err("Could not listen on port: " + port + ".");
         return;
      }
   }

   private void listen() {
      try {
         while (listening) {
            Socket socket = serverSocket.accept(); // wait for the next connection
            new Thread(new TaskServerThread(587289473, socket)).start();
         }
         serverSocket.close();
      } catch (IOException ex) {
         ex.printStackTrace();
      }
   }
}