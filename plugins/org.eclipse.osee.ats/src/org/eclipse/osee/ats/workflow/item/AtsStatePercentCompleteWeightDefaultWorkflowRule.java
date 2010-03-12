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
