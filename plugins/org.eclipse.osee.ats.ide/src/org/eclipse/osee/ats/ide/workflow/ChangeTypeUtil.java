/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.workflow;

import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.ide.internal.AtsClientService;

/**
 * @author Donald G. Dunne
 */
public class ChangeTypeUtil {

   public static String getChangeTypeStr(IAtsObject atsObject) {
      ChangeType changeType = getChangeType(atsObject);
      if (changeType == ChangeType.None) {
         return "";
      }
      return changeType.name();
   }

   public static ChangeType getChangeType(IAtsObject atsObject) {
      return ChangeType.getChangeType(
         AtsClientService.get().getQueryServiceClient().getArtifact(atsObject).getSoleAttributeValue(
            AtsAttributeTypes.ChangeType, ""));
   }

   public static void setChangeType(IAtsObject atsObject, ChangeType changeType) {
      if (changeType == ChangeType.None) {
         AtsClientService.get().getQueryServiceClient().getArtifact(atsObject).deleteSoleAttribute(
            AtsAttributeTypes.ChangeType);
      } else {
         AtsClientService.get().getQueryServiceClient().getArtifact(atsObject).setSoleAttributeValue(
            AtsAttributeTypes.ChangeType, changeType.name());
      }
   }

}
