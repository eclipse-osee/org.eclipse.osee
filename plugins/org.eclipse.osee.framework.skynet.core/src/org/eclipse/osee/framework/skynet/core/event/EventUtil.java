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
package org.eclipse.osee.framework.skynet.core.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Roberto E. Escobar
 */
public final class EventUtil {

   private EventUtil() {
      // Utility Class
   }

   public static String getObjectSafeName(Object object) {
      try {
         return object.toString();
      } catch (Exception ex) {
         return object.getClass().getSimpleName() + " - exception on toString: " + ex.getLocalizedMessage();
      }
   }

   public static String getListenerReport(Collection<IEventListener> listeners, Collection<IEventListener> priorityListeners) {
      List<String> listenerStrs = new ArrayList<String>();
      for (IEventListener listener : priorityListeners) {
         listenerStrs.add("Priority: " + EventUtil.getObjectSafeName(listener));
      }
      for (IEventListener listener : listeners) {
         listenerStrs.add(EventUtil.getObjectSafeName(listener));
      }
      String[] listArr = listenerStrs.toArray(new String[listenerStrs.size()]);
      Arrays.sort(listArr);
      return org.eclipse.osee.framework.jdk.core.util.Collections.toString("\n", (Object[]) listArr);
   }

   public static void eventLog(String output) {
      eventLog(output, null);
   }

   public static void eventLog(String output, Exception ex) {
      try {
         if (isEventDebugConsole()) {
            System.err.println(output + (ex != null ? " <<ERROR>> " + ex.toString() : ""));
         } else if (isEventDebugErrorLog()) {
            if (ex != null) {
               OseeLog.log(Activator.class, Level.SEVERE, output, ex);
            } else {
               OseeLog.log(Activator.class, Level.FINE, output);
            }
         }
      } catch (Exception ex1) {
         OseeLog.log(Activator.class, Level.SEVERE, ex1);
      }
   }

   private static boolean isEventDebugConsole() {
      if (!Strings.isValid(System.getProperty("eventDebug"))) {
         return false;
      }
      return System.getProperty("eventDebug").equals("console");
   }

   private static boolean isEventDebugErrorLog() {
      if (!Strings.isValid(System.getProperty("eventDebug"))) {
         return false;
      }
      return System.getProperty("eventDebug").equals("log") || "TRUE".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.osee.framework.skynet.core/debug/Events"));
   }

}
