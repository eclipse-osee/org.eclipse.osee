/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.ats.core.workflow.util;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsAction;

/**
 * @author Donald G. Dunne
 */
public class ChangeTypeUtil {

   public static String getChangeTypeStr(IAtsWorkItem workItem, AtsApi atsApi) {
      ChangeType changeType = getChangeType(workItem, atsApi);
      if (changeType == ChangeType.None) {
         return "";
      }
      return changeType.name();
   }

   public static ChangeType getChangeType(IAtsWorkItem workItem, AtsApi atsApi) {
      return ChangeType.getChangeType(
         atsApi.getAttributeResolver().getSoleAttributeValue(workItem, AtsAttributeTypes.ChangeType, ""));
   }

   public static ChangeType getChangeType(IAtsAction action, AtsApi atsApi) {
      return ChangeType.getChangeType(
         atsApi.getAttributeResolver().getSoleAttributeValue(action, AtsAttributeTypes.ChangeType, ""));
   }

   public static ChangeType getChangeType(IAtsObject atsObject, AtsApi atsApi) {
      ChangeType type = null;
      if (atsObject instanceof IAtsWorkItem) {
         type = getChangeType((IAtsWorkItem) atsObject.getStoreObject(), atsApi);
      } else if (atsObject instanceof IAtsAction) {
         type = getChangeType((IAtsAction) atsObject.getStoreObject(), atsApi);
      }
      return type;
   }

   public static void setChangeType(IAtsObject workItem, ChangeType changeType, IAtsChangeSet changes) {
      if (changeType == ChangeType.None) {
         changes.deleteAttributes(workItem, AtsAttributeTypes.ChangeType);
      } else {
         changes.setSoleAttributeValue(workItem, AtsAttributeTypes.ChangeType, changeType.name());
      }
   }

}
