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
package org.eclipse.osee.ats.rest.util;

import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G. Dunne
 */
public class ChangeTypeUtil {

   public static String getChangeTypeStr(ArtifactReadable artifact)  {
      ChangeType changeType = getChangeType(artifact);
      if (changeType == ChangeType.None) {
         return "";
      }
      return changeType.name();
   }

   public static ChangeType getChangeType(ArtifactReadable artifact)  {
      return ChangeType.getChangeType(artifact.getSoleAttributeValue(AtsAttributeTypes.ChangeType, ""));
   }

   public static ChangeType getChangeType(IAtsObject atsObject)  {
      return getChangeType((ArtifactReadable) atsObject.getStoreObject());
   }

   public static void setChangeType(IAtsObject artifact, ChangeType changeType, IAtsChangeSet changes)  {
      if (changeType == ChangeType.None) {
         changes.deleteAttributes(artifact, AtsAttributeTypes.ChangeType);
      } else {
         changes.setSoleAttributeValue(artifact, AtsAttributeTypes.ChangeType, changeType.name());
      }
   }

}
