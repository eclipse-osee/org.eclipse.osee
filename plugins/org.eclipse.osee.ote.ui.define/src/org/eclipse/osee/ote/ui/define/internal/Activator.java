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
package org.eclipse.osee.ote.ui.define.internal;

import org.eclipse.osee.framework.ui.plugin.OseeUiActivator;

public class Activator extends OseeUiActivator {

   public static final String PLUGIN_ID = "org.eclipse.osee.ote.ui.define";
   private static Activator instance;

   public Activator() {
      super(PLUGIN_ID);
   }

   public static Activator getInstance() {
      return instance;
   }
}
