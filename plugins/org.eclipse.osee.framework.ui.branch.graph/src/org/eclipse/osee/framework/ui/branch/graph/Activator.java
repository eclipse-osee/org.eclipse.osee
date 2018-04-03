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
