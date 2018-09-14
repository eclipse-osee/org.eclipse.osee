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
package org.eclipse.osee.ats.health;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.osgi.framework.Bundle;

/**
 * @author Donald G. Dunne
 */
public class AtsHealthCheck {

   private static Set<IAtsHealthCheck> healthCheckItems = new HashSet<>();
   private static boolean loaded = false;

   public static Set<IAtsHealthCheck> getAtsHealthCheckItems() {
      if (!loaded) {
         loaded = true;
         IExtensionPoint point =
            Platform.getExtensionRegistry().getExtensionPoint("org.eclipse.osee.ats.AtsHealthCheck");
         if (point == null) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Can't access AtsHealthCheck extension point");
            return healthCheckItems;
         }
         IExtension[] extensions = point.getExtensions();
         for (IExtension extension : extensions) {
            IConfigurationElement[] elements = extension.getConfigurationElements();
            String classname = null;
            String bundleName = null;
            for (IConfigurationElement el : elements) {
               if (el.getName().equals("AtsHealthCheck")) {
                  classname = el.getAttribute("classname");
                  bundleName = el.getContributor().getName();
                  if (classname != null && bundleName != null) {
                     Bundle bundle = Platform.getBundle(bundleName);
                     try {
                        Class<?> taskClass = bundle.loadClass(classname);
                        Object obj = taskClass.newInstance();
                        healthCheckItems.add((IAtsHealthCheck) obj);
                     } catch (Exception ex) {
                        OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Error loading AtsHealthCheck extension",
                           ex);
                     }
                  }
               }
            }
         }
      }
      return healthCheckItems;
   }
}
