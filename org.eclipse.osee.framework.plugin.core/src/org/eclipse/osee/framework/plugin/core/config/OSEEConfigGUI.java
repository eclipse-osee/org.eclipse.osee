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

import static org.eclipse.osee.framework.jdk.core.util.OseeProperties.OSEE_CONFIG_FILE;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.osee.framework.plugin.core.PluginCoreActivator;

/**
 * @author Andrew M. Finkbeiner
 */
public class OSEEConfigGUI {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(OSEEConfigGUI.class);

   protected static OSEEConfig getInstance() {
      String configPath = System.getProperty(OSEE_CONFIG_FILE);
      if (configPath == null) {
         try {
            File file = PluginCoreActivator.getInstance().getPluginFile("support/oseeSiteConfig.xml");
            System.setProperty(OSEE_CONFIG_FILE, file.getAbsolutePath());
         } catch (IOException ex) {
            logger.log(Level.SEVERE, ex.toString(), ex);
         }
      }
      return OSEEConfig.getInstance();
   }
}