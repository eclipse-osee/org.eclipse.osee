/*********************************************************************
 * Copyright (c) 2013 Boeing
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
import org.eclipse.osee.framework.jdk.core.type.MutableBoolean;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.OseePreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.internal.dialogs.WorkbenchPreferenceDialog;
import org.eclipse.ui.progress.UIJob;

@SuppressWarnings("restriction")
public class OpenOseePropertiesAction extends Action {

   private final MutableBoolean isSelectionAllowed;

   public OpenOseePropertiesAction() {
      super("", SWT.PUSH);
      isSelectionAllowed = new MutableBoolean(true);
   }

   @Override
   public void run() {
      if (isSelectionAllowed.getValue()) {
         Job job = new UIJob("Open OSEE Preferences Page") {

            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
               Shell shell = AWorkbench.getActiveShell();
               WorkbenchPreferenceDialog dialog =
                  WorkbenchPreferenceDialog.createDialogOn(shell, OseePreferencePage.PAGE_ID);
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
