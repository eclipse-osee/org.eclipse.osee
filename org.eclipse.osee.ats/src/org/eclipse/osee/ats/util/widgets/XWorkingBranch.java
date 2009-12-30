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
package org.eclipse.osee.ats.util.widgets;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.FrameworkTransactionData;
import org.eclipse.osee.framework.skynet.core.event.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.IFrameworkTransactionEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.ArtifactExplorer;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.IBranchArtifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

/**
 * @author Megumi Telles
 */
public class XWorkingBranch extends XWidget implements IArtifactWidget, IFrameworkTransactionEventListener, IBranchEventListener {

   private TeamWorkFlowArtifact teamArt;
   private Button createBranchButton;
   private Button showArtifactExplorer;
   private Button showChangeReport;
   private Button purgeBranchButton;
   public static enum BranchStatus {
      Not_Started, Changes_InProgress, Changes_NotPermitted
   }
   public final static String WIDGET_ID = ATSAttributes.WORKING_BRANCH_WIDGET.getStoreName();

   public XWorkingBranch() {
      super("Working Branch", "");
      OseeEventManager.addListener(this);
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      if (horizontalSpan < 2) {
         horizontalSpan = 2;
      }
      if (!getLabel().equals("")) {
         labelWidget = new Label(parent, SWT.NONE);
         labelWidget.setText(getLabel() + ": " + getWorkingBranchShortName() + " " + getStatus());
         if (getToolTip() != null) {
            labelWidget.setToolTipText(getToolTip());
         }
      }

      Composite bComp = new Composite(parent, SWT.NONE);
      bComp.setLayout(new GridLayout(4, false));
      bComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      if (toolkit != null) {
         toolkit.adapt(bComp);
      }

      if (toolkit != null) {
         createBranchButton = toolkit.createButton(bComp, null, SWT.PUSH);
      } else {
         createBranchButton = new Button(bComp, SWT.PUSH);
      }
      if (getWorkingBranch() != null) {
         createBranchButton.setEnabled(false);
      }
      createBranchButton.setToolTipText("Create Working Branch");
      createBranchButton.addListener(SWT.Selection, new Listener() {
         public void handleEvent(Event e) {
            teamArt.getBranchMgr().createWorkingBranch(null, true);
         }
      });

      if (toolkit != null) {
         showArtifactExplorer = toolkit.createButton(bComp, null, SWT.PUSH);
      } else {
         showArtifactExplorer = new Button(bComp, SWT.PUSH);
      }
      if (getWorkingBranch() == null) {
         showArtifactExplorer.setEnabled(false);
      }
      showArtifactExplorer.setToolTipText("Show Artifact Explorer");
      showArtifactExplorer.addListener(SWT.Selection, new Listener() {
         public void handleEvent(Event e) {
            ArtifactExplorer.exploreBranch(getWorkingBranch());
         }
      });

      if (toolkit != null) {
         showChangeReport = toolkit.createButton(bComp, null, SWT.PUSH);
      } else {
         showChangeReport = new Button(bComp, SWT.PUSH);
      }
      if (getWorkingBranch() == null) {
         showChangeReport.setEnabled(false);
      }
      showChangeReport.setToolTipText("Show Change Report");
      showChangeReport.addListener(SWT.Selection, new Listener() {
         public void handleEvent(Event e) {
            teamArt.getBranchMgr().showChangeReport();
         }
      });

      if (toolkit != null) {
         purgeBranchButton = toolkit.createButton(bComp, null, SWT.PUSH);
      } else {
         purgeBranchButton = new Button(bComp, SWT.PUSH);
      }
      if (getWorkingBranch() == null) {
         purgeBranchButton.setEnabled(false);
      }
      purgeBranchButton.setToolTipText("Delete Working Branch");
      purgeBranchButton.addListener(SWT.Selection, new Listener() {
         public void handleEvent(Event e) {
            teamArt.getBranchMgr().deleteWorkingBranch(true);
            refresh();
         }
      });

      if (AtsPlugin.getInstance() != null) {
         createBranchButton.setImage(ImageManager.getImage(FrameworkImage.BRANCH));
         purgeBranchButton.setImage(ImageManager.getImage(FrameworkImage.TRASH));
      }
      if (SkynetGuiPlugin.getInstance() != null) {
         showArtifactExplorer.setImage(ImageManager.getImage(FrameworkImage.ARTIFACT_EXPLORER));
         showChangeReport.setImage(ImageManager.getImage(FrameworkImage.BRANCH_CHANGE));
      }
      refresh();
   }

