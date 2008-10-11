/*
 * Created on Sep 29, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workflow.item;

import org.eclipse.osee.ats.workflow.flow.SimpleWorkflowDefinition.SimpleState;

/**
 * @author Donald G. Dunne
 */
public class AtsStatePercentCompleteWeightSimpleWorkflowRule extends AtsStatePercentCompleteWeightRule {

   public static String ID = "atsStatePercentCompleteWeight.SimpleWorkflow";

   public AtsStatePercentCompleteWeightSimpleWorkflowRule() {
      super(ID, ID);
      setDescription("State Percent Complete rule where InWork work is performed.");
      addWorkDataKeyValue(SimpleState.Endorse.name(), ".10");
      addWorkDataKeyValue(SimpleState.InWork.name(), ".89");
      addWorkDataKeyValue(SimpleState.Completed.name(), ".01");
   }

}
