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

import static org.eclipse.osee.framework.jdk.core.util.OseeProperties.OSEE_CONFIG_FACTORY;
import org.eclipse.osee.framework.plugin.core.PluginCoreActivator;

public class ConfigUtil {
   private static IOseeConfigurationFactory config = null;

   private ConfigUtil() {
   }

   public static IOseeConfigurationFactory getConfigFactory() {
      if (config == null) {
         String classname = System.getProperty(OSEE_CONFIG_FACTORY);
         if (classname == null) {
            classname = "org.eclipse.osee.framework.plugin.core.config.EclipseConfigurationFactory";
         }
         try {
            Class<?> configClass = PluginCoreActivator.class.getClassLoader().loadClass(classname);
            config = (IOseeConfigurationFactory) configClass.newInstance();
         } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
         } catch (InstantiationException ex) {
            ex.printStackTrace();
         } catch (IllegalAccessException ex) {
            ex.printStackTrace();
         }
      }
      return config;
   }
}
