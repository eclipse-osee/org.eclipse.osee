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
public class PriorityUtil {

   public static String getPriorityStr(Object object) throws OseeCoreException {
      if (object instanceof Artifact) {
         return ((Artifact) object).getSoleAttributeValue(AtsAttributeTypes.PriorityType, "");
      }
      return "";
   }

}
