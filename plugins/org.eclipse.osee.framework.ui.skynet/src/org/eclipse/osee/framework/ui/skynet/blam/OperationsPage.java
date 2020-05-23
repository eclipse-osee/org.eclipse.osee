/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.blam;

import java.util.logging.Level;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.osgi.framework.Bundle;

/**
 * @author Ryan D. Brooks
 */
public class OperationsPage {
   public void findAllOperations() {
      IExtensionPoint point =
         Platform.getExtensionRegistry().getExtensionPoint("org.eclipse.osee.framework.ui.skynet.BlamOperation");
      IExtension[] extensions = point.getExtensions();
      for (IExtension extension : extensions) {
         extension.getUniqueIdentifier();
         IConfigurationElement[] elements = extension.getConfigurationElements();
         String classname = null;
         String bundleName = null;
         for (IConfigurationElement el : elements) {
            if (el.getName().equals("Renderer")) {
               classname = el.getAttribute("classname");
               bundleName = el.getContributor().getName();
            }
         }
         if (classname != null && bundleName != null) {
            Bundle bundle = Platform.getBundle(bundleName);
            try {
               Class<?> renderClass = bundle.loadClass(classname);
               renderClass.newInstance();
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            } catch (NoClassDefFoundError er) {
               OseeLog.log(Activator.class, Level.WARNING,
                  "Failed to find a class definition for " + classname + ", registered from bundle " + bundleName, er);
            }
         }
      }
   }

}
