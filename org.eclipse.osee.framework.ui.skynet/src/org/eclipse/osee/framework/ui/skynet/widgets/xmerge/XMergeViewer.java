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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.IATSArtifact;
import org.eclipse.osee.framework.skynet.core.conflict.Conflict;
import org.eclipse.osee.framework.skynet.core.event.LocalTransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.RemoteTransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.skynet.core.event.TransactionEvent;
import org.eclipse.osee.framework.skynet.core.revision.RevisionManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.user.UserEnum;
import org.eclipse.osee.framework.ui.plugin.event.Event;
import org.eclipse.osee.framework.ui.plugin.event.IEventReceiver;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;

/**
 * @author Donald G. Dunne
 * @author Theron Virgin
 */
public class XMergeViewer extends XWidget implements IEventReceiver, IActionable {

   private MergeXViewer xCommitViewer;
   private IDirtiableEditor editor;
   public final static String normalColor = "#EEEEEE";
   private static final String LOADING = "Loading ...";
   private static final String NO_CONFLICTS = "No conflicts were found";
   private Label extraInfoLabel;
   private Conflict[] conflicts;
   private String displayLabelText;
   private ToolItem openAssociatedArtifactItem;
   private Branch sourceBranch;
   private Branch destBranch;
   private TransactionId tranId;
   private MergeView mergeView;
   private final static String CONFLICTS_RESOLVED = "\nAll Conflicts Are Resolved";

