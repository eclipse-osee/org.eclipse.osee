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
package org.eclipse.osee.framework.ui.skynet.blam;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.osgi.framework.Bundle;

/**
 * @author Ryan D. Brooks
 */
public class OperationsPage {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(OperationsPage.class);

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
               logger.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            } catch (NoClassDefFoundError er) {
               logger.log(Level.WARNING,
                     "Failed to find a class definition for " + classname + ", registered from bundle " + bundleName,
                     er);
            }
         }
      }
   }

}
