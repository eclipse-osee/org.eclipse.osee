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

import org.eclipse.osee.ats.util.TeamState;

/**
 * @author Donald G. Dunne
 */
public class AtsStatePercentCompleteWeightDefaultWorkflowRule extends AtsStatePercentCompleteWeightRule {

   public final static String ID = "atsStatePercentCompleteWeight.DefaultWorkflow";

   public AtsStatePercentCompleteWeightDefaultWorkflowRule() {
      super(ID, ID);
      setDescription("State Percent Complete rule where Analyze and Implement states are where work is performed.");
      addWorkDataKeyValue(TeamState.Endorse.getPageName(), ".05");
      addWorkDataKeyValue(TeamState.Analyze.getPageName(), ".1");
      addWorkDataKeyValue(TeamState.Authorize.getPageName(), ".05");
      addWorkDataKeyValue(TeamState.Implement.getPageName(), ".79");
      addWorkDataKeyValue(TeamState.Completed.getPageName(), ".01");
   }

}
