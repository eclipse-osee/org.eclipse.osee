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
package org.eclipse.osee.ote.runtimemanager;

import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.ote.core.ServiceUtility;
import org.eclipse.ui.IStartup;

/**
 * @author Andrew M. Finkbeiner
 */
public class EarlyStartup implements IStartup {

   private SafeWorkspaceTracker workspaceTracker;

   @Override
   public void earlyStartup() {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            try {
               workspaceTracker = new SafeWorkspaceTracker(ServiceUtility.getContext());
               workspaceTracker.open(true);
            } catch (Throwable ex) {
               OseeLog.log(RuntimeManager.class, Level.SEVERE, ex);
            }
         }
      });
   }
}
