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

package org.eclipse.osee.framework.ui.branch.graph;

import org.eclipse.osee.framework.ui.plugin.OseeUiActivator;

/**
 * @author Roberto E. Escobar
 */
public class Activator extends OseeUiActivator {

   public static final String PLUGIN_ID = "org.eclipse.osee.framework.ui.branch.graph";
   private static Activator instance;

   public Activator() {
      super(PLUGIN_ID);
      Activator.instance = this;
   }

   public static Activator getInstance() {
      return instance;
   }

}
