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

import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.ArtifactExplorer;
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
public class XWorkingBranch extends XWidget implements IArtifactWidget {

   private Artifact artifact;
   private SMAManager smaMgr;
   private Button createBranch;
   private Button showArtifactExplorer;
   private Button showChangeReport;
   private Button deleteBranch;

   public XWorkingBranch() {
      super("Working Branch", "");
   }

   /**
    * 
    */
   @Override
   public void createWidgets(Composite parent, int horizontalSpan) {
      setSMAMgr();
      if (horizontalSpan < 2) horizontalSpan = 2;
      if (!label.equals("")) {
         labelWidget = new Label(parent, SWT.NONE);
         labelWidget.setText(label + ": " + getWorkingBranchShortName() + " " + getStatus());
         if (toolTip != null) {
            labelWidget.setToolTipText(toolTip);
         }
      }

      Composite bComp = new Composite(parent, SWT.NONE);
      bComp.setLayout(new GridLayout(4, false));
      bComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      createBranch = new Button(bComp, SWT.PUSH);
      if (getWorkingBranch() != null) createBranch.setEnabled(false);
      createBranch.setToolTipText("Create Working Branch");
      createBranch.addListener(SWT.Selection, new Listener() {
         public void handleEvent(Event e) {
            Result result = smaMgr.getBranchMgr().createWorkingBranch(null, true);
            if (result.isFalse()) result.popup();
         }
      });

      showArtifactExplorer = new Button(bComp, SWT.PUSH);
      if (getWorkingBranch() == null) showArtifactExplorer.setEnabled(false);
      showArtifactExplorer.setToolTipText("Show Artifact Explorer");
      showArtifactExplorer.addListener(SWT.Selection, new Listener() {
         public void handleEvent(Event e) {
            ArtifactExplorer.exploreBranch(getWorkingBranch());
         }
      });

      showChangeReport = new Button(bComp, SWT.PUSH);
      if (getWorkingBranch() == null) showChangeReport.setEnabled(false);
      showChangeReport.setToolTipText("Show Change Report");
      showChangeReport.addListener(SWT.Selection, new Listener() {
         public void handleEvent(Event e) {
            smaMgr.getBranchMgr().showChangeReport();
         }
      });

      deleteBranch = new Button(bComp, SWT.PUSH);
      if (getWorkingBranch() == null) deleteBranch.setEnabled(false);
      deleteBranch.setToolTipText("Delete Working Branch");
      deleteBranch.addListener(SWT.Selection, new Listener() {
         public void handleEvent(Event e) {
            smaMgr.getBranchMgr().deleteEmptyWorkingBranch();
            refresh();
         }
      });

      if (AtsPlugin.getInstance() != null) {
         createBranch.setImage(AtsPlugin.getInstance().getImage("branch.gif"));
      }
      if (SkynetGuiPlugin.getInstance() != null) {
         showArtifactExplorer.setImage(SkynetGuiPlugin.getInstance().getImage("artifact_explorer.gif"));
         showChangeReport.setImage(SkynetGuiPlugin.getInstance().getImage("branch_change.gif"));
         deleteBranch.setImage(SkynetGuiPlugin.getInstance().getImage("delete.gif"));
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

   private String getStatus() {
      try {
         if (getWorkingBranch() == null) {
            return "Not Started";
         } else

         if (smaMgr.getStateMgr().getCurrentStateName().equals("Promote")) {
            return "Changes Not Permitted";
         } else {
            return "Changes In-Progress";
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
   public Result isValid() {
      return Result.TrueResult;
   }

   /* (non-Javadoc)
    * @see osee.skynet.gui.widgets.XWidget#refresh()
    */
   @Override
   public void refresh() {
      if (labelWidget != null) {
         labelWidget.setText(label + ": " + getWorkingBranchShortName() + " " + getStatus());
      }
      if (createBranch != null) {
         if (getWorkingBranch() != null)
            createBranch.setEnabled(false);
         else
            createBranch.setEnabled(true);
      }
      if (showArtifactExplorer != null) {
         if (getWorkingBranch() == null)
            showArtifactExplorer.setEnabled(false);
         else
            showArtifactExplorer.setEnabled(true);
      }
      if (showChangeReport != null) {
         if (getWorkingBranch() == null)
            showChangeReport.setEnabled(false);
         else
            showChangeReport.setEnabled(true);
      }
      if (deleteBranch != null) {
         if (getWorkingBranch() == null)
            deleteBranch.setEnabled(false);
         else
            deleteBranch.setEnabled(true);
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

}
