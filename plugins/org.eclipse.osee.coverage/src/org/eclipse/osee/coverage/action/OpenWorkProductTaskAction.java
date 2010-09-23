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
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.coverage.model.IWorkProductRelatable;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.cm.IOseeCmService;
import org.eclipse.osee.framework.ui.skynet.cm.OseeCmEditor;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class OpenWorkProductTaskAction extends Action {

   private final ISelectedCoverageEditorItem selectedCoverageEditorItem;

   public OpenWorkProductTaskAction(ISelectedCoverageEditorItem selectedCoverageEditorItem) {
      super("Open Work Product Task");
      this.selectedCoverageEditorItem = selectedCoverageEditorItem;
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.OPEN);
   }

   @Override
   public void run() {

      if (selectedCoverageEditorItem.getSelectedCoverageEditorItems().isEmpty()) {
         AWorkbench.popup("Select Coverage Item(s) or Coverage Units(s)");
         return;
      }

      final IOseeCmService cm = SkynetGuiPlugin.getInstance().getOseeCmService();
      if (cm == null) {
         AWorkbench.popup("Unable to connect to CM service.");
         return;
      }

      final List<String> workProductGuids = new ArrayList<String>();
      for (ICoverage coverage : selectedCoverageEditorItem.getSelectedCoverageEditorItems()) {
         if (coverage instanceof IWorkProductRelatable && Strings.isValid(((IWorkProductRelatable) coverage).getWorkProductGuid())) {
            workProductGuids.add(((IWorkProductRelatable) coverage).getWorkProductGuid());
         }
      }
      if (workProductGuids.isEmpty()) {
         AWorkbench.popup("No Coverage Item(s) and Coverage Units(s) related.");
         return;
      }

      Job job = new Job("Open " + getText()) {
         @Override
         protected IStatus run(IProgressMonitor monitor) {
            try {
               List<Artifact> artifacts =
                  ArtifactQuery.getArtifactListFromIds(workProductGuids, BranchManager.getCommonBranch());
               cm.openArtifacts("Related Work Products", artifacts, OseeCmEditor.CmMultiPcrEditor);
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
            return Status.OK_STATUS;
         }
      };
      job.schedule();

   }
}
