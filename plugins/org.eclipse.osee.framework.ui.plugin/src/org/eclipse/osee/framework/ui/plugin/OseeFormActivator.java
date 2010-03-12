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
package org.eclipse.osee.framework.ui.plugin;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.FormColors;
import org.osgi.framework.BundleContext;

/**
 * @author Ryan D. Brooks
 */
public abstract class OseeFormActivator extends OseeUiActivator {
   private FormColors formColors;

   /**
    * This method is called when the plug-in is stopped
    */
   @Override
   public void stop(BundleContext context) throws Exception {
      super.stop(context);
      if (formColors != null) {
         formColors.dispose();
         formColors = null;
      }
   }

   /**
    * returns a shared FormColors that can be passed to a FormToolkit constructor "Create one color manager (FormColors)
    * per plug-in. When creating the toolkit, pass the color manager to the toolkit. The toolkit will know that the
    * colors are shared and will not dispose them."
    */
   public FormColors getSharedFormColors(Display display) {
      if (formColors == null) {
         formColors = new FormColors(display);
         formColors.markShared();
      }
      return formColors;
   }
}
