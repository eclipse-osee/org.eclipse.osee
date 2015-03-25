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
package org.eclipse.osee.ote.ui.internal;

import java.io.IOException;

import org.eclipse.osee.framework.jdk.core.util.IConsoleInputListener;
import org.eclipse.osee.framework.ui.plugin.util.OseeConsole;
import org.eclipse.osee.ote.message.event.OteEventMessageUtil;
import org.eclipse.osee.ote.remote.messages.ConsoleInputMessage;
import org.eclipse.osee.ote.remote.messages.ConsoleOutputMessage;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class OteServerConsole {

   private final OseeConsole console = new OseeConsole("OTE Server", false, false);
   private ServiceRegistration<EventHandler> registration;

   public OteServerConsole() {
      console.addInputListener(new OTEServerConsoleInputListener());
      registration = OteEventMessageUtil.subscribe(ConsoleOutputMessage.TOPIC, new ConsoleOutputHandler(console));
   }

   private OseeConsole getConsole() {
      return console;
   }

   public void close() {
      getConsole().shutdown();
      if(registration != null){
         registration.unregister();
         registration = null;
      }
   }
   
   private static class ConsoleOutputHandler implements EventHandler {

      private OseeConsole console;
      private ConsoleOutputMessage output;

      public ConsoleOutputHandler(OseeConsole console) {
         this.console = console;
         this.output = new ConsoleOutputMessage();
      }

      @Override
      public void handleEvent(Event arg0) {
         output.setBackingBuffer(OteEventMessageUtil.getBytes(arg0));
         try {
            console.write(output.getString());
         } catch (IOException e) {
            e.printStackTrace();
         } catch (ClassNotFoundException e) {
            e.printStackTrace();
         }
         
      }
      
   }
   
   private static class OTEServerConsoleInputListener implements IConsoleInputListener {

      private ConsoleInputMessage input = new ConsoleInputMessage();
      
      @Override
      public void lineRead(String line) {
         try {
            input.setString(line);
            OteEventMessageUtil.sendEvent(input);
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
      
   }
}
