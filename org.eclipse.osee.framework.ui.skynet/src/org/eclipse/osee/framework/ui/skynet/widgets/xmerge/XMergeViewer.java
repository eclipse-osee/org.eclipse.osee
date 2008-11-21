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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.IATSArtifact;
import org.eclipse.osee.framework.skynet.core.conflict.Conflict;
import org.eclipse.osee.framework.skynet.core.revision.ConflictManagerInternal;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.plugin.util.ListSelectionDialogNoSave;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.status.SwtStatusMonitor;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xchange.ChangeView;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;

/**
 * @author Donald G. Dunne
 * @author Theron Virgin
 */
public class XMergeViewer extends XWidget implements IActionable {

   private MergeXViewer mergeXViewer;
   private IDirtiableEditor editor;
   public final static String normalColor = "#EEEEEE";
   private static final String LOADING = "Loading ...";
   private static final String NO_CONFLICTS = "No conflicts were found";
   private static final String CONFLICTS_NOT_LOADED = "Cleared on shutdown.  Refresh to Reload.";
   private Label extraInfoLabel;
   private Conflict[] conflicts;
   private String displayLabelText;
   private ToolItem openAssociatedArtifactItem;
   private Branch sourceBranch;
   private Branch destBranch;
   private TransactionId commitTrans;
   private TransactionId tranId;
   private MergeView mergeView;
   private final static String CONFLICTS_RESOLVED = "\nAll Conflicts Are Resolved";

