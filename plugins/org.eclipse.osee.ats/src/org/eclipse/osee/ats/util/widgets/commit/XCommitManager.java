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

package org.eclipse.osee.ats.util.widgets.commit;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.branch.AtsBranchManager;
import org.eclipse.osee.ats.editor.WorkflowEditor;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.skynet.widgets.GenericXWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.FontManager;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;

/**
 * @author Donald G. Dunne
 */
public class XCommitManager extends GenericXWidget implements IArtifactWidget, IBranchEventListener {

   private CommitXManager xCommitManager;
   private IDirtiableEditor editor;
   public final static String normalColor = "#EEEEEE";
   private TeamWorkFlowArtifact teamArt;
   private static final int paddedTableHeightHint = 2;
   private Label extraInfoLabel;
   public static final String WIDGET_NAME = "XCommitManager";
   public static final String NAME = "Commit Manager";
   public static final String DESCRIPTION = "Commit branches to parent and parallel branches.";
   private int lastDefectListSize = 0;
   private Composite mainComp;
   private Composite parentComp;

   public XCommitManager() {
      super(NAME);
      OseeEventManager.addListener(this);
   }

   @Override
   public TeamWorkFlowArtifact getArtifact() {
      return teamArt;
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      // parentComp needs to be created and remain intact; mainComp will be disposed and re-created as necessary
      parentComp = new Composite(parent, SWT.FLAT);
      parentComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      parentComp.setLayout(ALayout.getZeroMarginLayout());

      redrawComposite();
   }

