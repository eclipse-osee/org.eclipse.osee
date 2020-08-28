/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.ats.ide.workflow.task.related;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.api.task.related.DerivedFromTaskData;
import org.eclipse.osee.ats.api.task.related.IAutoGenTaskData;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsBranchService;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.MutableInteger;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.revision.ChangeData;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBox;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredTreeBranchDialog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Donald G. Dunne
 */
public final class ShowRequirementDifferencesOperation extends AbstractOperation {

   public static interface Display {
      Artifact getArtifactSelection(IProgressMonitor monitor, Collection<? extends Artifact> selectableArtifacts) throws Exception;

      void showDifferences(Collection<ArtifactDelta> artifactDeltas);
   }

   private final Display display;
   private final Collection<? extends IAtsTask> tasks;
   private final Set<IOseeBranch> savedBranches = new LinkedHashSet<>();
   private static final IOseeBranch BASELINE_ROOT = IOseeBranch.create("Use root version branch (default)");

   public ShowRequirementDifferencesOperation(Display display, Collection<? extends IAtsTask> tasks, boolean useDefault) {
      super("Show Requirement Differences", Activator.PLUGIN_ID);
      this.display = display;
      this.tasks = tasks;
      if (useDefault) {
         savedBranches.add(BASELINE_ROOT);
      }
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      List<ArtifactDelta> artifactDeltas = new ArrayList<>();
      Set<String> processedReqNames = new HashSet<>();
      Map<IAtsTeamWorkflow, ChangeData> changeDataMap = new HashMap<>();
      XResultData results = new XResultData();

      for (IAtsTask task : tasks) {
         IAutoGenTaskData data = AtsApiService.get().getTaskRelatedService().getAutoGenTaskData(task);
         checkForNoRequirementArtifacts(data);
         if (processedReqNames.contains(data.getRelatedArtName() + data.getAddDetails())) {
            continue;
         }

         DerivedFromTaskData trd =
            AtsApiService.get().getTaskRelatedService().getDerivedTeamWf(new DerivedFromTaskData(task));
         if (trd.getResults().isErrors()) {
            results.addRaw(trd.getResults().toString());
            continue;
         }
         IAtsTeamWorkflow sourceTeamWf = trd.getDerivedFromTeamWf();

         ChangeData changeData;
         monitor.setTaskName("Load Change Report");
         if (changeDataMap.containsKey(sourceTeamWf)) {
            changeData = changeDataMap.get(sourceTeamWf);
         } else {
            IAtsBranchService branchService = AtsApiService.get().getBranchService();
            if (branchService.isWorkingBranchInWork(sourceTeamWf)) {
               changeData = AtsApiService.get().getBranchServiceIde().getChangeDataFromEarliestTransactionId(sourceTeamWf);
            } else {
               IAtsVersion taskTargetedVersion = AtsApiService.get().getVersionService().getTargetedVersion(task);
               Collection<TransactionRecord> transactions =
                  Collections.castAll(branchService.getTransactionIds(sourceTeamWf, false));
               MutableInteger result = new MutableInteger(0);
               TransactionRecord transaction =
                  determineTransactionId(transactions, task.getName(), taskTargetedVersion.getName(), result);
               monitor.setCanceled(result.getValue() != 0);
               checkForCancelledStatus(monitor);
               changeData = createChangeDataFromTransaction(transaction);
            }
            changeDataMap.put(sourceTeamWf, changeData);
         }

         monitor.worked(calculateWork(0.70));

         Collection<Change> changes = null;
         if (data.getRelatedArtId().isValid()) {
            changes = changeData.getArtifactChangesById(data.getRelatedArtId());
         } else {
            changes = changeData.getArtifactChangesByName(data.getRelatedArtName(), data.getAddDetails());
         }
         checkForCancelledStatus(monitor);
         if (!changes.isEmpty()) {
            if (tasks.size() == 1) {
               Change change = selectChangeToDiff(monitor, changes);
               artifactDeltas.add(change.getDelta());
            } else {
               for (Change change : changes) {
                  artifactDeltas.add(change.getDelta());
               }
            }
         }
         processedReqNames.add(data.getRelatedArtName() + data.getAddDetails());
      }

      display.showDifferences(artifactDeltas);
      if (results.isEmpty()) {
         results.log("Opening Differences in Word");
      }
      XResultDataUI.report(results, getName());
      monitor.worked(calculateWork(0.10));
   }

   private void checkForNoRequirementArtifacts(IAutoGenTaskData data) {
      if (!data.hasRelatedArt()) {
         throw new OseeArgumentException("No related artifact found for %s", data.getTask().toStringWithId());
      }
   }

   private Change selectChangeToDiff(IProgressMonitor monitor, Collection<Change> changes) throws Exception {
      Change change = null;
      if (changes.size() == 1) {
         change = changes.iterator().next();
      } else {
         Set<Artifact> selectableArtifacts = new HashSet<>();
         for (Change artChange : changes) {
            Artifact changeArtifact = artChange.getChangeArtifact();
            if (changeArtifact.isValid()) {
               selectableArtifacts.add(changeArtifact);
            }
         }
         Artifact selectedArtifact = null;
         if (selectableArtifacts.size() == 1) {
            selectedArtifact = selectableArtifacts.iterator().next();
         } else {
            selectedArtifact = display.getArtifactSelection(monitor, selectableArtifacts);
         }
         if (selectedArtifact == null) {
            return null;
         }
         checkForCancelledStatus(monitor);

         for (Change artChange : changes) {
            if (artChange.getChangeArtifact().equals(selectedArtifact)) {
               change = artChange;
               break;
            }
         }
      }
      return change;
   }

