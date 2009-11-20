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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.IMergeBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.MergeBranchEventType;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.IBranchArtifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;

/**
 * @author Donald G. Dunne
 */
public class XCommitManager extends XWidget implements IArtifactWidget, IMergeBranchEventListener, IBranchEventListener {

   private CommitXManager xCommitManager;
   private IDirtiableEditor editor;
   public final static String normalColor = "#EEEEEE";
   private TeamWorkFlowArtifact teamArt;
   private static final int paddedTableHeightHint = 2;
   private Label extraInfoLabel;
   public final static String WIDGET_ID = ATSAttributes.COMMIT_MANAGER_WIDGET.getStoreName();
   private ToolBar toolBar;

   /**
    * @param label
    */
   public XCommitManager() {
      super("Commit Manager");
      OseeEventManager.addListener(this);
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {

      // Create Text Widgets
      if (isDisplayLabel() && !getLabel().equals("")) {
         labelWidget = new Label(parent, SWT.NONE);
         labelWidget.setText(getLabel() + ":");
         if (getToolTip() != null) {
            labelWidget.setToolTipText(getToolTip());
         }
      }

      try {
         if (!teamArt.getSmaMgr().getBranchMgr().isWorkingBranchInWork() && !teamArt.getSmaMgr().getBranchMgr().isCommittedBranchExists()) {
            labelWidget.setText(getLabel() + ": No working or committed branches available.");
         } else {

            Composite mainComp = new Composite(parent, SWT.BORDER);
            mainComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            mainComp.setLayout(ALayout.getZeroMarginLayout());
            if (toolkit != null) {
               toolkit.paintBordersFor(mainComp);
            }

            createTaskActionBar(mainComp);

            labelWidget.setText(getLabel() + ": ");// If ATS Admin, allow right-click to auto-complete reviews
            if (AtsUtil.isAtsAdmin() && !AtsUtil.isProductionDb()) {
               labelWidget.addListener(SWT.MouseUp, new Listener() {
                  @Override
                  public void handleEvent(Event event) {
                     if (event.button == 3) {
                        if (!MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Auto Commit Branches",
                              "ATS Admin\n\nAuto Commit Branches?")) {
                           return;
                        }
                        try {
                           for (Branch destinationBranch : teamArt.getSmaMgr().getBranchMgr().getBranchesLeftToCommit()) {
                              teamArt.getSmaMgr().getBranchMgr().commitWorkingBranch(false, true, destinationBranch,
                                    true);
                              Thread.sleep(1000);
                           }
                        } catch (Exception ex) {
                           OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
                        }
                     }
                  }
               });
            }

            xCommitManager = new CommitXManager(mainComp, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION, this);
            xCommitManager.getTree().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

            xCommitManager.setContentProvider(new XCommitContentProvider(xCommitManager));
            xCommitManager.setLabelProvider(new XCommitLabelProvider(xCommitManager));

            if (toolkit != null && xCommitManager.getStatusLabel() != null) {
               toolkit.adapt(xCommitManager.getStatusLabel(), false, false);
            }

            setXviewerTree();
            //            new ActionContributionItem(xCommitManager.getCustomizeAction()).fill(toolBar, -1);

            loadTable();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   int lastDefectListSize = 0;

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
      extraInfoLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));

      Composite rightComp = new Composite(bComp, SWT.NONE);
      rightComp.setLayout(new GridLayout());
      rightComp.setLayoutData(new GridData(GridData.END));

      toolBar = new ToolBar(rightComp, SWT.FLAT | SWT.RIGHT);
      GridData gd = new GridData(GridData.FILL_HORIZONTAL);
      toolBar.setLayoutData(gd);
      ToolItem item = null;

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(ImageManager.getImage(FrameworkImage.REFRESH));
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
            Collection<ICommitConfigArtifact> configArtSet =
                  teamArt.getSmaMgr().getBranchMgr().getConfigArtifactsConfiguredToCommitTo();
            xCommitManager.setInput(configArtSet);
            xCommitManager.refresh();
            refresh();
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @SuppressWarnings("unchecked")
   public ArrayList<Branch> getSelectedBranches() {
      ArrayList<Branch> items = new ArrayList<Branch>();
      if (xCommitManager == null) {
         return items;
      }
      if (xCommitManager.getSelection().isEmpty()) {
         return items;
      }
      Iterator i = ((IStructuredSelection) xCommitManager.getSelection()).iterator();
      while (i.hasNext()) {
         Object obj = i.next();
         items.add((Branch) obj);
      }
      return items;
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
   public void setFocus() {
      if (xCommitManager != null) {
         xCommitManager.getTree().setFocus();
      }
   }

   @Override
   public void refresh() {
      if (xCommitManager == null || xCommitManager.getTree() == null || xCommitManager.getTree().isDisposed()) {
         return;
      }
      validate();
      setXviewerTree();
   }

   private void updateExtraInfoLabel(final int color, final String infoStr) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            if (Widgets.isAccessible(extraInfoLabel)) {
               String currentString = extraInfoLabel.getText();
               if (infoStr == null && currentString != null || infoStr != null && currentString == null || !infoStr.equals(currentString)) {
                  extraInfoLabel.setText("Double-click item to perform Action");
               }
               extraInfoLabel.setForeground(Display.getCurrent().getSystemColor(color));
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
         if (xCommitManager != null && xCommitManager.getXCommitViewer() != null && xCommitManager.getXCommitViewer().getTeamArt() != null && xCommitManager.getXCommitViewer().getTeamArt().getSmaMgr() != null && xCommitManager.getXCommitViewer().getTeamArt().getSmaMgr().getBranchMgr() != null) {
            if (!xCommitManager.getXCommitViewer().getTeamArt().getSmaMgr().getBranchMgr().isAllObjectsToCommitToConfigured()) {
               infoStr = "All branches must be configured and committed - Double-click item to perform Action";
               backgroundColor = SWT.COLOR_RED;
               returnStatus =
                     new Status(IStatus.ERROR, getClass().getSimpleName(),
                           "All branches must be configured and committed.");
            } else if (!xCommitManager.getXCommitViewer().getTeamArt().getSmaMgr().getBranchMgr().isBranchesAllCommitted()) {
               infoStr = "All branches must be committed - Double-click item to perform Action";
               backgroundColor = SWT.COLOR_RED;
               returnStatus = new Status(IStatus.ERROR, getClass().getSimpleName(), "All branches must be committed.");
            }
         }
         updateExtraInfoLabel(backgroundColor, infoStr);
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         return new Status(IStatus.ERROR, getClass().getSimpleName(), ex.getLocalizedMessage());
      }
      return returnStatus;
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

   public void setEditor(IDirtiableEditor editor) {
      this.editor = editor;
   }

   public void setArtifact(Artifact artifact, String attrName) throws IllegalStateException {
      if (!(artifact instanceof TeamWorkFlowArtifact)) {
         throw new IllegalStateException("Must be TeamWorkflowArtifact, set was a " + artifact.getArtifactTypeName());
      }
      this.teamArt = (TeamWorkFlowArtifact) artifact;
      loadTable();
   }

   @Override
   public Result isDirty() throws OseeCoreException {
      return Result.FalseResult;
   }

   public Branch getWorkingBranch() throws OseeCoreException {
      return ((IBranchArtifact) teamArt).getWorkingBranch();
   }

   @Override
   public void revert() throws OseeCoreException {
   }

   @Override
   public void saveToArtifact() throws OseeCoreException {
   }

   /**
    * @return the artifact
    */
   public TeamWorkFlowArtifact getTeamArt() {
      return teamArt;
   }

   @Override
   public void handleBranchEvent(Sender sender, BranchEventType branchModType, int branchId) throws OseeCoreException {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            loadTable();
         }
      });
   }

   @Override
   public void handleLocalBranchToArtifactCacheUpdateEvent(Sender sender) {
   }

   @Override
   public void handleMergeBranchEvent(Sender sender, MergeBranchEventType branchModType, int branchId) throws OseeCoreException {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            xCommitManager.refresh();
            refresh();
         }
      });
   }

   @Override
   public Control getErrorMessageControl() {
      return labelWidget;
   }

}
