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
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.coverage.editor.xcover.CoverageXViewer;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.coverage.model.IWorkProductRelatable;
import org.eclipse.osee.coverage.model.WorkProductAction;
import org.eclipse.osee.coverage.model.WorkProductTask;
import org.eclipse.osee.coverage.util.ISaveable;
import org.eclipse.osee.coverage.util.dialog.WorkProductListDialog;
import org.eclipse.osee.coverage.util.dialog.WorkProductTaskFilteredTreeEntryDialog;
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
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class CreateWorkProductTaskAction extends Action {

   private final ISelectedCoverageEditorItem selectedCoverageEditorItem;
   private final ISaveable saveable;
   private final CoverageXViewer coverageXViewer;

   public CreateWorkProductTaskAction(CoverageXViewer coverageXViewer, ISelectedCoverageEditorItem selectedCoverageEditorItem, ISaveable saveable) {
      super("Create/Update Work Product Task");
      this.coverageXViewer = coverageXViewer;
      this.selectedCoverageEditorItem = selectedCoverageEditorItem;
      this.saveable = saveable;
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.REPORT);
   }

   @Override
   public void run() {
      if (coverageXViewer.getWorkProductTaskProvider().getWorkProductRelatedActions().isEmpty()) {
         AWorkbench.popup("No Work Product related Actions.\n\nMust related Work Product Actions first.");
         return;
      }

      if (selectedCoverageEditorItem.getSelectedCoverageEditorItems().isEmpty()) {
         AWorkbench.popup("Select Coverage Item(s) or Coverage Units(s)");
         return;
      }

      List<ICoverage> relateableCoverageItems = new ArrayList<ICoverage>();
      for (ICoverage coverage : selectedCoverageEditorItem.getSelectedCoverageEditorItems()) {
         if (coverage instanceof IWorkProductRelatable) {
            relateableCoverageItems.add(coverage);
         }
      }
      if (relateableCoverageItems.isEmpty()) {
         AWorkbench.popup("Only Coverage Item(s) and Coverage Units(s) can be related to Work Products");
         return;
      }
      IOseeCmService cm = SkynetGuiPlugin.getInstance().getOseeCmService();
      if (cm == null) {
         AWorkbench.popup("Unable to connect to CM service.");
         return;
      }

      WorkProductListDialog dialog = new WorkProductListDialog(getText(), "Select Work Product");
      dialog.setInput(coverageXViewer.getWorkProductTaskProvider().getWorkProductRelatedActions());
      if (dialog.open() != 0) {
         return;
      }
      WorkProductAction action = (WorkProductAction) dialog.getResult()[0];

      WorkProductTaskFilteredTreeEntryDialog taskDialog =
         new WorkProductTaskFilteredTreeEntryDialog(getText(), "Select existing task or enter new task name",
            "New Task Name");
      taskDialog.setInput(action.getTasks());
      if (taskDialog.open() != 0) {
         return;
      }

      try {
         String taskTitle = taskDialog.getEntryValue();
         Artifact newTaskArt = null;
         if (Strings.isValid(taskTitle)) {
            newTaskArt = cm.createWorkTask(taskTitle, action.getGuid());
         } else {
            WorkProductTask task = taskDialog.getSelection();
            newTaskArt = ArtifactQuery.getArtifactFromId(task.getGuid(), BranchManager.getCommonBranch());
         }
         if (newTaskArt == null) {
            AWorkbench.popup("Unable to create new Work Product task.");
            return;
         }

         for (ICoverage coverage : relateableCoverageItems) {
            if (coverage instanceof IWorkProductRelatable) {
               ((IWorkProductRelatable) coverage).setWorkProductGuid(newTaskArt.getGuid());
            }
         }
         newTaskArt.persist(getText());
         saveable.save(relateableCoverageItems);

         coverageXViewer.getWorkProductTaskProvider().reload();
         for (ICoverage coverage : relateableCoverageItems) {
            if (coverage instanceof IWorkProductRelatable) {
               ((IWorkProductRelatable) coverage).setWorkProductTask(coverageXViewer.getWorkProductTaskProvider().getWorkProductTask(
                  newTaskArt.getGuid()));
               coverageXViewer.update(coverage);
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }
}
