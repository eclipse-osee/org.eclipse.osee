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
package org.eclipse.osee.framework.database;

import org.eclipse.osee.framework.plugin.core.OseeActivator;

/**
 * The main plug-in class to be used in the desktop.
 * 
 * @author Ryan D. Brooks
 */
public class DatabaseActivator extends OseeActivator {

   // The shared instance.
   private static DatabaseActivator plugin;

   /**
    * The constructor.
    */
   public DatabaseActivator() {
      plugin = this;
   }

   /**
    * Returns the shared instance.
    */
   public static DatabaseActivator getInstance() {
      return plugin;
   }
}