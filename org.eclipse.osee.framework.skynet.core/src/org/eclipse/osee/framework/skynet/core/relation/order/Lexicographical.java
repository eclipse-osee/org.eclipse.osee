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
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactNameComparator;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;

/**
 * @author Andrew M. Finkbeiner
 */
class Lexicographical implements RelationOrder {

   private ArtifactNameComparator comparator;
   private RelationOrderId id;

   Lexicographical(boolean descending, RelationOrderId id) {
      comparator = new ArtifactNameComparator(descending);
      this.id = id;
   }

   @Override
   public void sort(Artifact artifact, RelationType type, RelationSide side, List<Artifact> relatives) {
      Collections.sort(relatives, comparator);
   }

   @Override
   public RelationOrderId getOrderId() {
      return id;
   }

   @Override
   public void setOrder(Artifact artifact, RelationType type, RelationSide side, List<Artifact> relatives) throws OseeCoreException {
      String value = artifact.getOrInitializeSoleAttributeValue("Relation Order");
      RelationOrderXmlProcessor relationOrderXmlProcessor = new RelationOrderXmlProcessor(value);
      String guid = relationOrderXmlProcessor.findRelationOrderGuid(type.getTypeName(), side);
      boolean isTypeToSetDefault = type.getDefaultOrderTypeGuid().equals(getOrderId().getGuid());
      if(guid == null && isTypeToSetDefault){//nothing has been saved for this type/side pair and it's the default
         return;
      } else if(guid != null && guid.equals(getOrderId().getGuid())){//already saved as this type
         return;
      } else if(guid != null && isTypeToSetDefault){//going back to the default type
         relationOrderXmlProcessor.removeOrder(type.getTypeName(), getOrderId(), side);
      }
      List<String> list = Collections.emptyList();
      if(!isTypeToSetDefault){
         relationOrderXmlProcessor.putOrderList(type.getTypeName(), getOrderId(), side, list);
      }
      if(relationOrderXmlProcessor.hasEntries()){
         artifact.setSoleAttributeFromString("Relation Order", relationOrderXmlProcessor.getAsXmlString());
      } else if (value != null && value.length() > 0){
         artifact.deleteAttribute("Relation Order", value);
      }
   }
}
