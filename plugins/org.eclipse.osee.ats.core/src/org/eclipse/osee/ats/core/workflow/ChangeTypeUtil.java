/*
 * Created on May 12, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.workflow;

import org.eclipse.osee.ats.core.type.AtsAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
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
