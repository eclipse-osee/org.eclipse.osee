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

package org.eclipse.osee.framework.ui.skynet.widgets.xmerge;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import org.apache.commons.lang.mutable.MutableBoolean;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.ConflictStatus;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.operation.FinishUpdateBranchOperation;
import org.eclipse.osee.framework.skynet.core.conflict.Conflict;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictManagerExternal;
import org.eclipse.osee.framework.skynet.core.httpRequests.CommitBranchHttpRequestOperation;
import org.eclipse.osee.framework.skynet.core.revision.ConflictManagerInternal;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.ListSelectionDialogNoSave;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.change.ChangeUiUtil;
import org.eclipse.osee.framework.ui.skynet.cm.IOseeCmService;
import org.eclipse.osee.framework.ui.skynet.cm.OseeCmEditor;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.GenericXWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.CheckBoxDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IOseeTreeReportProvider;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PlatformUI;

/**
 * @author Donald G. Dunne
 * @author Theron Virgin
 */
public class MergeXWidget extends GenericXWidget implements IOseeTreeReportProvider {
   private static final String COMPLETE_COMMIT_ACTION_ID = "complete.commit.action.id";
   private static final String REFRESH_ACTION_ID = "refresh.action.id";
   private MergeXViewer mergeXViewer;
   private IDirtiableEditor editor;
   public final static String normalColor = "#EEEEEE";
   private static final String LOADING = "Loading ...";
   private static final String NO_CONFLICTS = "No conflicts were found";
   private static final String CONFLICTS_NOT_LOADED = "Cleared on shutdown.  Refresh to Reload.";
   private Label extraInfoLabel;
   private String displayLabelText;
   private Action openAssociatedArtifactAction;
   private BranchToken sourceBranch;
   private BranchToken destBranch;
   private TransactionToken commitTrans;
   private TransactionToken tranId;
   private MergeView mergeView;
   private final static String CONFLICTS_RESOLVED = "\nAll Conflicts Are Resolved";

