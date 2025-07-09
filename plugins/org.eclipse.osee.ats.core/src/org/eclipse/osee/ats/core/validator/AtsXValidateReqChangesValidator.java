/*********************************************************************
 * Copyright (c) 2025 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.core.validator;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.IValueProvider;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workdef.WidgetStatus;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;

/**
 * @author Donald G. Dunne
 */
public class AtsXValidateReqChangesValidator extends AtsXWidgetValidator {

   @Override
   public WidgetResult validateTransition(IAtsWorkItem workItem, IValueProvider provider, WidgetDefinition widgetDef,
      StateDefinition fromStateDef, StateDefinition toStateDef, AtsApi atsApi) {
      WidgetResult result = WidgetResult.Success;
      if (workItem.isTeamWorkflow() && "XValidateReqChangesButton".equals(widgetDef.getXWidgetName())) {
         if (isRequiredForTransition(widgetDef) && //
         //
            (atsApi.getBranchService().isWorkingBranchInWork((IAtsTeamWorkflow) workItem) || //
               atsApi.getBranchService().isCommittedBranchExists((IAtsTeamWorkflow) workItem)) //
            //
            && !atsApi.getAttributeResolver().hasAttribute(workItem, AtsAttributeTypes.ValidateChangesRanBy)) {
            return new WidgetResult(WidgetStatus.Invalid_Range, "[Validate Requirement Changes] must be run");
         }
      }
      return result;
   }
}
