/*
 * Created on Aug 12, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.relation.order;

import java.util.Collections;
import java.util.List;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;

/**
 * @author Andrew M. Finkbeiner
 *
 */
class Unordered implements RelationOrder {

   @Override
   public void sort(Artifact artifact, RelationType type, RelationSide side, List<Artifact> relatives) {
   }

   @Override
   public RelationOrderId getOrderId() {
      return RelationOrderBaseTypes.UNORDERED;
   }

   @Override
   public void setOrder(Artifact artifact, RelationType type, RelationSide side, List<Artifact> relatives) throws OseeCoreException {
      if (!type.getDefaultOrderTypeGuid().equals(getOrderId().getGuid())) {
         String value = artifact.getOrInitializeSoleAttributeValue("Relation Order");
         RelationOrderXmlProcessor relationOrderXmlProcessor = new RelationOrderXmlProcessor(value);
         List<String> list = Collections.emptyList();
         relationOrderXmlProcessor.putOrderList(type.getTypeName(), getOrderId(), side, list);
         artifact.setSoleAttributeFromString("Relation Order", relationOrderXmlProcessor.getAsXmlString());
      }
   }

}
