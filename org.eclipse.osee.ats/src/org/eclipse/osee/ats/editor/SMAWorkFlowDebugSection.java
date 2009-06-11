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
package org.eclipse.osee.ats.editor;

import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.editor.stateItem.AtsDebugWorkPage;
import org.eclipse.osee.ats.util.AtsLib;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts.OpenView;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkRuleDefinition;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author Donald G. Dunne
 */
public class SMAWorkFlowDebugSection extends SMAWorkFlowSection {

   private Composite workComp;
   private XFormToolkit toolkit;

   /**
    * @param parent
    * @param toolkit
    * @param style
    * @param page
    * @param smaMgr
    * @throws Exception
    */
   public SMAWorkFlowDebugSection(Composite parent, XFormToolkit toolkit, int style, SMAManager smaMgr) throws OseeCoreException {
      super(parent, toolkit, style, new AtsDebugWorkPage(), smaMgr);
   }

   @Override
   protected Composite createWorkArea(Composite comp, AtsWorkPage page, XFormToolkit toolkit) throws OseeCoreException {
      this.toolkit = toolkit;
      workComp = super.createWorkArea(comp, page, toolkit);
      toolkit.createLabel(workComp, "ATS Debug Section");

      Hyperlink link = toolkit.createHyperlink(workComp, "Dirty Report", SWT.NONE);
      link.addHyperlinkListener(new IHyperlinkListener() {

         public void linkEntered(HyperlinkEvent e) {
         }

         public void linkExited(HyperlinkEvent e) {
         }

         public void linkActivated(HyperlinkEvent e) {
            Result result = smaMgr.getEditor().isDirtyResult();
            AWorkbench.popup("Success", Strings.isValid(result.getText()) ? result.getText() : "Success");
         }

      });
      link = toolkit.createHyperlink(workComp, "Refresh Dirty", SWT.NONE);
      link.addHyperlinkListener(new IHyperlinkListener() {

         public void linkEntered(HyperlinkEvent e) {
         }

         public void linkExited(HyperlinkEvent e) {
         }

         public void linkActivated(HyperlinkEvent e) {
            smaMgr.getEditor().onDirtied();
         }

      });
      link = toolkit.createHyperlink(workComp, "Open VUE Workflow", SWT.NONE);
      link.addHyperlinkListener(new IHyperlinkListener() {

         public void linkEntered(HyperlinkEvent e) {
         }

         public void linkExited(HyperlinkEvent e) {
         }

         public void linkActivated(HyperlinkEvent e) {
            try {
               String hrid = smaMgr.getWorkFlowDefinition().getId().replaceFirst("^.* - ", "");
               if (hrid.length() != 5)
                  AWorkbench.popup("Open Workflow",
                        "Workflow is NOT an artifact\n\n" + smaMgr.getWorkFlowDefinition().getId());
               else
                  AtsLib.open(hrid, OpenView.ArtifactEditor);
            } catch (Exception ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });
      link = toolkit.createHyperlink(workComp, "Open Team Definition", SWT.NONE);
      link.addHyperlinkListener(new IHyperlinkListener() {

         public void linkEntered(HyperlinkEvent e) {
         }

         public void linkExited(HyperlinkEvent e) {
         }

         public void linkActivated(HyperlinkEvent e) {
            if (!(smaMgr.getSma() instanceof TeamWorkFlowArtifact)) {
               AWorkbench.popup("Open Workflow", "Workflow is NOT TeamWorkflowArtifact");
            } else {
               try {
                  AtsLib.open(((TeamWorkFlowArtifact) smaMgr.getSma()).getTeamDefinition().getGuid(),
                        OpenView.ArtifactEditor);
               } catch (Exception ex) {
                  OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         }
      });

      // Display team definition
      if (smaMgr.getSma() instanceof TeamWorkFlowArtifact) {
         TeamDefinitionArtifact teamDef = ((TeamWorkFlowArtifact) smaMgr.getSma()).getTeamDefinition();
         addDebug("Team Definition: " + teamDef);
         for (WorkRuleDefinition workItemDefinition : teamDef.getWorkRules()) {
            addDebug("        " + workItemDefinition.toString());
         }
      }

      // Display workflows
      addDebug("WorkflowId: " + smaMgr.getWorkFlowDefinition().getId());
      if (smaMgr.getWorkFlowDefinition().getParentId() != null && !smaMgr.getWorkFlowDefinition().getParentId().equals(
            "")) addDebug("Inherit Workflow from Parent Id: " + smaMgr.getWorkFlowDefinition().getParentId());
      for (WorkRuleDefinition workItemDefinition : smaMgr.getWorkFlowDefinition().getWorkRules()) {
         addDebug("        " + workItemDefinition.toString());
      }

      // Display pages
      for (WorkPageDefinition atsPage : smaMgr.getWorkFlowDefinition().getPagesOrdered()) {
         addDebug(atsPage.toString());
         for (WorkItemDefinition wid : atsPage.getWorkItems(true)) {
            addDebug("        " + wid.toString());
         }
      }

      return workComp;
   }

   public void addDebug(String str) {
      toolkit.createText(workComp, str, SWT.MULTI | SWT.WRAP);
      workComp.layout();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.SMAWorkFlowSection#isShowReviewInfo()
    */
   @Override
   protected boolean isShowReviewInfo() throws OseeCoreException {
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.SMAWorkFlowSection#isShowTaskInfo()
    */
   @Override
   protected boolean isShowTaskInfo() throws OseeCoreException {
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.SMAWorkFlowSection#isShowTargetedVersion()
    */
   @Override
   protected boolean isShowTargetedVersion() throws OseeCoreException {
      return false;
   }

}
