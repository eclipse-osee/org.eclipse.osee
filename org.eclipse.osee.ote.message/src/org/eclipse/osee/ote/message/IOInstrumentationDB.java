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
package org.eclipse.osee.ote.message;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.message.instrumentation.IOInstrumentation;

/**
 * @author Andrew M. Finkbeiner
 *
 */
public class IOInstrumentationDB {
   
   private final Map<String, IOInstrumentation> ioInstrumentation = new ConcurrentHashMap<String, IOInstrumentation>();;
   private final CopyOnWriteArraySet<IInstrumentationRegistrationListener> listeners = new CopyOnWriteArraySet<IInstrumentationRegistrationListener>();
   
   IOInstrumentationDB(){
   }
   
   public IOInstrumentation getIOInstrumentation(String name) {//, IOInstrumentation io){
      return ioInstrumentation.get(name);
   }

   public IOInstrumentation registerIOInstrumentation(String name, IOInstrumentation io) {
      IOInstrumentation old = ioInstrumentation.put(name, io);
      if (old != io) {
         notifyRegistration(name, io);
      }
      return old;
   }

   public void unregisterIOInstrumentation(String name) {
      IOInstrumentation io = ioInstrumentation.remove(name);
      if (io != null) {
         notifyDeregistration(name);
      }
   }
   
   public void addRegistrationListener(IInstrumentationRegistrationListener listener) {
      listeners.add(listener);
      for (Map.Entry<String, IOInstrumentation> entry : ioInstrumentation.entrySet()) {
         try {
            listener.onRegistered(entry.getKey(), entry.getValue());
         } catch (Exception e) {
            OseeLog.log(IOInstrumentation.class, Level.SEVERE, "exception notifying listener of IO instrumentation registration", e);
         }
      }
   }
   
   public void removeRegistrationListener(IInstrumentationRegistrationListener listener) {
      listeners.remove(listener);
   }
   
   private void notifyRegistration(String name, IOInstrumentation io) {
      for (IInstrumentationRegistrationListener listener : listeners) {
         try {
            listener.onRegistered(name, io);
         } catch (Exception e) {
            OseeLog.log(IOInstrumentation.class, Level.SEVERE, "exception notifying listener of IO instrumentation registration", e);
         }
      }
   }
   private void notifyDeregistration(String name) {
      for (IInstrumentationRegistrationListener listener : listeners) {
         try {
            listener.onDeregistered(name);
         } catch (Exception e) {
            OseeLog.log(IOInstrumentation.class, Level.SEVERE, "exception notifying listener of IO instrumentation de-registration", e);
         }
      }
   }
}