   private void redrawComposite() {
      if (parentComp == null || !Widgets.isAccessible(parentComp)) {
         return;
      }
      if (mainComp != null && Widgets.isAccessible(mainComp)) {
         mainComp.dispose();
         xCommitManager = null;
      }
      mainComp = new Composite(parentComp, SWT.FLAT);
      mainComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      mainComp.setLayout(new GridLayout(1, true));
      if (toolkit != null) {
         toolkit.paintBordersFor(mainComp);
      }

      labelWidget = new Label(mainComp, SWT.NONE);
      labelWidget.setText(getLabel() + ":");
      if (getToolTip() != null) {
         labelWidget.setToolTipText(getToolTip());
      }

      try {
         if (AtsClientService.get().getBranchService().isWorkingBranchCreationInProgress(teamArt)) {
            labelWidget.setText(getLabel() + ": Branch Creation in Progress");
         } else if (!AtsClientService.get().getBranchService().isWorkingBranchInWork(
            teamArt) && !AtsClientService.get().getBranchService().isCommittedBranchExists(teamArt)) {
            labelWidget.setText(getLabel() + ": No Working Branch or Committed changes available.");
         } else {

            Composite tableComp = new Composite(mainComp, SWT.BORDER);
            tableComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            tableComp.setLayout(ALayout.getZeroMarginLayout());
            if (toolkit != null) {
               toolkit.paintBordersFor(tableComp);
            }

            createTaskActionBar(tableComp);

            labelWidget.setText(getLabel() + ": ");// If ATS Admin, allow right-click to auto-complete reviews
            if (AtsClientService.get().getUserService().isAtsAdmin() && !AtsClientService.get().getStoreService().isProductionDb()) {
               labelWidget.addListener(SWT.MouseUp, new Listener() {
                  @Override
                  public void handleEvent(Event event) {
                     if (event.button == 3) {
                        if (!MessageDialog.openConfirm(Displays.getActiveShell(), "Auto Commit Branches",
                           "ATS Admin\n\nAuto Commit Branches?")) {
                           return;
                        }
                        try {
                           Collection<BranchId> branches =
                              AtsClientService.get().getBranchService().getBranchesLeftToCommit(teamArt);
                           for (Iterator<BranchId> it = branches.iterator(); it.hasNext();) {
                              BranchId destinationBranch = it.next();
                              IOperation operation = AtsBranchManager.commitWorkingBranch(teamArt, false, true,
                                 destinationBranch, !it.hasNext());
                              Operations.executeWorkAndCheckStatus(operation);
                           }
                        } catch (Exception ex) {
                           OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
                        }
                     }
                  }
               });
            }

            xCommitManager = new CommitXManager(tableComp, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION, this, teamArt,
               AtsClientService.get());
            xCommitManager.getTree().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

            xCommitManager.setContentProvider(new XCommitContentProvider());
            xCommitManager.setLabelProvider(new XCommitLabelProvider(xCommitManager, teamArt));

            if (toolkit != null && xCommitManager.getStatusLabel() != null) {
               toolkit.adapt(xCommitManager.getStatusLabel(), false, false);
            }

            setXviewerTree();
            loadTable();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      // reset bold for label
      WorkflowEditor.setLabelFonts(labelWidget, FontManager.getDefaultLabelFont());

      parentComp.layout();
   }

   public void setXviewerTree() {
      Tree tree = xCommitManager.getTree();
      int defectListSize = xCommitManager.getTree().getItemCount();
      if (defectListSize == lastDefectListSize) {
         return;
      }
      lastDefectListSize = defectListSize;
      int treeItemHeight = xCommitManager.getTree().getItemHeight();
      GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
      gridData.heightHint = treeItemHeight * (paddedTableHeightHint + defectListSize);
      tree.setLayout(ALayout.getZeroMarginLayout());
      tree.setLayoutData(gridData);
      tree.setHeaderVisible(true);
      tree.setLinesVisible(true);
   }

   public void createTaskActionBar(Composite parent) {

      // Button composite for state transitions, etc
      Composite bComp = new Composite(parent, SWT.NONE);
      // bComp.setBackground(mainSComp.getDisplay().getSystemColor(SWT.COLOR_CYAN));
      bComp.setLayout(new GridLayout(2, false));
      bComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      Composite leftComp = new Composite(bComp, SWT.NONE);
      leftComp.setLayout(new GridLayout());
      leftComp.setLayoutData(new GridData(GridData.BEGINNING | GridData.FILL_HORIZONTAL));

      extraInfoLabel = new Label(leftComp, SWT.NONE);
      extraInfoLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      extraInfoLabel.setText("");
      extraInfoLabel.setForeground(Displays.getSystemColor(SWT.COLOR_RED));

      Composite rightComp = new Composite(bComp, SWT.NONE);
      rightComp.setLayout(new GridLayout());
      rightComp.setLayoutData(new GridData(GridData.END));

      ToolBar toolBar = new ToolBar(rightComp, SWT.FLAT | SWT.RIGHT);
      GridData gd = new GridData(GridData.FILL_HORIZONTAL);
      toolBar.setLayoutData(gd);
      ToolItem item = null;

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(ImageManager.getImage(PluginUiImage.REFRESH));
      item.setToolTipText("Refresh");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            loadTable();
         }
      });

   }

   public void loadTable() {
      try {
         if (xCommitManager != null && teamArt != null && xCommitManager.getContentProvider() != null) {
            Collection<Object> commitMgrInputObjs =
               AtsClientService.get().getBranchService().getCommitTransactionsAndConfigItemsForTeamWf(teamArt);
            xCommitManager.setInput(commitMgrInputObjs);
            xCommitManager.refresh();
            refresh();
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public Control getControl() {
      if (xCommitManager == null) {
         return null;
      }
      return xCommitManager.getTree();
   }

   @Override
   public void dispose() {
      if (xCommitManager != null) {
         xCommitManager.dispose();
      }
      OseeEventManager.removeListener(this);
   }

   @Override
   public void refresh() {
      if (xCommitManager == null || xCommitManager.getTree() == null || xCommitManager.getTree().isDisposed()) {
         return;
      }
      setXviewerTree();
   }

   private void updateExtraInfoLabel(final int color, final String infoStr) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            if (Widgets.isAccessible(extraInfoLabel)) {
               String currentString = extraInfoLabel.getText();
               if (infoStr == null && currentString != null || //
               infoStr != null && currentString == null || //
               infoStr != null && currentString != null && !infoStr.equals(currentString)) {
                  extraInfoLabel.setText("Double-click item to perform Action; Right-click for mor options.");
               }
               extraInfoLabel.setForeground(Displays.getSystemColor(color));
            }
         }
      });
   }

   @Override
   public IStatus isValid() {
      Status returnStatus = new Status(IStatus.OK, getClass().getSimpleName(), "");
      try {
         int backgroundColor = SWT.COLOR_BLACK;
         String infoStr = "Double-click item to perform Action";
         if (xCommitManager != null && xCommitManager.getXCommitViewer() != null && xCommitManager.getXCommitViewer().getTeamArt() != null && xCommitManager.getXCommitViewer().getTeamArt() != null) {
            boolean workingBranchInWork = AtsClientService.get().getBranchService().isWorkingBranchInWork(teamArt);
            if (workingBranchInWork && !AtsClientService.get().getBranchService().isAllObjectsToCommitToConfigured(
               xCommitManager.getXCommitViewer().getTeamArt())) {
               infoStr = "All branches must be configured - Double-click item to perform Action";
               backgroundColor = SWT.COLOR_RED;
               returnStatus = new Status(IStatus.ERROR, getClass().getSimpleName(),
                  "All branches must be configured and committed.");
            }
         }
         updateExtraInfoLabel(backgroundColor, infoStr);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         return new Status(IStatus.ERROR, getClass().getSimpleName(), ex.getLocalizedMessage());
      }
      return returnStatus;
   }

   public CommitXManager getXViewer() {
      return xCommitManager;
   }

   @Override
   public Object getData() {
      return xCommitManager.getInput();
   }

   public IDirtiableEditor getEditor() {
      return editor;
   }

   @Override
   public void setArtifact(Artifact artifact) {
      if (!artifact.isOfType(AtsArtifactTypes.TeamWorkflow)) {
         throw new OseeStateException("Must be TeamWorkflowArtifact, set was a [%s]", artifact.getArtifactTypeName());
      }
      this.teamArt = (TeamWorkFlowArtifact) artifact;
      loadTable();
   }

   @Override
   public Result isDirty() {
      return Result.FalseResult;
   }

   @Override
   public void revert() {
      // do nothing
   }

   @Override
   public void saveToArtifact() {
      // do nothing
   }

   /**
    * @return the artifact
    */
   public TeamWorkFlowArtifact getTeamArt() {
      return teamArt;
   }

   @Override
   public Control getErrorMessageControl() {
      return labelWidget;
   }

   @Override
   public String toString() {
      return String.format("%s", getLabel());
   }

   @Override
   public void handleBranchEvent(Sender sender, final BranchEvent branchEvent) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            redrawComposite();
         }
      });

   }

   @Override
   public List<? extends IEventFilter> getEventFilters() {
      return null;
   }

   @Override
   public boolean isEmpty() {
      return xCommitManager.getXCommitViewer().getXViewer().getTree().getItemCount() == 0;
   }

}
