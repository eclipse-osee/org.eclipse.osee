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
package org.eclipse.osee.framework.ui.skynet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.MutableBoolean;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.ui.plugin.OseeStatusContributionItem;
import org.eclipse.osee.framework.ui.skynet.preferences.ConfigurationDetails;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.dialogs.WorkbenchPreferenceDialog;
import org.eclipse.ui.progress.UIJob;

/**
 * @author Roberto E. Escobar
 */
public class OseeBuildTypeContributionItem extends OseeStatusContributionItem {

   private static final String ID = "osee.build.type";

   private static String TOOLTIP = "Version [%s]\nBuild Type[%s]\nDouble-Click for details";

   public OseeBuildTypeContributionItem() {
      super(ID);
      setActionHandler(new OpenConfigDetailsAction());
      updateStatus(true);
   }

   @Override
   protected Image getDisabledImage() {
      return ImageManager.getImage(FrameworkImage.TOOLS);
   }

   @Override
   protected String getDisabledToolTip() {
      return Strings.emptyString();
   }

   @Override
   protected Image getEnabledImage() {
      return ImageManager.getImage(FrameworkImage.TOOLS);
   }

   @Override
   protected String getEnabledToolTip() {
      return String.format(TOOLTIP, getClientVersion(), getBuildDesignation());
   }

   private String getBuildDesignation() {
      String designation = "N/A";
      try {
         designation = ClientSessionManager.getClientBuildDesignation();
      } catch (OseeCoreException ex) {
         // Do Nothing
      }
      return designation;
   }

   private String getClientVersion() {
      String version = "N/A";
      try {
         version = ClientSessionManager.getSession().getVersion();
      } catch (OseeCoreException ex) {
         // Do Nothing
      }
      return version;
   }

   public final static class OpenConfigDetailsAction extends Action {

      private final MutableBoolean isSelectionAllowed;

      public OpenConfigDetailsAction() {
         super("", SWT.PUSH);
         isSelectionAllowed = new MutableBoolean(true);
      }

      @Override
      public void run() {
         if (isSelectionAllowed.getValue()) {
            Job job = new UIJob("Open OSEE Configuration Details Page") {

               @Override
               public IStatus runInUIThread(IProgressMonitor monitor) {
                  Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
                  WorkbenchPreferenceDialog dialog =
                     WorkbenchPreferenceDialog.createDialogOn(shell, ConfigurationDetails.PAGE_ID);
                  isSelectionAllowed.setValue(false);
                  dialog.open();
                  isSelectionAllowed.setValue(true);
                  return Status.OK_STATUS;
               }
            };
            Jobs.startJob(job);
         }
      }
   }
}
