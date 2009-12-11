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

package org.eclipse.osee.framework.ui.skynet.widgets.xmerge;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.IATSArtifact;
import org.eclipse.osee.framework.skynet.core.conflict.Conflict;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictManagerExternal;
import org.eclipse.osee.framework.skynet.core.revision.ConflictManagerInternal;
import org.eclipse.osee.framework.skynet.core.types.IArtifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.ListSelectionDialogNoSave;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xchange.ChangeView;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;

/**
 * @author Donald G. Dunne
 * @author Theron Virgin
 */
public class XMergeViewer extends XWidget implements IAdaptable {
   private static final String COMPLETE_COMMIT_ACTION_ID = "complete.commit.action.id";
   private static final String REFRESH_ACTION_ID = "refresh.action.id";
   private MergeXViewer mergeXViewer;
   private IDirtiableEditor editor;
   public final static String normalColor = "#EEEEEE";
   private static final String LOADING = "Loading ...";
   private static final String NO_CONFLICTS = "No conflicts were found";
   private static final String CONFLICTS_NOT_LOADED = "Cleared on shutdown.  Refresh to Reload.";
   private Label extraInfoLabel;
   private Conflict[] conflicts;
   private String displayLabelText;
   private Action openAssociatedArtifactAction;
   private Action completeCommitAction;
   private Branch sourceBranch;
   private Branch destBranch;
   private TransactionRecord commitTrans;
   private TransactionRecord tranId;
   private MergeView mergeView;
   private IToolBarManager toolBarManager;
   private final static String CONFLICTS_RESOLVED = "\nAll Conflicts Are Resolved";

