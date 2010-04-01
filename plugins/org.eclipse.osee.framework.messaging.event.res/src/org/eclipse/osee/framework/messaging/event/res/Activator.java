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
package org.eclipse.osee.framework.messaging.event.res;

import org.eclipse.osee.framework.plugin.core.OseeActivator;

/**
 * @author Donald G. Dunne
 */
public class Activator extends OseeActivator {

   private static Activator pluginInstance;
   public static final String PLUGIN_ID = "org.eclipse.osee.framework.messaging.event.res";

   public Activator() {
      super();
      pluginInstance = this;
   }

   /**
    * Returns the shared instance.
    */
   public static Activator getInstance() {
      return pluginInstance;
   }
}