   private TransactionRecord findMinBaselineTransaction(Collection<TransactionRecord> records) {
      Iterator<TransactionRecord> it = records.iterator();
      TransactionRecord min = it.next();
      while (it.hasNext()) {
         TransactionRecord next = it.next();

         if (BranchManager.getBaseTransaction(min.getBranch()).getId() > BranchManager.getBaseTransaction(
            next.getBranch()).getId()) {
            min = next;
         }
      }
      return min;
   }

   private ChangeData createChangeDataFromTransaction(TransactionRecord transactionId) {
      Collection<Change> changes = new LinkedList<>();
      IOperation operation = ChangeManager.comparedToPreviousTx(transactionId, changes);
      Operations.executeWorkAndCheckStatus(operation);
      return new ChangeData(changes);
   }

   private TransactionRecord determineTransactionId(Collection<TransactionRecord> records, String taskName, String versionName, MutableInteger dialogResult) {
      TransactionRecord toReturn;
      if (records.size() == 1) {
         toReturn = records.iterator().next();
      } else {
         TransactionRecord min = findMinBaselineTransaction(records);

         if (savedBranches.contains(BASELINE_ROOT)) {
            toReturn = min;
         } else {
            final Map<IOseeBranch, TransactionRecord> branchToTx = new LinkedHashMap<>();
            for (TransactionRecord record : records) {
               IOseeBranch branch = BranchManager.getBranchToken(record.getBranch());
               branchToTx.put(branch, record);
            }
            toReturn = getTransactionFromSavedOrUser(branchToTx, taskName, versionName, dialogResult);
         }

      }

      return toReturn;
   }

   private TransactionRecord getTransactionFromSavedOrUser(Map<IOseeBranch, TransactionRecord> branchToTx, String taskName, String versionName, final MutableInteger dialogResult) {
      TransactionRecord toReturn = null;
      Set<BranchId> branchesInMap = new LinkedHashSet<>(branchToTx.keySet());
      // remove everything but the user saved branches
      branchesInMap.retainAll(savedBranches);
      if (branchesInMap.size() == 1) {
         toReturn = branchToTx.get(branchesInMap.iterator().next());
      } else if (branchesInMap.size() > 1) {
         savedBranches.removeAll(branchesInMap);
      }

      // prompt the user
      if (toReturn == null) {
         String title = "Multiple Commits Associated With Task";
         String message = String.format("Task named [%s]\nIs targeted for [%s].\nSelect which to diff against:",
            taskName, versionName);

         BranchListDialogRunnable bldr = new BranchListDialogRunnable(title, message, branchToTx.keySet());

         Displays.ensureInDisplayThread(bldr, true);
         dialogResult.setValue(bldr.getResult());

         // user hit OK
         if (dialogResult.getValue() == 0) {
            IOseeBranch branch = bldr.getSelectedBranch();
            toReturn = branchToTx.get(branch);
            if (bldr.getApplyToAll()) {
               savedBranches.add(branch);
            }
         }
      }

      // ok to return null if user is prompted and hit cancel
      return toReturn;
   }

   private class BranchListDialogRunnable implements Runnable {

      private final String title;
      private final String message;
      private final Collection<IOseeBranch> branchChoices;
      private int result;
      private IOseeBranch selectedBranch;
      private Boolean applyToAll;

      public BranchListDialogRunnable(String title, String message, Collection<IOseeBranch> branchChoices) {
         this.title = title;
         this.message = message;
         this.branchChoices = branchChoices;
      }

      @Override
      public void run() {
         BranchListDialog dialog = new BranchListDialog(title, message, branchChoices);
         dialog.setMultiSelect(false);
         result = dialog.open();
         if (result == 0) {
            selectedBranch = dialog.getSelectedFirst();
         }
         applyToAll = dialog.getApplyToAll();
      }

      public int getResult() {
         return result;
      }

      public IOseeBranch getSelectedBranch() {
         return selectedBranch;
      }

      public boolean getApplyToAll() {
         return applyToAll;
      }

   }

   private class BranchListDialog extends FilteredTreeBranchDialog {

      XCheckBox applyToAll;

      public BranchListDialog(String title, String message, Collection<IOseeBranch> branchChoices) {
         super(title, message, branchChoices);
         setInput(branchChoices);
      }

      @Override
      protected Control createDialogArea(Composite container) {
         Control control = super.createDialogArea(container);

         Composite comp = new Composite(control.getParent(), SWT.NONE);
         comp.setLayout(new GridLayout(2, false));
         comp.setLayoutData(new GridData(GridData.FILL_BOTH));

         applyToAll = new XCheckBox("Apply to all");
         applyToAll.set(true);
         applyToAll.createWidgets(comp, 2);

         return control;
      }

      private Boolean getApplyToAll() {
         return applyToAll.isChecked();
      }

   }

}
