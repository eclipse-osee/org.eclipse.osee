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

import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Roberto E. Escobar
 */
public final class OsgiUtil {

   private OsgiUtil() {
      // Utility Class;
   }

   public static void close(ServiceTracker tracker) {
      if (tracker != null) {
         tracker.close();
      }
   }

   public static void close(ServiceRegistration registration) {
      if (registration != null) {
         registration.unregister();
      }
   }

   public static void close(ServiceDependencyTracker tracker) {
      Lib.close(tracker);
   }
}
