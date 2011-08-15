/*
 * Created on Aug 11, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.replace;

import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;

/**
 * @author Jeff C. Phillips
 */
public class AttributeOrderHandlerNotContainedGuid {

   public String handleAttributeOrder(Artifact artifact, RelationLink link, TransactionRecord baselineTx) throws OseeCoreException {
      String relationOrderString = null;
      Artifact otherSideArtifact = link.getArtifactOnOtherSide(artifact);

      if (otherSideArtifact != null) {
         Artifact baselineOthersideArtifact = ReplaceUtil.getBaselineArtifact(baselineTx, otherSideArtifact);

         //If there is a relation order
         if (baselineOthersideArtifact.getAttributeCount(CoreAttributeTypes.RelationOrder) > 0) {
            String otherSideRelationOrderString =
               ReplaceUtil.getRelationOrder(baselineOthersideArtifact).getDisplayableString();

            if (otherSideRelationOrderString.contains(artifact.getGuid())) {
               Pair<String, String> firstAndLastGuid =
                  ReplaceUtil.getBeforeOrderGuid(otherSideRelationOrderString, artifact.getGuid());

               int findGuidIndex = otherSideRelationOrderString.indexOf(artifact.getGuid());
               int findBeforeGuidIndex = otherSideRelationOrderString.indexOf(firstAndLastGuid.getFirst());

               //if baseline relation order exists
               if (findGuidIndex > 0) {
                  //existed
                  if (findBeforeGuidIndex < findGuidIndex) {
                     //add it before if it exists
                     relationOrderString =
                        ReplaceUtil.addArtifactGuidBeforeToRelationOrder(artifact.getArtifactTypeName(),
                           firstAndLastGuid.getFirst(), otherSideRelationOrderString);
                  } else {
                     //add it afterwards if it exists
                     relationOrderString =
                        ReplaceUtil.addArtifactGuidAfterToRelationOrder(artifact.getArtifactTypeName(),
                           firstAndLastGuid.getSecond(), otherSideRelationOrderString);
                  }
               } else {
                  relationOrderString = otherSideRelationOrderString.concat(", " + artifact.getGuid());
                  //add to the bottom of the list
               }
            }
         }
      }
      return relationOrderString;
   }

}
