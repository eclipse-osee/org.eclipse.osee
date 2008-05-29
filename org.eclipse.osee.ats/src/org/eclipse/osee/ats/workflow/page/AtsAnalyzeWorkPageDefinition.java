/*
 * Created on May 28, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workflow.page;

import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.util.DefaultTeamState;
import org.eclipse.osee.ats.workflow.flow.DefaultTeamWorkflowDefinition;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions.BooleanWorkItemId;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;

/**
 * @author Donald G. Dunne
 */
public class AtsAnalyzeWorkPageDefinition extends WorkPageDefinition {

   public static String ID = DefaultTeamWorkflowDefinition.ID + "." + DefaultTeamState.Analyze.name();

   public AtsAnalyzeWorkPageDefinition() {
      this(DefaultTeamState.Analyze.name(), ID, null);
   }

   public AtsAnalyzeWorkPageDefinition(String name, String pageId, String parentId) {
      super(name, pageId, null);
      addWorkItem(AtsWorkDefinitions.getWorkItemDefinition(BooleanWorkItemId.atsRequireStateHourSpentPrompt.name()));
      addWorkItem(AtsWorkDefinitions.getWorkItemDefinition(ATSAttributes.PROBLEM_ATTRIBUTE));
      addWorkItem(AtsWorkDefinitions.getWorkItemDefinition(ATSAttributes.PROPOSED_RESOLUTION_ATTRIBUTE));
      addWorkItem(AtsWorkDefinitions.getWorkItemDefinition(ATSAttributes.CHANGE_TYPE_ATTRIBUTE));
      addWorkItem(AtsWorkDefinitions.getWorkItemDefinition(ATSAttributes.PRIORITY_TYPE_ATTRIBUTE));
      addWorkItem(AtsWorkDefinitions.getWorkItemDefinition(ATSAttributes.DEADLINE_ATTRIBUTE));
      addWorkItem(AtsWorkDefinitions.getWorkItemDefinition(ATSAttributes.ESTIMATED_HOURS_ATTRIBUTE));
   }

}
