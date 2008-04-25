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
package org.eclipse.osee.framework.plugin.core.config;

import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Handler;
import java.util.logging.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

public class EclipseConfigurationFactory extends BaseConfigurationFactory {

   private EclipseHandler handler;
   private Collection<Handler> extensionHandlers;

   public EclipseConfigurationFactory() {
      handler = new EclipseHandler();
      extensionHandlers = new LinkedList<Handler>();

      try {
         loadExtensionHandlers();
      } catch (CoreException ex) {
         ex.printStackTrace();
      }
   }

   public Handler getLogHandler() {
      return handler;
   }

   public OSEEConfig getOseeConfig() {
      return OSEEConfig.getInstance();
   }

   public Logger getLogger(Class<?> classname) {
      Logger logger = Logger.getLogger(classname.getName());
      if (logger.getHandlers().length < 1) {
         logger.setUseParentHandlers(false);
         logger.addHandler(handler);
         logger.addHandler(new OseeConsoleHandler());
         for (Handler extHandler : extensionHandlers) {
            logger.addHandler(extHandler);
         }
      }
      return logger;
   }

   private void loadExtensionHandlers() throws CoreException {
      IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
      if (extensionRegistry != null) {
         IExtensionPoint point =
               extensionRegistry.getExtensionPoint("org.eclipse.osee.framework.plugin.core.logHandler");
         if (point != null) {
            IExtension[] extensions = point.getExtensions();
            for (IExtension extension : extensions) {
               IConfigurationElement[] elements = extension.getConfigurationElements();
               for (IConfigurationElement el : elements) {
                  if (el.getName().equals("handler")) {
                     extensionHandlers.add((Handler) el.createExecutableExtension("name"));
                     System.out.println("Added extension log handler :" + el.getAttribute("name"));
                  }
               }
            }
         }
      }
   }
}
