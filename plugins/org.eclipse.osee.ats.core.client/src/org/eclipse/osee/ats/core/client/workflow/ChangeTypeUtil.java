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
package org.eclipse.osee.ats.core.client.workflow;

import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class ChangeTypeUtil {

   public static String getChangeTypeStr(Artifact artifact) throws OseeCoreException {
      ChangeType changeType = getChangeType(artifact);
      if (changeType == ChangeType.None) {
         return "";
      }
      return changeType.name();
   }

   public static ChangeType getChangeType(Artifact artifact) throws OseeCoreException {
      return ChangeType.getChangeType(artifact.getSoleAttributeValue(AtsAttributeTypes.ChangeType, ""));
   }

   public static void setChangeType(Artifact artifact, ChangeType changeType) throws OseeCoreException {
      if (changeType == ChangeType.None) {
         artifact.deleteSoleAttribute(AtsAttributeTypes.ChangeType);
      } else {
         artifact.setSoleAttributeValue(AtsAttributeTypes.ChangeType, changeType.name());
      }
   }

}
