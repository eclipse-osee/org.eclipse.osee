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
package org.eclipse.osee.framework.ui.service.control;

import org.eclipse.osee.framework.ui.plugin.OseeUiActivator;

/**
 * The main plugin class to be used in the desktop.
 * 
 * @author Roberto E. Escobar
 */
public class ControlPlugin extends OseeUiActivator {

   private static ControlPlugin pluginInstance; // The shared instance.

   /**
    * The constructor.
    */
   public ControlPlugin() {
      pluginInstance = this;
   }

   /**
    * Returns the shared instance.
    */
   public static ControlPlugin getInstance() {
      return pluginInstance;
   }
}
