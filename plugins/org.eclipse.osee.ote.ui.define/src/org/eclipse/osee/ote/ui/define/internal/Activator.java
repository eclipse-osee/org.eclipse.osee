/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

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