   public MergeXWidget(MergeView mergeView) {
      super("Merge Manager");
      this.mergeView = mergeView;
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {

      Composite mainComp = new Composite(parent, SWT.BORDER);
      mainComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      mainComp.setLayout(ALayout.getZeroMarginLayout());
      if (toolkit != null) {
         toolkit.paintBordersFor(mainComp);
      }

      Composite taskComp = new Composite(mainComp, SWT.NONE);
      taskComp.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
      taskComp.setLayout(ALayout.getZeroMarginLayout());
      createTextWidgets(parent);

      createTaskActionLabel(taskComp);

      createCompletionComposite(mainComp);

      mergeXViewer = new MergeXViewer(mainComp, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION, mergeView, this);
      mergeXViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
      XMergeLabelProvider labelProvider = new XMergeLabelProvider(mergeXViewer);
      mergeXViewer.addLabelProvider(labelProvider);
      mergeXViewer.setSorter(new MergeXViewerSorter(mergeXViewer, labelProvider));
      mergeXViewer.setContentProvider(new XMergeContentProvider());
      mergeXViewer.setLabelProvider(new XMergeLabelProvider(mergeXViewer));

      Tree tree = mergeXViewer.getTree();
      createTree(tree);

      if (toolkit != null) {
         toolkit.adapt(mergeXViewer.getStatusLabel(), false, false);
      }
   }

   private void createCompletionComposite(Composite mainComp) {
      Composite completeComp = new Composite(mainComp, SWT.NONE);
      completeComp.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
      completeComp.setLayout(new GridLayout(2, false));

      new Label(completeComp, SWT.NONE).setText("Resolve all conflicts and ");
      Button completeButton = new Button(completeComp, SWT.PUSH);
      completeButton.setText("Complete Commit");
      completeButton.setImage(ImageManager.getImage(FrameworkImage.BRANCH_COMMIT));
      completeButton.setToolTipText("Commit changes into destination branch");
      setId(COMPLETE_COMMIT_ACTION_ID);
      completeButton.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            if (hasMergeBranchBeenCommitted()) {
               AWorkbench.popup("Branch has been committed");
            } else if (isCompleteForCommit()) {
               if (MessageDialog.openQuestion(Display.getCurrent().getActiveShell(), "Complete Commit",
                  "Are you sure?")) {
                  completeCommit();
               }
            } else {
               AWorkbench.popup("All conflicts must be resolved");
            }
         }

      });
   }

   private void createTextWidgets(Composite parent) {
      if (isDisplayLabel() && !getLabel().equals("")) {
         labelWidget = new Label(parent, SWT.NONE);
         labelWidget.setText(getLabel() + ":");
         if (getToolTip() != null) {
            labelWidget.setToolTipText(getToolTip());
         }
      }
   }

   private void createTree(Tree tree) {
      GridData gridData = new GridData(GridData.FILL_BOTH);
      gridData.heightHint = 100;
      tree.setLayout(ALayout.getZeroMarginLayout());
      tree.setLayoutData(gridData);
      tree.setHeaderVisible(true);
      tree.setLinesVisible(true);
   }

   public void createTaskActionLabel(Composite parent) {
      Composite composite = new Composite(parent, SWT.NONE);
      GridLayout layout = ALayout.getZeroMarginLayout(2, false);
      layout.marginLeft = 5;
      composite.setLayout(layout);
      composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      extraInfoLabel = new Label(composite, SWT.NONE);
      extraInfoLabel.setAlignment(SWT.LEFT);
      extraInfoLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      extraInfoLabel.setText("\n");
   }

   public void createTaskActionBar() {
      IToolBarManager manager = mergeView.getViewSite().getActionBars().getToolBarManager();
      manager.add(new RefreshAction());
      manager.add(new Separator());
      openAssociatedArtifactAction = new OpenAssociatedArtifactAction();
      manager.add(openAssociatedArtifactAction);
      manager.add(new Separator());
      manager.add(new ApplyPriorMergeResultsAction());
      manager.add(new Separator());
      manager.add(new ShowSourceBranchChangeReportAction());
      manager.add(new ShowDestinationBranchChangeReportAction());
      manager.add(new Separator());
      manager.add(mergeXViewer.getCustomizeAction());
      mergeView.getViewSite().getActionBars().updateActionBars();
      manager.update(true);
   }

   private void applyPreviousMerge(final BranchToken destinationBranch) {
      Job job = new Job("Apply Previous Merge") {

         @Override
         protected IStatus run(final IProgressMonitor monitor) {
            Conflict[] conflicts = getConflicts();

            monitor.beginTask("ApplyingPreviousMerge", conflicts.length);
            for (Conflict conflict : conflicts) {
               try {
                  BranchToken mergeBranch = BranchManager.getMergeBranch(conflict.getSourceBranch(), destinationBranch);
                  conflict.applyPreviousMerge(mergeBranch, destinationBranch);
               } catch (OseeCoreException ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
               } finally {
                  monitor.worked(1);
               }
            }
            monitor.done();
            return Status.OK_STATUS;
         }
      };

      Jobs.startJob(job, new JobChangeAdapter() {
         @Override
         public void done(IJobChangeEvent event) {
            loadTable();
         }
      });
   }

   public void refreshTable() {
      Job job = new Job("Loading Merge Manager") {
         @Override
         protected IStatus run(IProgressMonitor monitor) {
            try {
               Conflict[] conflicts = getConflicts();
               Conflict[] artifactChanges = new Conflict[0];
               if (conflicts[0].getToTransactionId().isValid()) {
                  setConflicts(ConflictManagerInternal.getConflictsPerBranch(conflicts[0].getSourceBranch(),
                     conflicts[0].getDestBranch(), conflicts[0].getToTransactionId(), monitor).toArray(
                        artifactChanges));
               } else {
                  setConflicts(ConflictManagerInternal.getConflictsPerBranch(conflicts[0].getCommitTransactionId(),
                     monitor).toArray(artifactChanges));
               }
            } catch (Exception ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }

            return Status.OK_STATUS;
         }
      };
      Jobs.startJob(job, new JobChangeAdapter() {
         @Override
         public void done(IJobChangeEvent event) {
            loadTable();
         }
      });
   }

   public void loadTable() {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            refresh();
         }
      });
   }

   @Override
   public Control getControl() {
      return mergeXViewer.getTree();
   }

   @Override
   public void dispose() {
      mergeXViewer.dispose();
   }

   public Conflict[] getConflicts() {
      return mergeXViewer != null ? mergeXViewer.getConflicts() : MergeXViewer.EMPTY_CONFLICTS;
   }

   @Override
   public void refresh() {
      mergeXViewer.refresh();
      validate();
      mergeView.showConflicts(true);
      int resolved = 0;
      int informational = 0;
      Conflict[] conflicts = getConflicts();
      if (conflicts.length > 0) {
         for (Conflict conflict : conflicts) {
            ConflictStatus status = conflict.getStatus();
            if (status.isResolved() || status.isCommitted()) {
               resolved++;
            }
            if (status.isInformational()) {
               informational++;
            }
         }
         if (hasMergeBranchBeenCommitted()) {
            extraInfoLabel.setText(displayLabelText + "\nBranch has been committed.");
         } else if (resolved == conflicts.length) {
            extraInfoLabel.setText(displayLabelText + CONFLICTS_RESOLVED);
         } else {
            extraInfoLabel.setText(
               displayLabelText + "\nConflicts : " + (conflicts.length - informational) + " <=> Resolved : " + resolved + (informational == 0 ? " " : "\nInformational Conflicts : " + informational));
         }
      }
   }

   private boolean areAllConflictsResolved() {
      int resolved = 0;
      Conflict[] conflicts = getConflicts();
      for (Conflict conflict : conflicts) {
         ConflictStatus status = conflict.getStatus();
         if (status.isConsideredResolved()) {
            resolved++;
         }
      }
      return resolved == conflicts.length;
   }

   @Override
   public IStatus isValid() {
      return Status.OK_STATUS;
   }

   @Override
   public boolean isEmpty() {
      return mergeXViewer.getTree().getItemCount() == 0;
   }

   public MergeXViewer getXViewer() {
      return mergeXViewer;
   }

   @Override
   public Object getData() {
      return getConflicts();
   }

   public IDirtiableEditor getEditor() {
      return editor;
   }

   public void setEditor(IDirtiableEditor editor) {
      this.editor = editor;
   }

   public void setInputData(final BranchToken sourceBranch, final BranchToken destBranch, final TransactionToken tranId, final MergeView mergeView, final TransactionToken commitTrans, boolean showConflicts) {
      setInputData(sourceBranch, destBranch, tranId, mergeView, commitTrans, "", showConflicts);
   }

   public void setInputData(final BranchToken sourceBranch, final BranchToken destBranch, final TransactionToken tranId, final MergeView mergeView, final TransactionToken commitTx, String loadingText, final boolean showConflicts) {
      this.sourceBranch = sourceBranch;
      this.destBranch = destBranch;
      this.tranId = tranId;
      this.mergeView = mergeView;
      this.commitTrans = commitTx;
      extraInfoLabel.setText(LOADING + loadingText);
      Job job = new Job("Loading Merge Manager") {
         @Override
         protected IStatus run(IProgressMonitor monitor) {
            try {
               if (showConflicts) {
                  Conflict[] conflicts;
                  if (commitTx == null) {
                     conflicts = ConflictManagerInternal.getConflictsPerBranch(sourceBranch, destBranch, tranId,
                        monitor).toArray(new Conflict[0]);
                  } else {
                     conflicts =
                        ConflictManagerInternal.getConflictsPerBranch(commitTx, monitor).toArray(new Conflict[0]);
                  }
                  mergeXViewer.setConflicts(conflicts);
               }
            } catch (Exception ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
            return Status.OK_STATUS;
         }
      };
      Jobs.startJob(job, new JobChangeAdapter() {

         @Override
         public void done(IJobChangeEvent event) {
            Displays.ensureInDisplayThread(new Runnable() {
               @Override
               public void run() {
                  if (Widgets.isAccessible(extraInfoLabel)) {
                     if (showConflicts) {
                        Conflict[] conflicts = getConflicts();
                        if (conflicts.length == 0) {
                           extraInfoLabel.setText(NO_CONFLICTS);
                        } else {
                           setConflicts(conflicts);
                           refresh();
                        }
                     } else {
                        extraInfoLabel.setText(CONFLICTS_NOT_LOADED);
                     }
                  }
               }
            });
         }

      });
      if (sourceBranch != null) {
         refreshAssociatedArtifactItem(sourceBranch);
      }
   }

   private void refreshAssociatedArtifactItem(BranchId sourceBranch) {
      try {
         Artifact branchAssociatedArtifact = BranchManager.getAssociatedArtifact(sourceBranch);
         if (branchAssociatedArtifact.isValid()) {
            openAssociatedArtifactAction.setImageDescriptor(
               ArtifactImageManager.getImageDescriptor(branchAssociatedArtifact));
            openAssociatedArtifactAction.setEnabled(true);
         }
      } catch (ArtifactDoesNotExist ex) {
         PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().hideView(mergeView);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   public void setConflicts(Conflict[] conflicts) throws IllegalStateException {
      mergeXViewer.setConflicts(conflicts);
      loadTable();
      int resolved = 0;
      int informational = 0;
      for (Conflict conflict : getConflicts()) {
         ConflictStatus status = conflict.getStatus();
         if (status.isResolved() || status.isCommitted()) {
            resolved++;
         }
         if (status.isInformational()) {
            informational++;
         }
      }
      Conflict[] storedConflicts = getConflicts();
      if (storedConflicts.length > 0) {
         StringBuilder builder = new StringBuilder();
         if (sourceBranch != null) {
            builder.append("Source Branch :  ");
            builder.append(sourceBranch.getName());
            builder.append("\nDestination Branch :  ");
            builder.append(destBranch.getName());
         } else {
            builder.append("Commit Transaction ID :  ");
            builder.append(commitTrans);
            builder.append(" ");
            builder.append(TransactionManager.getTransaction(commitTrans).getComment());
         }
         displayLabelText = builder.toString();

         if (resolved == storedConflicts.length - informational) {
            extraInfoLabel.setText(displayLabelText + CONFLICTS_RESOLVED);
         } else {
            String message = String.format("%s\nConflicts : %s <=> Resolved : %s%s", displayLabelText,
               storedConflicts.length - informational, resolved,
               informational == 0 ? " " : "\nInformational Conflicts : " + informational);
            extraInfoLabel.setText(message);
         }
      }

   }

   private boolean hasMergeBranchBeenCommitted() {
      Conflict[] conflicts = getConflicts();
      if (conflicts.length > 0) {
         return !BranchManager.isEditable(conflicts[0].getMergeBranch());
      }
      return false;
   }

   private boolean isCompleteForCommit() {
      boolean complete = !hasMergeBranchBeenCommitted() && areAllConflictsResolved() && getConflicts().length > 0;
      if (null != sourceBranch) {
         try {
            boolean rebase = BranchManager.getState(sourceBranch).isRebaselineInProgress();
            boolean isValidUpdate =
               rebase && BranchManager.isParent(sourceBranch, BranchManager.getParentBranch(destBranch));
            // if this is the update from targeted branch case, even if the parent of the target branch is not the
            // same as the parent branch of the source branch, we need to allow the update.
            if (!isValidUpdate) {
               if (BranchManager.getType(destBranch) == BranchType.WORKING) {
                  // override since names are similar and destination isn't a baseline branch (see commit bug:[ats_B2207])
                  isValidUpdate = true;
               }
            }
            boolean isValidCommit = BranchManager.hasMergeBranches(sourceBranch) && !rebase;

            complete &= isValidUpdate || isValidCommit;
         } catch (OseeCoreException ex) {
            Displays.ensureInDisplayThread(new Runnable() {

               @Override
               public void run() {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
            });
            complete = false;
         }
      }
      return complete;
   }

   /**
    * Completes the update branch operation by committing latest parent based branch with branch with changes. Then
    * swaps branches so we are left with the most current branch containing latest changes.
    */
   private void completeCommit() {
      if (mergeView.getMergeBranchForView() != null) {
         try {
            boolean rebase = BranchManager.getState(sourceBranch).isRebaselineInProgress();
            if (rebase) {
               ConflictManagerExternal conflictManager = new ConflictManagerExternal(destBranch, sourceBranch);
               IOperation operation = new FinishUpdateBranchOperation(conflictManager, true, false);
               Operations.executeAsJob(operation, true);
            } else if (BranchManager.hasMergeBranches(sourceBranch) && !rebase) {
               IOseeCmService cm = ServiceUtil.getOseeCmService();

               if (cm.isWorkFlowBranch(sourceBranch)) {
                  Artifact art = BranchManager.getAssociatedArtifact(sourceBranch);
                  boolean isArchiveSourceBranch = cm.isBranchesAllCommittedExcept(art, destBranch);
                  XResultData rd = cm.commitBranch(art, destBranch, isArchiveSourceBranch, new XResultData());
                  if (rd.isErrors()) {
                     XResultDataUI.report(rd, "Commit Error(s)");
                  }
               } else {
                  handleNonAtsCommit(sourceBranch, destBranch);
               }
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }

   }

   private void handleNonAtsCommit(final BranchId sourceBranch, final BranchId destBranch) {
      final MutableBoolean archiveSourceBranch = new MutableBoolean();

      if (BranchManager.isParent(sourceBranch, destBranch)) {
         archiveSourceBranch.setValue(true);
      } else {
         Displays.pendInDisplayThread(new Runnable() {
            @Override
            public void run() {
               CheckBoxDialog dialog = new CheckBoxDialog("Commit Into",
                  String.format("Commit from\n\nSource Branch: [%s]\n\ninto\n\nDestination Branch: [%s]", sourceBranch,
                     destBranch),
                  "Archive Source Branch");
               if (dialog.open() == 0) {
                  archiveSourceBranch.setValue(dialog.isChecked());
               }
            }
         });
      }

      IOperation operation = new CommitBranchHttpRequestOperation(UserManager.getUser(), sourceBranch, destBranch,
         archiveSourceBranch.booleanValue(), false);
      Operations.executeWorkAndCheckStatus(operation, null);
   }

   private final class OpenAssociatedArtifactAction extends Action {

      public OpenAssociatedArtifactAction() {
         super();
         setToolTipText("Open Associated Artifact");
         setEnabled(false);
      }

      @Override
      public void run() {
         Conflict[] storedConflicts = getConflicts();
         try {
            Artifact associatedArtifact = BranchManager.getAssociatedArtifact(storedConflicts[0].getSourceBranch());
            IOseeCmService cmService = ServiceUtil.getOseeCmService();
            if (cmService.isPcrArtifact(associatedArtifact)) {
               cmService.openArtifact(associatedArtifact, OseeCmEditor.CmPcrEditor);
            } else if (associatedArtifact.isValid()) {
               if (SystemUser.OseeSystem.equals(associatedArtifact)) {
                  AWorkbench.popup("ERROR", "No Associated Artifact");
               }
               RendererManager.open(associatedArtifact, PresentationType.SPECIALIZED_EDIT);
            } else {
               AWorkbench.popup("ERROR", "Associated Artifact is invalid");
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }

   private final class ShowSourceBranchChangeReportAction extends Action {

      public ShowSourceBranchChangeReportAction() {
         super();
         //         setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("branch_change_source.gif"));
         setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.BRANCH_CHANGE_SOURCE));
         setToolTipText("Show Source Branch Change Report");
      }

      @Override
      public void run() {
         Conflict[] conflicts = getConflicts();
         if (conflicts.length > 0) {
            Conflict firstConflict = conflicts[0];
            BranchId sourceBranch = firstConflict.getSourceBranch();
            if (sourceBranch != null) {
               try {
                  ChangeUiUtil.open(sourceBranch);
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
            } else {
               try {
                  ChangeUiUtil.open(firstConflict.getCommitTransactionId());
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         }
      }
   }

   private final class ShowDestinationBranchChangeReportAction extends Action {

      public ShowDestinationBranchChangeReportAction() {
         super();
         setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.BRANCH_CHANGE_DEST));
         setToolTipText("Show Destination Branch Change Report");
      }

      @Override
      public void run() {
         Conflict[] conflicts = getConflicts();
         if (conflicts.length > 0) {
            try {
               ChangeUiUtil.open(conflicts[0].getDestBranch());
            } catch (Exception ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      }
   }

   private final class RefreshAction extends Action {

      public RefreshAction() {
         super();
         setImageDescriptor(ImageManager.getImageDescriptor(PluginUiImage.REFRESH));
         setToolTipText("Refresh");
         setId(REFRESH_ACTION_ID);
      }

      @Override
      public void run() {
         setInputData(sourceBranch, destBranch, tranId, mergeView, commitTrans, true);
      }
   }

   private final class ApplyPriorMergeResultsAction extends Action {
      public ApplyPriorMergeResultsAction() {
         super();
         setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.OUTGOING_MERGED));
         setToolTipText("Apply Merge Results From Prior Merge");
      }

      @Override
      public void run() {
         Conflict[] conflicts = getConflicts();
         if (conflicts.length != 0) {
            if (conflicts[0].getSourceBranch() != null) {
               ArrayList<BranchToken> branches = new ArrayList<>();
               try {
                  Collection<BranchToken> destBranches =
                     ConflictManagerInternal.getDestinationBranchesMerged(sourceBranch);
                  for (BranchToken branch : destBranches) {
                     if (destBranch.notEqual(branch)) {
                        branches.add(branch);
                     }
                  }
                  if (branches.size() > 0) {
                     ListSelectionDialogNoSave dialog = new ListSelectionDialogNoSave(Collections.castAll(branches),
                        Displays.getActiveShell().getShell(), "Apply Prior Merge Resolution", null,
                        "Select the destination branch that the previous commit was appplied to", 2,
                        new String[] {"Apply", "Cancel"}, 1);
                     if (dialog.open() == 0) {
                        Branch selected = (Branch) dialog.getSelected();
                        applyPreviousMerge(selected);
                     }
                  }
                  if (branches.isEmpty()) {
                     new MessageDialog(Displays.getActiveShell().getShell(), "Apply Prior Merge Resolution", null,
                        "This Source Branch has had No Prior Merges", 2, new String[] {"OK"}, 1).open();
                  }
               } catch (OseeCoreException ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         }
      }
   }

   @Override
   public String getEditorTitle() {
      try {
         Branch branch = BranchManager.getBranch(sourceBranch);

         return String.format("Table Report - Merge View [%s]", branch.toStringWithId());
      } catch (Exception ex) {
         // do nothing
      }
      return "Table Report - Merge View";
   }

   @Override
   public String getReportTitle() {
      return getEditorTitle();
   }

}
