/*******************************************************************************
 * Copyright (c) 2024 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.workdef.model.conditions;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.conditions.ConditionalRule;

/**
 * Note: It enables the widget when at least one value matches the current attribute value.
 *
 * @author Donald G. Dunne
 */
public class RequiredAssigneeIfInRelationCondition extends ConditionalRule {

   private RelationTypeSide relationSide;

   public RequiredAssigneeIfInRelationCondition(RelationTypeSide relationSide) {
      this.relationSide = relationSide;
   }

   public RelationTypeSide getRelationSide() {
      return relationSide;
   }

   public void setRelationSide(RelationTypeSide relationSide) {
      this.relationSide = relationSide;
   }

   /**
    * @return true if this condition exists in Workflow Definition
    */
   public static boolean isRequiredAssigneeIfInRelationConditionExists(IAtsWorkItem workItem) {
      for (ConditionalRule rule : workItem.getWorkDefinition().getConditions()) {
         if (rule instanceof RequiredAssigneeIfInRelationCondition) {
            return true;
         }
      }
      return false;
   }

   /**
    * @return true if workItem is in the specified condition relation
    */
   public static boolean isWorkDefInRelationFromAssigneeCondition(IAtsWorkItem workItem, AtsApi atsApi) {
      for (ConditionalRule rule : workItem.getWorkDefinition().getConditions()) {
         if (rule instanceof RequiredAssigneeIfInRelationCondition) {
            RequiredAssigneeIfInRelationCondition requiredAssigneerule = (RequiredAssigneeIfInRelationCondition) rule;
            if (atsApi.getRelationResolver().isInRelation(workItem.getStoreObject(),
               requiredAssigneerule.getRelationSide())) {
               return true;
            }

         }
      }
      return false;
   }

}
