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

package org.eclipse.osee.framework.ui.plugin.workspace;

import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.ui.plugin.internal.UiPluginConstants;
import org.eclipse.osee.framework.ui.plugin.workspace.internal.SafeWorkspaceAccessImpl;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.ui.IStartup;
import org.osgi.framework.Bundle;

/**
 * @author Andrew M. Finkbeiner
 */
public class EarlyStartup implements IStartup {

   @Override
   public void earlyStartup() {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            registerWorkspaceAccessService();
         }
      });
   }

   private void registerWorkspaceAccessService() {
      Bundle bundle = Platform.getBundle(UiPluginConstants.PLUGIN_ID);
      bundle.getBundleContext().registerService(SafeWorkspaceAccess.class.getName(), new SafeWorkspaceAccessImpl(),
         null);
   }

}
