/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.action;

import java.util.ArrayList;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.coverage.editor.CoverageEditorWorkProductTab;
import org.eclipse.osee.coverage.model.WorkProductAction;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class RemoveRelatedWorkProductAction extends Action {

   private final CoverageEditorWorkProductTab coverageEditorWorkProductTab;

   public RemoveRelatedWorkProductAction(CoverageEditorWorkProductTab coverageEditorWorkProductTab) {
      super("Remove Related Work Product Action");
      this.coverageEditorWorkProductTab = coverageEditorWorkProductTab;
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.DELETE);
   }

   @Override
   public void run() {
      final ArrayList<WorkProductAction> selActions = coverageEditorWorkProductTab.getSelectedActions();

      if (selActions.isEmpty()) {
         AWorkbench.popup("Please select work product to remove");
         return;
      }
      if (MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), getText(),
         "Remove selected work product actions?")) {

         Job job = new Job(getText()) {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
               coverageEditorWorkProductTab.getCoveragePackage().getWorkProductTaskProvider().removeWorkProductAction(
                  selActions.iterator().next());
               Displays.ensureInDisplayThread(new Runnable() {

                  @Override
                  public void run() {
                     coverageEditorWorkProductTab.getCoveragePackage().getWorkProductTaskProvider().reload();
                     coverageEditorWorkProductTab.refresh();
                  }
               });
               return Status.OK_STATUS;
            }
         };
         Jobs.startJob(job);
      }
   }
}
