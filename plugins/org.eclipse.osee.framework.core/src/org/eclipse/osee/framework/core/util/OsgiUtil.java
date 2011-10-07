/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.util;

import java.util.logging.Level;
import org.eclipse.osee.framework.core.internal.Activator;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Roberto E. Escobar
 */
public final class OsgiUtil {

   private OsgiUtil() {
      // Utility Class;
   }

   public static void close(ServiceTracker<?, ?> tracker) {
      if (tracker != null) {
         try {
            tracker.close();
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.WARNING, ex);
         }
      }
   }

   public static void close(ServiceRegistration<?> registration) {
      if (registration != null) {
         try {
            registration.unregister();
         } catch (Exception ex) {
            //            OseeLog.log(Activator.class, Level.WARNING, ex);
         }
      }
   }

   public static void close(ServiceDependencyTracker tracker) {
      Lib.close(tracker);
   }
}
