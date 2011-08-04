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
      String newRelationOrder = relationOrder.replace(guid, "");
      newRelationOrder = newRelationOrder.trim();

      char[] relationOrderArray = newRelationOrder.toCharArray();
      StringBuilder newRelationOrderArray = new StringBuilder();
      boolean previousCommaChar = false;
      boolean firstTime = true;

      for (int i = 0; i < relationOrderArray.length; i++) {
         char chr = relationOrderArray[i];

         if (chr != ' ') {
            if (chr == ',') {
               if (firstTime) {
                  continue;
               } else if (previousCommaChar) {
                  continue;
               } else if (i == relationOrderArray.length - 1) {
                  continue;
               }
               previousCommaChar = true;
            } else {
               previousCommaChar = false;
            }
            firstTime = false;
            newRelationOrderArray.append(chr);
         }
      }

      String returnString = newRelationOrderArray.toString();

      if (returnString.endsWith(",")) {
         returnString = returnString.substring(0, returnString.length() - 1);
      }
      return returnString;
   }

   public static String getBeforeOrderGuid(String relationOrder, String guid) {
      String beforeGuid = "";
      for (String aGuid : relationOrder.split(",")) {
         if (aGuid.equals(guid)) {
            break;
         }
         beforeGuid = aGuid;
      }
      return beforeGuid;
   }
}
