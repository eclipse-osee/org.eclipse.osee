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
package org.eclipse.osee.framework.ui.workspacebundleloader;

import java.util.logging.Level;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.workspacebundleloader.internal.Activator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IStartup;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

/**
 * @author Andrew M. Finkbeiner
 */
public class EarlyStartup implements IStartup {

   private SafeWorkspaceTracker workspaceTracker;

   @Override
   public void earlyStartup() {
      Display.getDefault().asyncExec(new Runnable() {
         @Override
         public void run() {
            try {
               Bundle bundle = Platform.getBundle("org.eclipse.osee.framework.ui.workspacebundleloader");
               bundle.start();
               workspaceTracker = new SafeWorkspaceTracker(bundle.getBundleContext());
               workspaceTracker.open(true);
            } catch (BundleException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      });

   }
}
