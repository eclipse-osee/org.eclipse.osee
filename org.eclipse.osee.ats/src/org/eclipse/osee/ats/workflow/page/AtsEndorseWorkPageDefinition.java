/*
 * Created on May 28, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workflow.page;

import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact.DefaultTeamState;
import org.eclipse.osee.ats.workflow.flow.TeamWorkflowDefinition;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions.RuleWorkItemId;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;

/**
 * @author Donald G. Dunne
 */
public class AtsEndorseWorkPageDefinition extends WorkPageDefinition {

   public static String ID = TeamWorkflowDefinition.ID + "." + DefaultTeamState.Endorse.name();

   public AtsEndorseWorkPageDefinition() {
      this(DefaultTeamState.Endorse.name(), ID, null);
   }

   public AtsEndorseWorkPageDefinition(String name, String pageId, String parentId) {
      super(name, pageId, parentId);
      addWorkItem(RuleWorkItemId.atsRequireStateHourSpentPrompt.name());
      addWorkItem("ats.Title");
      addWorkItem(ATSAttributes.DESCRIPTION_ATTRIBUTE.getStoreName());
      addWorkItem(ATSAttributes.PROPOSED_RESOLUTION_ATTRIBUTE.getStoreName());
      addWorkItem(ATSAttributes.CHANGE_TYPE_ATTRIBUTE.getStoreName());
      addWorkItem(ATSAttributes.PRIORITY_TYPE_ATTRIBUTE.getStoreName());
      addWorkItem(ATSAttributes.DEADLINE_ATTRIBUTE.getStoreName());
      addWorkItem(ATSAttributes.VALIDATION_REQUIRED_ATTRIBUTE.getStoreName());
      addWorkItem(ATSAttributes.USER_COMMUNITY_ATTRIBUTE.getStoreName());
   }

}
