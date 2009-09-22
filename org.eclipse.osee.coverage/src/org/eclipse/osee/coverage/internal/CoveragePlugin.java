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
package org.eclipse.osee.coverage.internal;

import org.eclipse.osee.framework.ui.plugin.OseeUiActivator;

public class CoveragePlugin extends OseeUiActivator {
   private static CoveragePlugin pluginInstance; // The shared instance.
   public static final String PLUGIN_ID = "org.eclipse.osee.coverage";

   public CoveragePlugin() {
      super();
      pluginInstance = this;
   }

   @Override
   protected String getPluginName() {
      return PLUGIN_ID;
   }

   /**
    * Returns the shared instance.
    */
   public static CoveragePlugin getInstance() {
      return pluginInstance;
   }
}