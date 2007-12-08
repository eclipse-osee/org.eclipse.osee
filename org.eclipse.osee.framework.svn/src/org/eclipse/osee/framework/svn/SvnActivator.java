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
package org.eclipse.osee.framework.svn;

import org.eclipse.osee.framework.plugin.core.OseeActivator;

/**
 * The activator class controls the plug-in life cycle see http://svnkit.com/kb/examples/index.php for code examples on
 * svn
 * 
 * @author Ryan D. Brooks
 */
public class SvnActivator extends OseeActivator {
   private static SvnActivator pluginInstance; // The shared instance.
   public static final String PLUGIN_ID = "osee.svn";

   public SvnActivator() {
      pluginInstance = this;
   }

   /**
    * Returns the shared instance.
    */
   public static SvnActivator getInstance() {
      return pluginInstance;
   }
}