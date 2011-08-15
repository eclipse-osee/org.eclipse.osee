/*
 * Created on Aug 11, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.replace;

import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;

/**
 * @author Jeff C. Phillips
 */
public class AttributeOrderHandlerContainedGuid {

   public String handleAttributeOrder(Artifact artifact, RelationLink link) throws OseeCoreException {
      String relationOrderString = null;
      Artifact otherSideArtifact = link.getArtifactOnOtherSide(artifact);

      if (otherSideArtifact != null && otherSideArtifact.getAttributeCount(CoreAttributeTypes.RelationOrder) > 0) {
         String otherSideRelationOrderString = ReplaceUtil.getRelationOrder(otherSideArtifact).getDisplayableString();
         relationOrderString =
            ReplaceUtil.removeArtifactGuidFromRelationOrder(artifact.getGuid(), otherSideRelationOrderString);
      }
      return relationOrderString;
   }
}
