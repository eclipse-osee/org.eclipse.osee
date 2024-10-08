/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.ats.core.column;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.column.AtsColumnTokensDefault;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.core.column.model.AtsCoreCodeColumn;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class StateColumn extends AtsCoreCodeColumn {

   public StateColumn(AtsApi atsApi) {
      super(AtsColumnTokensDefault.StateColumn, atsApi);
   }

   @Override
   public String getText(IAtsObject atsObject) throws Exception {
      if (atsObject instanceof IAtsWorkItem) {
         IAtsWorkItem workItem = (IAtsWorkItem) atsObject;
         String isBlocked =
            atsApi.getAttributeResolver().getSoleAttributeValue(atsObject, AtsAttributeTypes.BlockedReason, "");
         String isHold =
            atsApi.getAttributeResolver().getSoleAttributeValue(atsObject, AtsAttributeTypes.HoldReason, "");
         if (Strings.isValid(isBlocked) && (Strings.isValid(isHold))) {
            String hold = ((IAtsWorkItem) atsObject).getCurrentStateName() + " (Hold)";
            String block = " " + ((IAtsWorkItem) atsObject).getCurrentStateName() + " (Blocked)";
            return hold + block;
         }

         String currStateName = workItem.getCurrentStateName();
         if (Strings.isValid(isBlocked)) {
            return currStateName + " (Blocked)";
         } else if (Strings.isValid(isHold)) {
            return currStateName + " (Hold)";
         } else {
            return currStateName;
         }
      }
      return "";
   }
}
