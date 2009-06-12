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
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
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

   private Artifact artifact;
   private SMAManager smaMgr;
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
      setSMAMgr();
      if (horizontalSpan < 2) horizontalSpan = 2;
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
      if (toolkit != null) toolkit.adapt(bComp);

      if (toolkit != null)
         createBranchButton = toolkit.createButton(bComp, null, SWT.PUSH);
      else
         createBranchButton = new Button(bComp, SWT.PUSH);
      if (getWorkingBranch() != null) createBranchButton.setEnabled(false);
      createBranchButton.setToolTipText("Create Working Branch");
      createBranchButton.addListener(SWT.Selection, new Listener() {
         public void handleEvent(Event e) {
            smaMgr.getBranchMgr().createWorkingBranch(null, true);
         }
      });

      if (toolkit != null)
         showArtifactExplorer = toolkit.createButton(bComp, null, SWT.PUSH);
      else
         showArtifactExplorer = new Button(bComp, SWT.PUSH);
      if (getWorkingBranch() == null) showArtifactExplorer.setEnabled(false);
      showArtifactExplorer.setToolTipText("Show Artifact Explorer");
      showArtifactExplorer.addListener(SWT.Selection, new Listener() {
         public void handleEvent(Event e) {
            ArtifactExplorer.exploreBranch(getWorkingBranch());
         }
      });

      if (toolkit != null)
         showChangeReport = toolkit.createButton(bComp, null, SWT.PUSH);
      else
         showChangeReport = new Button(bComp, SWT.PUSH);
      if (getWorkingBranch() == null) showChangeReport.setEnabled(false);
      showChangeReport.setToolTipText("Show Change Report");
      showChangeReport.addListener(SWT.Selection, new Listener() {
         public void handleEvent(Event e) {
            smaMgr.getBranchMgr().showChangeReport();
         }
      });

      if (toolkit != null)
         purgeBranchButton = toolkit.createButton(bComp, null, SWT.PUSH);
      else
         purgeBranchButton = new Button(bComp, SWT.PUSH);
      if (getWorkingBranch() == null) purgeBranchButton.setEnabled(false);
      purgeBranchButton.setToolTipText("Delete Working Branch");
      purgeBranchButton.addListener(SWT.Selection, new Listener() {
         public void handleEvent(Event e) {
            smaMgr.getBranchMgr().deleteWorkingBranch(true);
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

   private void setSMAMgr() {
      if (artifact instanceof TeamWorkFlowArtifact) {
         smaMgr = ((TeamWorkFlowArtifact) artifact).getSmaMgr();
      }
   }

   private Branch getWorkingBranch() {
      try {
         return ((IBranchArtifact) artifact).getWorkingBranch();
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE, ex);
         return null;
      }
   }

   private String getWorkingBranchShortName() {
      if (getWorkingBranch() != null) {
         return getWorkingBranch().getBranchShortName();
      }
      return "";
   }

   public String getStatus() {
      try {
         if (smaMgr != null && smaMgr.getBranchMgr().isWorkingBranchArchived()) {
            return BranchStatus.Changes_NotPermitted.name();
         } else if (smaMgr != null && smaMgr.getBranchMgr().isWorkingBranchEverCommitted()) {
            return BranchStatus.Changes_NotPermitted.name();
         } else if (smaMgr != null && smaMgr.getBranchMgr().isWorkingBranchInWork()) {
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

   /* (non-Javadoc)
    * @see osee.skynet.gui.widgets.XWidget#dispose()
    */
   @Override
   public void dispose() {
      OseeEventManager.removeListener(this);
   }

   /* (non-Javadoc)
    * @see osee.skynet.gui.widgets.XWidget#getControl()
    */
   @Override
   public Control getControl() {
      return labelWidget;
   }

   /* (non-Javadoc)
    * @see osee.skynet.gui.widgets.XWidget#getData()
    */
   @Override
   public Object getData() {
      return null;
   }

   /* (non-Javadoc)
    * @see osee.skynet.gui.widgets.XWidget#getReportData()
    */
   @Override
   public String getReportData() {
      return null;
   }

   /* (non-Javadoc)
    * @see osee.skynet.gui.widgets.XWidget#getXmlData()
    */
   @Override
   public String getXmlData() {
      return null;
   }

   /* (non-Javadoc)
    * @see osee.skynet.gui.widgets.XWidget#isValid()
    */
   @Override
   public IStatus isValid() {
      // Need this cause it removes all error items of this namespace
      return new Status(IStatus.OK, getClass().getSimpleName(), "");
   }

   /* (non-Javadoc)
    * @see osee.skynet.gui.widgets.XWidget#refresh()
    */
   @Override
   public void refresh() {
      if (labelWidget == null || labelWidget.isDisposed()) {
         return;
      }
      labelWidget.setText(getLabel() + ": " + getWorkingBranchShortName() + " " + getStatus());
      try {
         if (smaMgr != null && smaMgr.getBranchMgr() != null) {
            if (createBranchButton != null) {
               createBranchButton.setEnabled(!smaMgr.getBranchMgr().isWorkingBranchInWork() && !smaMgr.getBranchMgr().isCommittedBranchExists());
            }
            if (showArtifactExplorer != null) {
               if (getWorkingBranch() == null)
                  showArtifactExplorer.setEnabled(false);
               else {
                  if (getStatus().equals(BranchStatus.Changes_NotPermitted.name())) {
                     showArtifactExplorer.setEnabled(false);
                  } else
                     showArtifactExplorer.setEnabled(true);
               }
            }
            if (showChangeReport != null) {
               if (smaMgr.getBranchMgr().isWorkingBranchInWork() || smaMgr.getBranchMgr().isCommittedBranchExists())
                  showChangeReport.setEnabled(true);
               else
                  showChangeReport.setEnabled(false);
            }
            if (purgeBranchButton != null) {
               purgeBranchButton.setEnabled(smaMgr.getBranchMgr().isWorkingBranchInWork() && !smaMgr.getBranchMgr().isCommittedBranchExists());
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE, ex);
      }
   }

   /* (non-Javadoc)
    * @see osee.skynet.gui.widgets.XWidget#setXmlData(java.lang.String)
    */
   @Override
   public void setXmlData(String str) {
   }

   /* (non-Javadoc)
    * @see osee.skynet.gui.widgets.XWidget#toHTML(java.lang.String)
    */
   @Override
   public String toHTML(String labelFont) {
      return "";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget#isDirty()
    */
   @Override
   public Result isDirty() throws OseeCoreException {
      return Result.FalseResult;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget#revert()
    */
   @Override
   public void revert() throws OseeCoreException {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget#saveToArtifact()
    */
   @Override
   public void saveToArtifact() throws OseeCoreException {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget#setArtifact(org.eclipse.osee.framework.skynet.core.artifact.Artifact, java.lang.String)
    */
   @Override
   public void setArtifact(Artifact artifact, String attrName) throws OseeCoreException {
      this.artifact = artifact;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.event.IBranchEventListener#handleBranchEvent(org.eclipse.osee.framework.skynet.core.event.Sender, org.eclipse.osee.framework.skynet.core.event.BranchEventType, int)
    */
   @Override
   public void handleBranchEvent(Sender sender, BranchEventType branchModType, int branchId) throws OseeCoreException {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            refresh();
         }
      });
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.event.IBranchEventListener#handleLocalBranchToArtifactCacheUpdateEvent(org.eclipse.osee.framework.skynet.core.event.Sender)
    */
   @Override
   public void handleLocalBranchToArtifactCacheUpdateEvent(Sender sender) throws OseeCoreException {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.event.IFrameworkTransactionEventListener#handleFrameworkTransactionEvent(org.eclipse.osee.framework.skynet.core.event.Sender, org.eclipse.osee.framework.skynet.core.event.FrameworkTransactionData)
    */
   @Override
   public void handleFrameworkTransactionEvent(Sender sender, FrameworkTransactionData transData) throws OseeCoreException {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            refresh();
         }
      });
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
