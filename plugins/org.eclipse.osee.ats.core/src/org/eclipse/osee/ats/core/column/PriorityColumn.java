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
import org.eclipse.osee.ats.api.column.AtsColumnTokensDefault;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.Priorities;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.core.column.model.AtsCoreCodeColumn;
import org.eclipse.osee.ats.core.internal.AtsApiService;

/**
 * @author Donald G. Dunne
 */
public class PriorityColumn extends AtsCoreCodeColumn {

   public PriorityColumn(AtsApi atsApi) {
      super(AtsColumnTokensDefault.PriorityColumn, atsApi);
   }

   @Override
   public String getText(IAtsObject atsObject) {
      return getPriorityStr(atsObject);
   }

   public static String getPriorityStr(IAtsObject atsObject) {
      return AtsApiService.get().getAttributeResolver().getSoleAttributeValue(atsObject, AtsAttributeTypes.Priority,
         "");
   }

   public static String getPriorityStr(IAtsWorkItem workItem, AtsApi atsApi) {
      Priorities priority = getPriority(workItem, atsApi);
      if (priority == Priorities.None) {
         return "";
      }
      return priority.name();
   }

   public static Priorities getPriority(String priorityStr, AtsApi atsApi) {
      return Priorities.getPriority(priorityStr);
   }

   public static Priorities getPriority(IAtsWorkItem workItem, AtsApi atsApi) {
      return Priorities.getPriority(
         atsApi.getAttributeResolver().getSoleAttributeValue(workItem, AtsAttributeTypes.Priority, ""));
   }

   public static Priorities getPriority(IAtsAction action, AtsApi atsApi) {
      return Priorities.getPriority(
         atsApi.getAttributeResolver().getSoleAttributeValue(action, AtsAttributeTypes.Priority, ""));
   }

   public static Priorities getPriority(IAtsObject atsObject, AtsApi atsApi) {
      Priorities priority = null;
      if (atsObject instanceof IAtsWorkItem) {
         priority = getPriority((IAtsWorkItem) atsObject.getStoreObject(), atsApi);
      } else if (atsObject instanceof IAtsAction) {
         priority = getPriority((IAtsAction) atsObject.getStoreObject(), atsApi);
      }
      return priority;
   }

   public static void setPriority(IAtsObject workItem, Priorities priority, IAtsChangeSet changes) {
      if (priority == null || priority == Priorities.None) {
         changes.deleteAttributes(workItem, AtsAttributeTypes.Priority);
      } else {
         changes.setSoleAttributeValue(workItem, AtsAttributeTypes.Priority, priority.name());
      }
   }

}
