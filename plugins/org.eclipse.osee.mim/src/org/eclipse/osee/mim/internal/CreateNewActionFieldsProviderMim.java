/*********************************************************************
 * Copyright (c) 2024 Boeing
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
package org.eclipse.osee.mim.internal;

import java.util.Collection;
import java.util.LinkedList;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.workflow.CreateNewActionField;
import org.eclipse.osee.ats.core.action.ICreateNewActionFieldsProvider;

/**
 * @author Ryan T. Baldwin
 */
public class CreateNewActionFieldsProviderMim implements ICreateNewActionFieldsProvider {

   @Override
   public Collection<CreateNewActionField> getCreateNewActionFields(AtsApi atsApi) {
      Collection<CreateNewActionField> fields = new LinkedList<>();
      fields.add(CreateNewActionField.TargetedVersion);
      return fields;
   }

   @Override
   public boolean actionableItemHasFields(AtsApi atsApi, Collection<IAtsActionableItem> ais) {
      Collection<IAtsTeamDefinition> teams = atsApi.getActionableItemService().getImpactedTeamDefs(ais);
      for (IAtsTeamDefinition team : teams) {
         if (team.getWorkTypes().contains(WorkType.MIM)) {
            return true;
         }
      }
      return false;
   }

}
