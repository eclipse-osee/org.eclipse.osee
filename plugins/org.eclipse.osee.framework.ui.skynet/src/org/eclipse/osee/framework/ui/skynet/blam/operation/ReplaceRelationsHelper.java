/*
 * Created on Jun 17, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import org.eclipse.osee.framework.skynet.core.artifact.Attribute;

/**
 * @author Jeff C. Phillips
 */
public class ReplaceRelationsHelper {

   public void deleteRelationOrder(Attribute<?> attribute) {
      attribute.delete();
   }

   public static String addArtifactGuidToOrder(String guid, String beforeGuid, String relationOrder) {
      return relationOrder.replace(beforeGuid, beforeGuid + ", " + guid);
   }

   public static String removeArtifactGuidFromRelationOrder(String guid, String relationOrder) {
      return relationOrder.replace(guid, "");
   }
}