   /**
    * @param label
    */
   public XMergeViewer() {
      super("Merge Manager");
      SkynetEventManager.getInstance().register(RemoteTransactionEvent.class, this);
      SkynetEventManager.getInstance().register(LocalTransactionEvent.class, this);
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

      xCommitViewer = new MergeXViewer(mainComp, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION, this);
      xCommitViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
      XMergeLabelProvider labelProvider = new XMergeLabelProvider(xCommitViewer);
      xCommitViewer.addLabelProvider(labelProvider);
      xCommitViewer.setSorter(new MergeXViewerSorter(xCommitViewer, labelProvider));
      xCommitViewer.setContentProvider(new XMergeContentProvider(xCommitViewer));
      xCommitViewer.setLabelProvider(new XMergeLabelProvider(xCommitViewer));
      xCommitViewer.addSelectionChangedListener(new ISelectionChangedListener() {
         /*
          * (non-Javadoc)
          * 
          * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
          */
         public void selectionChanged(SelectionChangedEvent event) {
            refreshActionEnablement();
         }
      });

      if (toolkit != null) toolkit.adapt(xCommitViewer.getStatusLabel(), false, false);

      Tree tree = xCommitViewer.getTree();
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
            openAssociatedArtifactItem.setEnabled(true);
            openAssociatedArtifactItem.setImage(branchAssociatedArtifact.getImage());
         }
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, false);
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
      openAssociatedArtifactItem.setToolTipText("Open Associated Artifact");
      openAssociatedArtifactItem.setEnabled(false);
      openAssociatedArtifactItem.setImage(SkynetGuiPlugin.getInstance().getImage("laser_16_16.gif"));
      openAssociatedArtifactItem.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            try {
               Branch sourceBranch = conflicts[0].getSourceBranch();
               Artifact branchAssociatedArtifact = sourceBranch.getAssociatedArtifact();
               if (branchAssociatedArtifact instanceof IATSArtifact) {
                  OseeAts.openATSArtifact(branchAssociatedArtifact);
                  return;
               } else if (!branchAssociatedArtifact.equals(SkynetAuthentication.getUser(UserEnum.NoOne))) {
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
         public void widgetSelected(SelectionEvent e) {
            if (conflicts.length != 0) {
               try {
                  ChangeView.open(conflicts[0].getSourceBranch());
               } catch (Exception ex) {
                  OSEELog.logException(XMergeViewer.class, ex, true);
               }
            }
         }
      });

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(SkynetGuiPlugin.getInstance().getImage("branch_change_dest.gif"));
      item.setToolTipText("Show Source Branch Change Report");
      item.setToolTipText("Show Destination Branch Change Report");
      item.addSelectionListener(new SelectionAdapter() {
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
         public void widgetSelected(SelectionEvent e) {
            setInputData(sourceBranch, destBranch, tranId, mergeView);
         }
      });

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(SkynetGuiPlugin.getInstance().getImage("customize.gif"));
      item.setToolTipText("Customize Table");
      item.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            xCommitViewer.getCustomize().handleTableCustomization();
         }
      });

      OseeAts.addButtonToEditorToolBar(this, SkynetGuiPlugin.getInstance(), toolBar, MergeView.VIEW_ID, "Merge Manager");
   }

   public void refreshTable() {
      try {
         if (!(conflicts.length == 0)) {
            Conflict[] artifactChanges = new Conflict[0];
            setConflicts(RevisionManager.getInstance().getConflictsPerBranch(conflicts[0].getSourceBranch(),
                  conflicts[0].getDestBranch(), conflicts[0].getToTransactionId()).toArray(artifactChanges));
         }
      } catch (Exception ex) {
         OSEELog.logException(XMergeViewer.class, ex, true);
      }
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
      if (xCommitViewer == null) return items;
      if (xCommitViewer.getSelection().isEmpty()) return items;
      Iterator i = ((IStructuredSelection) xCommitViewer.getSelection()).iterator();
      while (i.hasNext()) {
         Object obj = i.next();
         items.add((Conflict) obj);
      }
      return items;
   }

   @Override
   public Control getControl() {
      return xCommitViewer.getTree();
   }

   @Override
   public void dispose() {
      xCommitViewer.dispose();
   }

   @Override
   public void setFocus() {
      xCommitViewer.getTree().setFocus();
   }

   public void refresh() {
      xCommitViewer.refresh();
      setLabelError();
      refreshActionEnablement();
      int resolved = 0;
      int informational = 0;
      if (conflicts != null && conflicts.length != 0) {
         for (Conflict conflict : conflicts) {
            if (conflict.statusResolved()) {
               resolved++;
            }
            if (conflict.statusInformational()) {
               informational++;
            }
         }
         if (resolved == conflicts.length) {
            extraInfoLabel.setText(displayLabelText + CONFLICTS_RESOLVED);
         } else {
            extraInfoLabel.setText(displayLabelText + "\nConflicts : " + (conflicts.length - informational) + " <=> Resovled : " + resolved + (informational == 0 ? " " : ("\nInformational Conflicts : " + informational)));

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
      return xCommitViewer;
   }

   public void onEvent(final Event event) {
      if (xCommitViewer == null || xCommitViewer.getTree() == null || xCommitViewer.getTree().isDisposed()) return;

      if (event instanceof TransactionEvent) {
         refresh();
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.jdk.core.event.IEventReceiver#runOnEventInDisplayThread()
    */
   public boolean runOnEventInDisplayThread() {
      return true;
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.skynet.gui.widgets.XWidget#getData()
    */
   @Override
   public Object getData() {
      return xCommitViewer.getInput();
   }

   public IDirtiableEditor getEditor() {
      return editor;
   }

   public void setEditor(IDirtiableEditor editor) {
      this.editor = editor;
   }

   public boolean isEditable() {
      return editable;
   }

   public void setEditable(boolean editable) {
      this.editable = editable;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.IDamWidget#setArtifact(org.eclipse.osee.framework.skynet.core.artifact.Artifact, java.lang.String)
    */
   public void setInputData(final Branch sourceBranch, final Branch destBranch, final TransactionId tranId, final MergeView mergeView) {
      this.sourceBranch = sourceBranch;
      this.destBranch = destBranch;
      this.tranId = tranId;
      this.mergeView = mergeView;
      extraInfoLabel.setText(LOADING);
      Job job = new Job("Loading Merge Manager") {
         @Override
         protected IStatus run(IProgressMonitor monitor) {
            try {
               conflicts =
                     RevisionManager.getInstance().getConflictsPerBranch(sourceBranch, destBranch, tranId).toArray(
                           new Conflict[0]);

               Displays.ensureInDisplayThread(new Runnable() {
                  public void run() {
                     try {
                        if (conflicts.length == 0) {
                           extraInfoLabel.setText(NO_CONFLICTS);
                        } else {
                           setConflicts(conflicts);
                           mergeView.setConflicts(conflicts);
                           loadTable();
                        }
                     } catch (SQLException ex) {
                        OSEELog.logException(SkynetGuiPlugin.class, ex.getLocalizedMessage(), ex, false);
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
      refreshAssociatedArtifactItem(sourceBranch);
   }

   public void setConflicts(Conflict[] conflicts) throws IllegalStateException, SQLException {
      this.conflicts = conflicts;
      loadTable();
      int resolved = 0;
      int informational = 0;
      for (Conflict conflict : conflicts) {
         if (conflict.statusResolved()) {
            resolved++;
         }
         if (conflict.statusInformational()) {
            informational++;
         }
      }
      xCommitViewer.setConflicts(conflicts);
      if (conflicts != null && conflicts.length != 0) {
         displayLabelText =
               "Source Branch :  " + conflicts[0].getSourceBranch().getBranchName() + "\nDestination Branch :  " + conflicts[0].getDestBranch().getBranchName();
         if (resolved == (conflicts.length - informational)) {
            extraInfoLabel.setText(displayLabelText + CONFLICTS_RESOLVED);
         } else {
            extraInfoLabel.setText(displayLabelText + "\nConflicts : " + (conflicts.length - informational) + " <=> Resovled : " + resolved + "\nInformational Conflicts : " + informational);
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
      return sb.toString();
   }
}
