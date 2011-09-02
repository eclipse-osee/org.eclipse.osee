/*
 * Created on Jun 17, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.replace;

import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Jeff C. Phillips
 */
public class ReplaceUtil {

   public void deleteRelationOrder(Attribute<?> attribute) {
      attribute.delete();
   }

   public static Attribute<?> getRelationOrder(Artifact artifact) {
      Attribute<?> relationOrder = null;
      try {
         relationOrder = artifact.getAttributes(CoreAttributeTypes.RelationOrder).iterator().next();
      } catch (Exception ex) {
         //do nothing
      }
      return relationOrder;
   }

   public static Artifact getBaselineArtifact(TransactionRecord transactionRecord, Artifact artifact) throws OseeCoreException {
      return ArtifactQuery.getHistoricalArtifactFromId(artifact.getArtId(), transactionRecord,
         DeletionFlag.INCLUDE_DELETED);
   }

   public static String addArtifactGuidBeforeToRelationOrder(String guid, String beforeGuid, String relationOrder) {
      return relationOrder.replace(beforeGuid, beforeGuid + ", " + guid);
   }

   public static String addArtifactGuidAfterToRelationOrder(String guid, String afterGuid, String relationOrder) {
      return relationOrder.replace(afterGuid, guid + ", " + afterGuid);
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

         //         if (chr != ' ') {
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
         //         }
      }

      String returnString = newRelationOrderArray.toString();

      if (returnString.endsWith(",")) {
         returnString = returnString.substring(0, returnString.length() - 1);
      }
      return returnString;
   }

   public static Pair<String, String> getBeforeOrderGuid(String relationOrder, String guid) {
      String beforeGuid = null;
      String afterGuid = null;
      boolean guidFound = false;

      for (String aGuid : relationOrder.split(",")) {
         if (guidFound) {
            afterGuid = aGuid;
            break;
         }

         if (aGuid.equals(guid)) {
            guidFound = true;
         }

         if (!guidFound) {
            beforeGuid = aGuid;
         }
      }
      return new Pair<String, String>(beforeGuid, afterGuid);
   }
}
