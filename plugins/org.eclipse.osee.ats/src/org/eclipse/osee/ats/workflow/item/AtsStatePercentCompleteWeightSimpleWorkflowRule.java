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
