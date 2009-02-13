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
package org.eclipse.osee.framework.ui.data.model.editor;

import org.eclipse.osee.framework.ui.plugin.OseeUiActivator;
import org.osgi.framework.BundleContext;

/**
 * @author Roberto E. Escobar
 */
public class ODMEditorActivator extends OseeUiActivator {

   public static final String PLUGIN_ID = "org.eclipse.osee.framework.ui.data.model.editor";

   private static ODMEditorActivator plugin;

   public ODMEditorActivator() {
   }

   /*
    * (non-Javadoc)
    * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
    */
   public void start(BundleContext context) throws Exception {
      super.start(context);
      plugin = this;
   }

   /*
    * (non-Javadoc)
    * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
    */
   public void stop(BundleContext context) throws Exception {
      plugin = null;
      super.stop(context);
   }

   public static ODMEditorActivator getInstance() {
      return plugin;
   }

}
