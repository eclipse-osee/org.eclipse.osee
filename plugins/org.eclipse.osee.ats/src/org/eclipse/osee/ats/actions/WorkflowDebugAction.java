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

import java.util.List;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.workdef.CompositeStateItem;
import org.eclipse.osee.ats.workdef.RuleDefinition;
import org.eclipse.osee.ats.workdef.StateDefinition;
import org.eclipse.osee.ats.workdef.StateItem;
import org.eclipse.osee.ats.workdef.WidgetDefinition;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class WorkflowDebugAction extends Action {

   private final AbstractWorkflowArtifact sma;

   public WorkflowDebugAction(AbstractWorkflowArtifact sma) {
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
         rd.log(AHTML.bold("Team Definition: " + teamDef));
         for (RuleDefinition ruleDefinition : teamDef.getWorkRules()) {
            rd.log(AHTML.addSpace(6) + "Rule: " + ruleDefinition.toString());
         }
      }

      // Display workflows
      rd.log(AHTML.newline() + AHTML.bold("WorkDefinition id: " + sma.getWorkDefinition().getName()) + AHTML.newline());
      for (RuleDefinition ruleDefinition : sma.getWorkDefinition().getRules()) {
         rd.log(AHTML.addSpace(6) + "Rule: " + ruleDefinition.toString());
      }

      // Display pages
      for (StateDefinition state : sma.getWorkDefinition().getStatesOrdered()) {
         rd.log(AHTML.bold(state.toString()));
         processStateItems(state.getStateItems(), rd, 1);
         for (RuleDefinition rule : state.getRules()) {
            rd.log(AHTML.addSpace(6) + "Rule: " + rule.toString());
         }
      }
      return rd;
   }

   private void processStateItems(List<StateItem> stateItems, XResultData rd, int level) {
      for (StateItem stateItem : stateItems) {
         if (stateItem instanceof WidgetDefinition) {
            rd.log(AHTML.addSpace(6 * level) + "Widget: " + stateItem.toString());
         } else if (stateItem instanceof CompositeStateItem) {
            rd.log(AHTML.addSpace(6 * level) + AHTML.bold("Composite - numColumns = " + ((CompositeStateItem) stateItem).getNumColumns()));
            processStateItems(((CompositeStateItem) stateItem).getStateItems(), rd, level + 1);
         }
      }
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(AtsImage.WORKFLOW_CONFIG);
   }

}
