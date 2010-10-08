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
package org.eclipse.osee.ats.config.demo.internal;

import org.eclipse.osee.framework.ui.plugin.OseeUiActivator;

/**
 * The activator class controls the plug-in life cycle
 */
public class OseeAtsConfigDemoActivator extends OseeUiActivator {
   private static OseeAtsConfigDemoActivator plugin;
   public static final String PLUGIN_ID = "org.eclipse.osee.ats.config.demo";

   public OseeAtsConfigDemoActivator() {
      super(PLUGIN_ID);
      plugin = this;
   }

   public static OseeAtsConfigDemoActivator getInstance() {
      return plugin;
   }
}