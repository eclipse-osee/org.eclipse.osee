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
package org.eclipse.osee.ats.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkRuleDefinition;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class WorkflowDebugAction extends Action {

   private final StateMachineArtifact sma;

   public WorkflowDebugAction(StateMachineArtifact sma) {
      super("Show Workflow Debug Report");
      this.sma = sma;
      setToolTipText("Show workflow definition and all page, widgets and rules");
   }

   @Override
   public void run() {
      try {
         XResultData result = getReport();
         result.report(String.format("Workflow Debug [%s]", sma));
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public XResultData getReport() throws OseeCoreException {
      XResultData rd = new XResultData();
      if (sma.isTeamWorkflow()) {
         TeamDefinitionArtifact teamDef = ((TeamWorkFlowArtifact) sma).getTeamDefinition();
         rd.log("Team Definition: " + teamDef);
         for (WorkRuleDefinition workItemDefinition : teamDef.getWorkRules()) {
            rd.log("        " + workItemDefinition.toString());
         }
      }

      // Display workflows
      rd.log("WorkflowId: " + sma.getWorkFlowDefinition().getId());
      if (Strings.isValid(sma.getWorkFlowDefinition().getParentId())) {
         rd.log("Inherit Workflow from Parent Id: " + sma.getWorkFlowDefinition().getParentId());
      }
      for (WorkRuleDefinition workItemDefinition : sma.getWorkFlowDefinition().getWorkRules()) {
         rd.log("        " + workItemDefinition.toString());
      }

      // Display pages
      for (WorkPageDefinition atsPage : sma.getWorkFlowDefinition().getPagesOrdered()) {
         rd.log(atsPage.toString());
         for (WorkItemDefinition wid : atsPage.getWorkItems(true)) {
            rd.log("        " + wid.toString());
         }
      }
      return rd;
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(AtsImage.WORKFLOW_CONFIG);
   }

}