   private Branch getWorkingBranch() {
      try {
         return ((IBranchArtifact) teamArt).getWorkingBranch();
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE, ex);
         return null;
      }
   }

   private String getWorkingBranchShortName() {
      Branch branch = getWorkingBranch();
      if (branch != null) {
         return branch.getShortName();
      }
      return "";
   }

   public String getStatus() {
      try {
         if (teamArt != null && teamArt.getBranchMgr().isWorkingBranchEverCommitted()) {
            return BranchStatus.Changes_NotPermitted.name();
         } else if (teamArt != null && teamArt.getBranchMgr().isWorkingBranchInWork()) {
            return BranchStatus.Changes_InProgress.name();
         } else {
            return BranchStatus.Not_Started.name();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE, ex);
         return "";
      }
   }

   @Override
   public void setFocus() {
   }

   @Override
   public void dispose() {
      OseeEventManager.removeListener(this);
   }

   @Override
   public Control getControl() {
      return labelWidget;
   }

   @Override
   public Object getData() {
      return null;
   }

   @Override
   public String getReportData() {
      return null;
   }

   @Override
   public String getXmlData() {
      return null;
   }

   @Override
   public IStatus isValid() {
      // Need this cause it removes all error items of this namespace
      return new Status(IStatus.OK, getClass().getSimpleName(), "");
   }

   @Override
   public void refresh() {
      if (teamArt == null || teamArt.getBranchMgr() == null || labelWidget == null || labelWidget.isDisposed()) {
         return;
      }
      final boolean forcePend = OseeProperties.isInTest();
      Runnable runnable = new Runnable() {
         @Override
         public void run() {
            try {
               final boolean workingBranchInWork = teamArt.getBranchMgr().isWorkingBranchInWork();
               final boolean committedBranchExists = teamArt.getBranchMgr().isCommittedBranchExists();
               final Branch workingBranch = teamArt.getBranchMgr().getWorkingBranch();
               final String status = getStatus();
               final boolean changesNotPermitted = status.equals(BranchStatus.Changes_NotPermitted.name());
               final String labelStr =
                     getLabel() + ": " + (workingBranch == null ? "" : workingBranch.getShortName()) + " " + status;
               Displays.ensureInDisplayThread(new Runnable() {
                  public void run() {
                     if (Widgets.isAccessible(labelWidget)) {
                        labelWidget.setText(labelStr);
                        if (Widgets.isAccessible(createBranchButton)) {
                           createBranchButton.setEnabled(!workingBranchInWork && !committedBranchExists);
                        }
                        if (Widgets.isAccessible(showArtifactExplorer)) {
                           showArtifactExplorer.setEnabled(workingBranch != null && !changesNotPermitted);
                        }
                        if (Widgets.isAccessible(showChangeReport)) {
                           showChangeReport.setEnabled(workingBranchInWork || committedBranchExists);
                        }
                        if (Widgets.isAccessible(purgeBranchButton)) {
                           purgeBranchButton.setEnabled(workingBranchInWork && !committedBranchExists);
                        }
                     }
                  }
               }, forcePend);
            } catch (OseeCoreException ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE, ex);
            }
         }
      };
      if (forcePend) {
         runnable.run();
      } else {
         Thread thread = new Thread(runnable);
         thread.start();
      }
   }

   @Override
   public void setXmlData(String str) {
   }

   @Override
   public String toHTML(String labelFont) {
      return "";
   }

   @Override
   public Result isDirty() throws OseeCoreException {
      return Result.FalseResult;
   }

   @Override
   public void revert() throws OseeCoreException {
   }

   @Override
   public void saveToArtifact() throws OseeCoreException {
   }

   @Override
   public void setArtifact(Artifact artifact, String attrName) throws OseeCoreException {
      this.teamArt = (TeamWorkFlowArtifact) artifact;
   }

   @Override
   public void handleBranchEvent(Sender sender, BranchEventType branchModType, int branchId) throws OseeCoreException {
      refresh();
   }

   @Override
   public void handleLocalBranchToArtifactCacheUpdateEvent(Sender sender) throws OseeCoreException {
   }

   @Override
   public void handleFrameworkTransactionEvent(Sender sender, FrameworkTransactionData transData) throws OseeCoreException {
      refresh();
   }

   public Button getCreateBranchButton() {
      return createBranchButton;
   }

   public Button getShowArtifactExplorerButton() {
      return showArtifactExplorer;
   }

   public Button getShowChangeReportButton() {
      return showChangeReport;
   }

   public Button getDeleteBranchButton() {
      return purgeBranchButton;
   }

}
