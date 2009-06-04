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
package org.eclipse.osee.framework.ui.plugin.workspace;

import java.util.Hashtable;
import org.eclipse.osee.framework.ui.plugin.OseePluginUiActivator;
import org.eclipse.osee.framework.ui.plugin.workspace.internal.SafeWorkspaceAccessImpl;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IStartup;
import org.osgi.framework.BundleContext;

/**
 * @author Andrew M. Finkbeiner
 *
 */
public class EarlyStartup implements IStartup {

   /* (non-Javadoc)
    * @see org.eclipse.ui.IStartup#earlyStartup()
    */
   @Override
   public void earlyStartup() {
      Display.getDefault().asyncExec(new Runnable() {
         @Override
         public void run() {
              registerWorkspaceAccessService();
         }
      });
   }
   
   private void registerWorkspaceAccessService(){
      BundleContext context = OseePluginUiActivator.getInstance().getContext();
      context.registerService(SafeWorkspaceAccess.class.getName(), new SafeWorkspaceAccessImpl(), new Hashtable());
   }

}
