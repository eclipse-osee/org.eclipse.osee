/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.relation.order;

import java.util.Collections;
import java.util.List;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactNameComparator;
import org.eclipse.osee.framework.skynet.core.attribute.CoreAttributes;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;

/**
 * @author Andrew M. Finkbeiner
 */
class Lexicographical implements RelationOrder {

   private final ArtifactNameComparator comparator;
   private final RelationOrderId id;

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
   public void applyOrder(Artifact artifact, RelationType type, RelationSide side, List<Artifact> relatives) throws OseeCoreException {
      RelationOrderXmlProcessor relationOrderXmlProcessor = new RelationOrderXmlProcessor(artifact);
      String value = artifact.getSoleAttributeValue(CoreAttributes.RELATION_ORDER.getName(), "");
      String guid = relationOrderXmlProcessor.findRelationOrderGuid(type.getName(), side);
      boolean isTypeToSetDefault = type.getDefaultOrderTypeGuid().equals(getOrderId().getGuid());
      if (guid == null && isTypeToSetDefault) {//nothing has been saved for this type/side pair and it's the default
         return;
      } else if (guid != null && guid.equals(getOrderId().getGuid())) {//already saved as this type
         return;
      } else if (guid != null && isTypeToSetDefault) {//going back to the default type
         relationOrderXmlProcessor.removeOrder(type.getName(), getOrderId(), side);
      }
      List<String> list = Collections.emptyList();
      if (!isTypeToSetDefault) {
         relationOrderXmlProcessor.putOrderList(type.getName(), getOrderId(), side, list);
      }
      if (relationOrderXmlProcessor.hasEntries()) {
         artifact.setSoleAttributeFromString(CoreAttributes.RELATION_ORDER.getName(),
               relationOrderXmlProcessor.getAsXmlString());
      } else if (value != null && value.length() > 0) {
         artifact.deleteAttribute(CoreAttributes.RELATION_ORDER.getName(), value);
      }
   }
}