   /**
    * @param label
    */
   public XMergeViewer() {
      super("Merge Manager");
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.skynet.gui.widgets.XWidget#createWidgets(org.eclipse.swt.widgets.Composite, int)
    */
   @Override
   public void createWidgets(Composite parent, int horizontalSpan) {

      // Create Text Widgets
      if (displayLabel && !label.equals("")) {
         labelWidget = new Label(parent, SWT.NONE);
         labelWidget.setText(label + ":");
         if (toolTip != null) {
            labelWidget.setToolTipText(toolTip);
         }
      }

      Composite mainComp = new Composite(parent, SWT.BORDER);
      mainComp.setLayoutData(new GridData(GridData.FILL_BOTH));
      mainComp.setLayout(ALayout.getZeroMarginLayout());
      if (toolkit != null) toolkit.paintBordersFor(mainComp);

      createTaskActionBar(mainComp);

      mergeXViewer = new MergeXViewer(mainComp, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION, this);
      mergeXViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
      XMergeLabelProvider labelProvider = new XMergeLabelProvider(mergeXViewer);
      mergeXViewer.addLabelProvider(labelProvider);
      mergeXViewer.setSorter(new MergeXViewerSorter(mergeXViewer, labelProvider));
      mergeXViewer.setContentProvider(new XMergeContentProvider(mergeXViewer));
      mergeXViewer.setLabelProvider(new XMergeLabelProvider(mergeXViewer));
      mergeXViewer.addSelectionChangedListener(new ISelectionChangedListener() {
         /*
          * (non-Javadoc)
          * 
          * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
          */
         public void selectionChanged(SelectionChangedEvent event) {
            refreshActionEnablement();
         }
      });

      if (toolkit != null) toolkit.adapt(mergeXViewer.getStatusLabel(), false, false);

      Tree tree = mergeXViewer.getTree();
      GridData gridData = new GridData(GridData.FILL_BOTH);
      gridData.heightHint = 100;
      tree.setLayout(ALayout.getZeroMarginLayout());
      tree.setLayoutData(gridData);
      tree.setHeaderVisible(true);
      tree.setLinesVisible(true);

   }

   private void refreshAssociatedArtifactItem(Branch sourceBranch) {
      try {
         Artifact branchAssociatedArtifact = sourceBranch.getAssociatedArtifact();
         if (branchAssociatedArtifact != null) {
            openAssociatedArtifactItem.setToolTipText("Open Associated Artifact");
            openAssociatedArtifactItem.setEnabled(true);
            openAssociatedArtifactItem.setImage(branchAssociatedArtifact.getImage());
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
   }

   public void createTaskActionBar(Composite parent) {

      Composite bComp = new Composite(parent, SWT.NONE);
      bComp.setLayout(new GridLayout(2, false));
      bComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      Composite leftComp = new Composite(bComp, SWT.NONE);
      leftComp.setLayout(new GridLayout());
      leftComp.setLayoutData(new GridData(GridData.BEGINNING | GridData.FILL_HORIZONTAL));

      extraInfoLabel = new Label(leftComp, SWT.NONE);
      extraInfoLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      extraInfoLabel.setText("\n");

      Composite rightComp = new Composite(bComp, SWT.NONE);
      rightComp.setLayout(new GridLayout());
      rightComp.setLayoutData(new GridData(GridData.END));

      ToolBar toolBar = new ToolBar(rightComp, SWT.FLAT | SWT.RIGHT);
      GridData gd = new GridData(GridData.FILL_HORIZONTAL);
      toolBar.setLayoutData(gd);
      ToolItem item = null;

      openAssociatedArtifactItem = new ToolItem(toolBar, SWT.PUSH);
      openAssociatedArtifactItem.setEnabled(false);
      openAssociatedArtifactItem.setDisabledImage(null);
      openAssociatedArtifactItem.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            try {
               Branch sourceBranch = conflicts[0].getSourceBranch();
               Artifact branchAssociatedArtifact = sourceBranch.getAssociatedArtifact();
               if (branchAssociatedArtifact instanceof IATSArtifact) {
                  OseeAts.openATSArtifact(branchAssociatedArtifact);
                  return;
               } else if (!branchAssociatedArtifact.equals(UserManager.getUser(SystemUser.NoOne))) {
                  ArtifactEditor.editArtifact(branchAssociatedArtifact);
                  return;
               }
               AWorkbench.popup("ERROR", "Unknown branch association");
            } catch (Exception ex) {
               OSEELog.logException(SkynetGuiPlugin.class, ex, true);
            }
         }
      });

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(SkynetGuiPlugin.getInstance().getImage("branch_change_source.gif"));
      item.setToolTipText("Show Source Branch Change Report");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            if (conflicts.length != 0) {
               if (conflicts[0].getSourceBranch() != null) {

                  try {
                     ChangeView.open(conflicts[0].getSourceBranch());
                  } catch (Exception ex) {
                     OSEELog.logException(XMergeViewer.class, ex, true);
                  }
               } else {
                  try {
                     ChangeView.open(conflicts[0].getCommitTransactionId());
                  } catch (Exception ex) {
                     OSEELog.logException(XMergeViewer.class, ex, true);
                  }
               }
            }
         }
      });

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(SkynetGuiPlugin.getInstance().getImage("branch_change_dest.gif"));
      item.setToolTipText("Show Destination Branch Change Report");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            if (conflicts.length != 0) {
               try {
                  ChangeView.open(conflicts[0].getDestBranch());
               } catch (Exception ex) {
                  OSEELog.logException(XMergeViewer.class, ex, true);
               }
            }
         }
      });

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(SkynetGuiPlugin.getInstance().getImage("refresh.gif"));
      item.setToolTipText("Refresh");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            setInputData(sourceBranch, destBranch, tranId, mergeView, commitTrans, true);
         }
      });

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(SkynetGuiPlugin.getInstance().getImage("customize.gif"));
      item.setToolTipText("Customize Table");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            mergeXViewer.getCustomizeMgr().handleTableCustomization();
         }
      });

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(SkynetGuiPlugin.getInstance().getImage("branch_merge.gif"));
      item.setToolTipText("Apply Merge Results From Prior Merge");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            if (conflicts.length != 0) {
               if (conflicts[0].getSourceBranch() != null) {
                  //(Object[] choose, Shell parentShell, String dialogTitle, Image dialogTitleImage, 
                  //String dialogMessage, int dialogImageType, String[] dialogButtonLabels, int defaultIndex) 
                  ArrayList<String> selections = new ArrayList<String>();
                  ArrayList<Integer> branchIds = new ArrayList<Integer>();
                  try {
                     Collection<Integer> destBranches =
                           ConflictManagerInternal.getDestinationBranchesMerged(sourceBranch.getBranchId());
                     for (Integer integer : destBranches) {
                        if (integer.intValue() != destBranch.getBranchId()) {
                           selections.add(BranchManager.getBranch(integer).getBranchName());
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
                           for (Conflict conflict : conflicts) {
                              conflict.applyPreviousMerge(ConflictManagerInternal.getMergeBranchId(
                                    conflicts[0].getSourceBranch().getBranchId(), branchIds.get(dialog.getSelection())));
                           }
                           setInputData(sourceBranch, destBranch, tranId, mergeView, commitTrans,
                                 " Aplying Previous Merge", true);
                        }
                     }
                     if (selections.size() == 0) {
                        new MessageDialog(Display.getCurrent().getActiveShell().getShell(),
                              "Apply Prior Merge Resolution", null, "This Source Branch has had No Prior Merges", 2,
                              new String[] {"OK"}, 1).open();
                     }
                  } catch (OseeCoreException ex) {
                     OSEELog.logException(XMergeViewer.class, ex, false);
                  }
               }
            }
         }
      });

      OseeAts.addButtonToEditorToolBar(this, SkynetGuiPlugin.getInstance(), toolBar, MergeView.VIEW_ID, "Merge Manager");
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
                           conflicts[0].getDestBranch(), conflicts[0].getToTransactionId(),
                           new SwtStatusMonitor(monitor)).toArray(artifactChanges));
                  } else {
                     setConflicts(org.eclipse.osee.framework.skynet.core.revision.ConflictManagerInternal.getConflictsPerBranch(
                           conflicts[0].getCommitTransactionId(), new SwtStatusMonitor(monitor)).toArray(
                           artifactChanges));
                  }
               }
            } catch (Exception ex) {
               OSEELog.logException(XMergeViewer.class, ex, true);
            }

            return Status.OK_STATUS;
         }
      };
      Jobs.startJob(job).join();
      loadTable();
   }

   public void refreshActionEnablement() {

   }

   public void loadTable() {
      refresh();
   }

   @SuppressWarnings("unchecked")
   public ArrayList<Conflict> getSelectedConflicts() {
      ArrayList<Conflict> items = new ArrayList<Conflict>();
      if (mergeXViewer == null) return items;
      if (mergeXViewer.getSelection().isEmpty()) return items;
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
      setLabelError();
      refreshActionEnablement();
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
            extraInfoLabel.setText(displayLabelText + "\nConflicts : " + (conflicts.length - informational) + " <=> Resolved : " + resolved + (informational == 0 ? " " : ("\nInformational Conflicts : " + informational)));

         }
      }
   }

   @Override
   public Result isValid() {
      return Result.TrueResult;
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

   /*
    * (non-Javadoc)
    * 
    * @see osee.skynet.gui.widgets.XWidget#getData()
    */
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

   @Override
   public boolean isEditable() {
      return editable;
   }

   @Override
   public void setEditable(boolean editable) {
      this.editable = editable;
   }

   public void setInputData(final Branch sourceBranch, final Branch destBranch, final TransactionId tranId, final MergeView mergeView, final TransactionId commitTrans, boolean showConflicts) {
      setInputData(sourceBranch, destBranch, tranId, mergeView, commitTrans, "", showConflicts);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.IDamWidget#setArtifact(org.eclipse.osee.framework.skynet.core.artifact.Artifact, java.lang.String)
    */
   public void setInputData(final Branch sourceBranch, final Branch destBranch, final TransactionId tranId, final MergeView mergeView, final TransactionId commitTrans, String loadingText, final boolean showConflicts) {
      this.sourceBranch = sourceBranch;
      this.destBranch = destBranch;
      this.tranId = tranId;
      this.mergeView = mergeView;
      this.commitTrans = commitTrans;
      extraInfoLabel.setText(LOADING + loadingText);
      Job job = new Job("Loading Merge Manager") {
         @Override
         protected IStatus run(IProgressMonitor monitor) {
            SwtStatusMonitor swtMonitor = new SwtStatusMonitor(monitor);
            try {
               if (showConflicts) {
                  if (commitTrans == null) {
                     conflicts =
                           ConflictManagerInternal.getConflictsPerBranch(sourceBranch, destBranch, tranId, swtMonitor).toArray(
                                 new Conflict[0]);
                  } else {
                     conflicts =
                           ConflictManagerInternal.getConflictsPerBranch(commitTrans, swtMonitor).toArray(
                                 new Conflict[0]);
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
                  }
               });
            } catch (Exception ex) {
               OSEELog.logException(SkynetGuiPlugin.class, ex, true);
            }
            return Status.OK_STATUS;
         }
      };
      Jobs.startJob(job);
      if (sourceBranch != null) {
         refreshAssociatedArtifactItem(sourceBranch);
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
                  "Source Branch :  " + sourceBranch.getBranchName() + "\nDestination Branch :  " + destBranch.getBranchName();
         } else {
            displayLabelText = "Commit Transaction ID :  " + commitTrans + " " + commitTrans.getComment();
         }
         if (resolved == (conflicts.length - informational)) {
            extraInfoLabel.setText(displayLabelText + CONFLICTS_RESOLVED);
         } else {
            extraInfoLabel.setText(displayLabelText + "\nConflicts : " + (conflicts.length - informational) + " <=> Resolved : " + resolved + (informational == 0 ? " " : ("\nInformational Conflicts : " + informational)));
         }
      }

   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.ats.IActionable#getActionDescription()
    */
   @Override
   public String getActionDescription() {
      StringBuffer sb = new StringBuffer();
      if (sourceBranch != null) sb.append("\nSource Branch: " + sourceBranch);
      if (destBranch != null) sb.append("\nDestination Branch: " + destBranch);
      if (tranId != null) sb.append("\nTransactionId: " + tranId);
      if (commitTrans != null) sb.append("\nCommit TransactionId: " + commitTrans);
      return sb.toString();
   }
}
