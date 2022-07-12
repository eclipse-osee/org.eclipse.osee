/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.core.internal.AtsApiService;

/**
 * @author Donald G. Dunne
 */
public class ChangeTypeColumn extends AbstractServicesColumn {

   public ChangeTypeColumn(AtsApi atsApi) {
      super(atsApi);
   }

   @Override
   public String getText(IAtsObject atsObject) {
      return getChangeTypeStr(atsObject);
   }

   public static String getChangeTypeStr(IAtsObject atsObject) {
      return AtsApiService.get().getAttributeResolver().getSoleAttributeValue(atsObject, AtsAttributeTypes.ChangeType,
         "");
   }

   public static String getChangeTypeStr(IAtsWorkItem workItem, AtsApi atsApi) {
      ChangeTypes changeType = getChangeType(workItem, atsApi);
      if (changeType == ChangeTypes.None) {
         return "";
      }
      return changeType.name();
   }

   public static ChangeTypes getChangeType(String cTypeStr, AtsApi atsApi) {
      return ChangeTypes.getChangeType(cTypeStr);
   }

   public static ChangeTypes getChangeType(IAtsWorkItem workItem, AtsApi atsApi) {
      return ChangeTypes.getChangeType(
         atsApi.getAttributeResolver().getSoleAttributeValue(workItem, AtsAttributeTypes.ChangeType, ""));
   }

   public static ChangeTypes getChangeType(IAtsAction action, AtsApi atsApi) {
      return ChangeTypes.getChangeType(
         atsApi.getAttributeResolver().getSoleAttributeValue(action, AtsAttributeTypes.ChangeType, ""));
   }

   public static ChangeTypes getChangeType(IAtsObject atsObject, AtsApi atsApi) {
      ChangeTypes type = null;
      if (atsObject instanceof IAtsWorkItem) {
         type = getChangeType((IAtsWorkItem) atsObject.getStoreObject(), atsApi);
      } else if (atsObject instanceof IAtsAction) {
         type = getChangeType((IAtsAction) atsObject.getStoreObject(), atsApi);
      }
      return type;
   }

   public static void setChangeType(IAtsObject workItem, ChangeTypes changeType, IAtsChangeSet changes) {
      if (changeType == null || changeType == ChangeTypes.None) {
         changes.deleteAttributes(workItem, AtsAttributeTypes.ChangeType);
      } else {
         changes.setSoleAttributeValue(workItem, AtsAttributeTypes.ChangeType, changeType.name());
      }
   }

}