   public XMergeViewer() {
      super("Merge Manager");
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      Composite mainComp = new Composite(parent, SWT.BORDER);
      Composite taskComp = new Composite(mainComp, SWT.NONE);
      taskComp.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
      taskComp.setLayout(ALayout.getZeroMarginLayout());
      createTextWidgets(parent);
      createMainComposite(mainComp);
      mergeXViewer = new MergeXViewer(mainComp, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION, this);
      createMergeXViewer();
      createTaskActionBar(taskComp);
      if (toolkit != null) {
         toolkit.adapt(mergeXViewer.getStatusLabel(), false, false);
      }
      Tree tree = mergeXViewer.getTree();
      createTree(tree);
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

   private void createMainComposite(Composite mainComp) {
      mainComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      mainComp.setLayout(ALayout.getZeroMarginLayout());
      if (toolkit != null) {
         toolkit.paintBordersFor(mainComp);
      }

   }

   private void createMergeXViewer() {
      mergeXViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
      XMergeLabelProvider labelProvider = new XMergeLabelProvider(mergeXViewer);
      mergeXViewer.addLabelProvider(labelProvider);
      mergeXViewer.setSorter(new MergeXViewerSorter(mergeXViewer, labelProvider));
      mergeXViewer.setContentProvider(new XMergeContentProvider(mergeXViewer));
      mergeXViewer.setLabelProvider(new XMergeLabelProvider(mergeXViewer));
      mergeXViewer.addSelectionChangedListener(new ISelectionChangedListener() {
         public void selectionChanged(SelectionChangedEvent event) {
            refreshActionEnablement();
         }
      });
   }

   private void createTree(Tree tree) {
      GridData gridData = new GridData(GridData.FILL_BOTH);
      gridData.heightHint = 100;
      tree.setLayout(ALayout.getZeroMarginLayout());
      tree.setLayoutData(gridData);
      tree.setHeaderVisible(true);
      tree.setLinesVisible(true);
   }

   public void createTaskActionBar(Composite parent) {
      Composite composite = new Composite(parent, SWT.NONE);
      GridLayout layout = ALayout.getZeroMarginLayout(2, false);
      layout.marginLeft = 5;
      composite.setLayout(layout);
      composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      extraInfoLabel = new Label(composite, SWT.NONE);
      extraInfoLabel.setAlignment(SWT.LEFT);
      extraInfoLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      extraInfoLabel.setText("\n");

      IToolBarManager manager = getToolBarManager();
      ((ToolBarManager) manager).createControl(composite);
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
      manager.add(OseeAts.createBugAction(SkynetGuiPlugin.getInstance(), this, MergeView.VIEW_ID, "Merge Manager"));
      manager.update(true);
   }

   private IToolBarManager getToolBarManager() {
      if (toolBarManager == null) {
         toolBarManager = new ToolBarManager(SWT.FLAT);
      }
      return toolBarManager;
   }

   private void applyPreviousMerge(final int destBranchId) {
      Job job = new Job("Apply Previous Merge") {

         @Override
         protected IStatus run(final IProgressMonitor monitor) {
            monitor.beginTask("ApplyingPreviousMerge", conflicts.length);
            for (Conflict conflict : conflicts) {
               try {
                  conflict.applyPreviousMerge(ConflictManagerInternal.getMergeBranchId(
                        conflict.getSourceBranch().getId(), destBranchId), destBranchId);
               } catch (OseeCoreException ex) {
                  OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
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

   public void refreshTable() throws InterruptedException {
      Job job = new Job("Loading Merge Manager") {
         @Override
         protected IStatus run(IProgressMonitor monitor) {
            try {
               if (!(conflicts.length == 0)) {
                  Conflict[] artifactChanges = new Conflict[0];
                  if (conflicts[0].getToTransactionId() != null) {
                     setConflicts(ConflictManagerInternal.getConflictsPerBranch(conflicts[0].getSourceBranch(),
                           conflicts[0].getDestBranch(), conflicts[0].getToTransactionId(), monitor).toArray(
                           artifactChanges));
                  } else {
                     setConflicts(ConflictManagerInternal.getConflictsPerBranch(conflicts[0].getCommitTransactionId(),
                           monitor).toArray(artifactChanges));
                  }
               }
            } catch (Exception ex) {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
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

   public void refreshActionEnablement() {

   }

   public void loadTable() {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            refresh();
         }
      });
   }

   @SuppressWarnings("unchecked")
   public ArrayList<Conflict> getSelectedConflicts() {
      ArrayList<Conflict> items = new ArrayList<Conflict>();
      if (mergeXViewer == null) {
         return items;
      }
      if (mergeXViewer.getSelection().isEmpty()) {
         return items;
      }
      Iterator i = ((IStructuredSelection) mergeXViewer.getSelection()).iterator();
      while (i.hasNext()) {
         Object obj = i.next();
         items.add((Conflict) obj);
      }
      return items;
   }

   @Override
   public Control getControl() {
      return mergeXViewer.getTree();
   }

   @Override
   public void dispose() {
      mergeXViewer.dispose();
   }

   @Override
   public void setFocus() {
      mergeXViewer.getTree().setFocus();
   }

   @Override
   public void refresh() {
      mergeXViewer.refresh();
      validate();
      refreshActionEnablement();
      mergeView.showConflicts(true);
      int resolved = 0;
      int informational = 0;
      if (conflicts != null && conflicts.length != 0) {
         for (Conflict conflict : conflicts) {
            if (conflict.statusResolved() || conflict.statusCommitted()) {
               resolved++;
            }
            if (conflict.statusInformational()) {
               informational++;
            }
         }
         if (resolved == conflicts.length) {
            extraInfoLabel.setText(displayLabelText + CONFLICTS_RESOLVED);
         } else {
            extraInfoLabel.setText(displayLabelText + "\nConflicts : " + (conflicts.length - informational) + " <=> Resolved : " + resolved + (informational == 0 ? " " : "\nInformational Conflicts : " + informational));
         }
      }
      checkForCompleteCommit();
   }

   private boolean areAllConflictsResolved() {
      int resolved = 0;
      for (Conflict conflict : conflicts) {
         if (conflict.statusResolved() || conflict.statusCommitted() || conflict.statusInformational()) {
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
   public void setXmlData(String str) {
   }

   @Override
   public String getXmlData() {
      return null;
   }

   @Override
   public String toHTML(String labelFont) {
      return AHTML.simplePage("Unhandled");
   }

   @Override
   public String getReportData() {
      return null;
   }

   /**
    * @return Returns the xViewer.
    */
   public MergeXViewer getXViewer() {
      return mergeXViewer;
   }

   @Override
   public Object getData() {
      return mergeXViewer.getInput();
   }

   public IDirtiableEditor getEditor() {
      return editor;
   }

   public void setEditor(IDirtiableEditor editor) {
      this.editor = editor;
   }

   public void setInputData(final Branch sourceBranch, final Branch destBranch, final TransactionRecord tranId, final MergeView mergeView, final TransactionRecord commitTrans, boolean showConflicts) {
      setInputData(sourceBranch, destBranch, tranId, mergeView, commitTrans, "", showConflicts);
   }

   public void setInputData(final Branch sourceBranch, final Branch destBranch, final TransactionRecord tranId, final MergeView mergeView, final TransactionRecord commitTrans, String loadingText, final boolean showConflicts) {
      this.sourceBranch = sourceBranch;
      this.destBranch = destBranch;
      this.tranId = tranId;
      this.mergeView = mergeView;
      this.commitTrans = commitTrans;
      extraInfoLabel.setText(LOADING + loadingText);
      Job job = new Job("Loading Merge Manager") {
         @Override
         protected IStatus run(IProgressMonitor monitor) {
            try {
               if (showConflicts) {
                  if (commitTrans == null) {
                     conflicts =
                           ConflictManagerInternal.getConflictsPerBranch(sourceBranch, destBranch, tranId, monitor).toArray(
                                 new Conflict[0]);
                  } else {
                     conflicts =
                           ConflictManagerInternal.getConflictsPerBranch(commitTrans, monitor).toArray(new Conflict[0]);
                  }
               }

               Displays.ensureInDisplayThread(new Runnable() {
                  public void run() {
                     if (showConflicts) {
                        if (conflicts.length == 0) {
                           extraInfoLabel.setText(NO_CONFLICTS);
                        } else {
                           setConflicts(conflicts);
                           mergeView.setConflicts(conflicts);
                           refresh();
                        }
                     } else {
                        extraInfoLabel.setText(CONFLICTS_NOT_LOADED);
                     }
                     checkForCompleteCommit();
                  }
               });
            } catch (Exception ex) {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
            return Status.OK_STATUS;
         }
      };
      Jobs.startJob(job);
      if (sourceBranch != null) {
         refreshAssociatedArtifactItem(sourceBranch);
      }
   }

   private void refreshAssociatedArtifactItem(Branch sourceBranch) {
      try {
         IArtifact branchAssociatedArtifact = (IArtifact) sourceBranch.getAssociatedArtifact();
         if (branchAssociatedArtifact != null) {
            openAssociatedArtifactAction.setImageDescriptor(ImageManager.getImageDescriptor(branchAssociatedArtifact));
            openAssociatedArtifactAction.setEnabled(true);
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
   }

   public void setConflicts(Conflict[] conflicts) throws IllegalStateException {
      this.conflicts = conflicts;
      loadTable();
      int resolved = 0;
      int informational = 0;
      for (Conflict conflict : conflicts) {
         if (conflict.statusResolved() || conflict.statusCommitted()) {
            resolved++;
         }
         if (conflict.statusInformational()) {
            informational++;
         }
      }
      mergeXViewer.setConflicts(conflicts);
      if (conflicts != null && conflicts.length != 0) {
         if (sourceBranch != null) {
            displayLabelText =
                  "Source Branch :  " + sourceBranch.getName() + "\nDestination Branch :  " + destBranch.getName();
         } else {
            displayLabelText = "Commit Transaction ID :  " + commitTrans + " " + commitTrans.getComment();
         }
         if (resolved == conflicts.length - informational) {
            extraInfoLabel.setText(displayLabelText + CONFLICTS_RESOLVED);
         } else {
            extraInfoLabel.setText(displayLabelText + "\nConflicts : " + (conflicts.length - informational) + " <=> Resolved : " + resolved + (informational == 0 ? " " : "\nInformational Conflicts : " + informational));
         }
      }

   }

   @SuppressWarnings("unchecked")
   @Override
   public Object getAdapter(Class adapter) {
      if (IActionable.class.equals(adapter)) {
         return new MergeViewerActionable();
      }
      return null;
   }

   private Action getCompleteCommitAction() {
      if (completeCommitAction == null) {
         completeCommitAction = new CompleteCommitAction();
      }
      return completeCommitAction;
   }

   private Branch getMergeBranch() {
      if (conflicts != null && conflicts.length != 0) {
         return conflicts[0].getMergeBranch();
      }
      return null;
   }

   private boolean hasMergeBranchBeenCommitted() {
      final Branch mergeBranch = getMergeBranch();
      return mergeBranch != null && !mergeBranch.isEditable();
   }

   private void checkForCompleteCommit() {
      boolean isVisible = false;
      if (conflicts != null && conflicts.length != 0) {
         isVisible = !hasMergeBranchBeenCommitted() && areAllConflictsResolved();
         isVisible &= sourceBranch != null && sourceBranch.getBranchState().isRebaselineInProgress();
      }
      setCompleteCommitItemVisible(isVisible);
   }

   private void setCompleteCommitItemVisible(boolean isVisible) {
      IToolBarManager manager = getToolBarManager();
      boolean wasFound = manager.find(COMPLETE_COMMIT_ACTION_ID) != null;
      if (isVisible) {
         if (!wasFound) {
            manager.insertBefore(REFRESH_ACTION_ID, getCompleteCommitAction());
         }
      } else if (wasFound) {
         manager.remove(COMPLETE_COMMIT_ACTION_ID);
      }
      manager.update(true);
   }

   private final class MergeViewerActionable implements IActionable {
      @Override
      public String getActionDescription() {
         StringBuilder sb = new StringBuilder();
         if (sourceBranch != null) {
            sb.append("\nSource Branch: " + sourceBranch);
         }
         if (destBranch != null) {
            sb.append("\nDestination Branch: " + destBranch);
         }
         if (tranId != null) {
            sb.append("\nTransactionId: " + tranId);
         }
         if (commitTrans != null) {
            sb.append("\nCommit TransactionId: " + commitTrans);
         }
         return sb.toString();
      }
   }

   private final class CompleteCommitAction extends Action {
      public CompleteCommitAction() {
         super();
         setImageDescriptor(FrameworkImage.BRANCH_COMMIT.createImageDescriptor());
         setToolTipText("Commit changes into destination branch");
         setId(COMPLETE_COMMIT_ACTION_ID);
      }

      @Override
      public void run() {
         if (sourceBranch.getBranchState().isRebaselineInProgress()) {
            ConflictManagerExternal conflictManager = new ConflictManagerExternal(destBranch, sourceBranch);
            BranchManager.completeUpdateBranch(conflictManager, true, false);
         }
      }
   }

   private final class OpenAssociatedArtifactAction extends Action {

      public OpenAssociatedArtifactAction() {
         super();
         setToolTipText("Open Associated Artifact");
         setEnabled(false);
      }

      @Override
      public void run() {
         try {
            Branch sourceBranch = conflicts[0].getSourceBranch();
            Artifact branchAssociatedArtifact = (Artifact) sourceBranch.getAssociatedArtifact().getFullArtifact();
            if (branchAssociatedArtifact instanceof IATSArtifact) {
               OseeAts.openATSArtifact(branchAssociatedArtifact);
               return;
            } else if (!branchAssociatedArtifact.equals(UserManager.getUser(SystemUser.OseeSystem))) {
               ArtifactEditor.editArtifact(branchAssociatedArtifact);
               return;
            }
            AWorkbench.popup("ERROR", "Unknown branch association");
         } catch (Exception ex) {
            OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
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
         if (conflicts.length != 0) {
            if (conflicts[0].getSourceBranch() != null) {
               try {
                  ChangeView.open(conflicts[0].getSourceBranch());
               } catch (Exception ex) {
                  OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
               }
            } else {
               try {
                  ChangeView.open(conflicts[0].getCommitTransactionId());
               } catch (Exception ex) {
                  OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         }
      }
   }

   private final class ShowDestinationBranchChangeReportAction extends Action {

      public ShowDestinationBranchChangeReportAction() {
         super();
         // setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("branch_change_dest.gif"));
         setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.BRANCH_CHANGE_DEST));
         setToolTipText("Show Destination Branch Change Report");
      }

      @Override
      public void run() {
         if (conflicts.length != 0) {
            try {
               ChangeView.open(conflicts[0].getDestBranch());
            } catch (Exception ex) {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      }
   }

   private final class RefreshAction extends Action {

      public RefreshAction() {
         super();
         setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.REFRESH));
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
         if (conflicts.length != 0) {
            if (conflicts[0].getSourceBranch() != null) {
               ArrayList<String> selections = new ArrayList<String>();
               ArrayList<Integer> branchIds = new ArrayList<Integer>();
               try {
                  Collection<Integer> destBranches =
                        ConflictManagerInternal.getDestinationBranchesMerged(sourceBranch.getId());
                  for (Integer integer : destBranches) {
                     if (integer.intValue() != destBranch.getId()) {
                        selections.add(BranchManager.getBranch(integer).getName());
                        branchIds.add(integer);
                     }
                  }
                  if (selections.size() > 0) {
                     ListSelectionDialogNoSave dialog =
                           new ListSelectionDialogNoSave(selections.toArray(),
                                 Display.getCurrent().getActiveShell().getShell(), "Apply Prior Merge Resolution",
                                 null, "Select the destination branch that the previous commit was appplied to", 2,
                                 new String[] {"Apply", "Cancel"}, 1);
                     if (dialog.open() == 0) {
                        System.out.print("Applying the merge found for Branch " + branchIds.toArray()[dialog.getSelection()]);
                        applyPreviousMerge(branchIds.get(dialog.getSelection()));
                     }
                  }
                  if (selections.size() == 0) {
                     new MessageDialog(Display.getCurrent().getActiveShell().getShell(),
                           "Apply Prior Merge Resolution", null, "This Source Branch has had No Prior Merges", 2,
                           new String[] {"OK"}, 1).open();
                  }
               } catch (OseeCoreException ex) {
                  OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         }
      }
   }
}
