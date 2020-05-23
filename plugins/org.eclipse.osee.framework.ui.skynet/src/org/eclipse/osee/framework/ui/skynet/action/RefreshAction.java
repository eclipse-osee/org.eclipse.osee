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

package org.eclipse.osee.framework.ui.skynet.action;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class RefreshAction extends Action {

   private final IRefreshActionHandler iRefreshActionHandler;

   public static interface IRefreshActionHandler {
      public void refreshActionHandler();
   }

   public RefreshAction(IRefreshActionHandler iRefreshActionHandler) {
      this.iRefreshActionHandler = iRefreshActionHandler;
      setImageDescriptor(ImageManager.getImageDescriptor(PluginUiImage.REFRESH));
      setToolTipText("Refresh");
   }

   @Override
   public void run() {
      Jobs.startJob(new Job("Refresh") {

         @Override
         protected IStatus run(IProgressMonitor monitor) {
            try {
               iRefreshActionHandler.refreshActionHandler();
            } catch (Exception ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
            return Status.OK_STATUS;
         }
      }, true);
   }
}
