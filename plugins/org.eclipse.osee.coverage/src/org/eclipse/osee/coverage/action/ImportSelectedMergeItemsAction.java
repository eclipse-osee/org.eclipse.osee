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

import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.coverage.editor.CoverageEditorInput;
import org.eclipse.osee.coverage.editor.CoverageEditorMergeTab;
import org.eclipse.osee.coverage.editor.IMergeItemSelectionProvider;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.merge.IMergeItem;
import org.eclipse.osee.coverage.merge.MergeImportManager;
import org.eclipse.osee.coverage.merge.MessageMergeItem;
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.coverage.util.ISaveable;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.CheckBoxDialog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class ImportSelectedMergeItemsAction extends Action {

   private final CoverageEditorMergeTab mergeTab;
   private final IMergeItemSelectionProvider mergeItemSelectionProvider;

   public ImportSelectedMergeItemsAction(IMergeItemSelectionProvider mergeItemSelectionProvider, CoverageEditorMergeTab mergeTab) {
      super("Import Checked Merge Items into stored Coverage Package");
      this.mergeItemSelectionProvider = mergeItemSelectionProvider;
      this.mergeTab = mergeTab;
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.ARROW_LEFT_YELLOW);
   }

   @Override
   public void run() {
      try {
         Artifact artifact = ((CoverageEditorInput) mergeTab.getEditorInput()).getCoveragePackageArtifact();
         if (artifact != null) {
            CoverageUtil.setNavigatorSelectedBranch(artifact.getBranch());
         } else {
            if (!CoverageUtil.getBranchFromUser(true)) {
               return;
            }
         }
         handleImportSelected();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

   }

   private void handleImportSelected() throws OseeCoreException {
      final ISaveable saveable = mergeTab;
      final Collection<IMergeItem> mergeItems = mergeItemSelectionProvider.getSelectedMergeItems();
      if (mergeItems.isEmpty()) {
         AWorkbench.popup("Select Items to Import via Import Column");
         return;
      }
      final CheckBoxDialog dialog =
         new CheckBoxDialog("Import Items", String.format("Importing [%d] items.", mergeItems.size()),
            "Save Import Record?");
      if (dialog.open() == 0) {
         mergeTab.getxImportViewer2().getXViewer().setInput(new MessageMergeItem("Merging..."));
         mergeTab.getxImportViewer2().getXViewer().refresh();
         mergeTab.setLoading(true);
         Job job = new Job("Coverage Merge") {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
               try {
                  final XResultData rd =
                     new MergeImportManager(mergeTab.getMergeManager()).importItems(saveable, mergeItems);
                  if (dialog.isChecked()) {
                     IOseeBranch branch = mergeTab.getBranch();
                     if (branch == null) {
                        return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Can't determine branch.");
                     }
                     SkynetTransaction transaction =
                        TransactionManager.createTransaction(branch,
                           "Save Import Record - " + mergeTab.getCoverageImport().getName());
                     mergeTab.saveImportRecord(transaction, mergeTab.getCoverageImport());
                     transaction.execute();
                  }
                  Displays.ensureInDisplayThread(new Runnable() {
                     @Override
                     public void run() {
                        mergeTab.setLoading(false);
                        XResultDataUI.report(rd, "Import");
                        mergeTab.handleSearchButtonPressed();
                        mergeTab.updateTitles();
                        mergeTab.loadImportViewer(true, false);
                     }

                  });
                  return Status.OK_STATUS;
               } catch (OseeCoreException ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
                  return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Coverage Save Failed", ex);
               }
            }
         };
         Jobs.startJob(job, false);
      }
   }

   @Override
   public String getToolTipText() {
      return "Import Selected Items into Coverage Package";
   };

}
