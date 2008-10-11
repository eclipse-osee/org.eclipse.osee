/*
 * Created on Sep 29, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workflow.item;

import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact.DefaultTeamState;

/**
 * @author Donald G. Dunne
 */
public class AtsStatePercentCompleteWeightDefaultWorkflowRule extends AtsStatePercentCompleteWeightRule {

   public static String ID = "atsStatePercentCompleteWeight.DefaultWorkflow";

   public AtsStatePercentCompleteWeightDefaultWorkflowRule() {
      super(ID, ID);
      setDescription("State Percent Complete rule where Analyze and Implement states are where work is performed.");
      addWorkDataKeyValue(DefaultTeamState.Endorse.name(), ".05");
      addWorkDataKeyValue(DefaultTeamState.Analyze.name(), ".1");
      addWorkDataKeyValue(DefaultTeamState.Authorize.name(), ".05");
      addWorkDataKeyValue(DefaultTeamState.Implement.name(), ".79");
      addWorkDataKeyValue(DefaultTeamState.Completed.name(), ".01");
   }

}
