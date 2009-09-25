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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.CoreAttributes;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;

/**
 * @author Andrew M. Finkbeiner
 */
class UserDefinedOrder implements RelationOrder {

   @Override
   public void sort(Artifact artifact, RelationType type, RelationSide side, List<Artifact> relatives) throws OseeCoreException {
      if (relatives != null && relatives.size() > 1) {
         Attribute<String> attribute = artifact.getSoleAttribute(CoreAttributes.RELATION_ORDER.getName());
         if (attribute != null) {
            RelationOrderXmlProcessor relationOrderXmlProcessor = new RelationOrderXmlProcessor(attribute.getValue());
            String relationOrderGuid = relationOrderXmlProcessor.findRelationOrderGuid(type.getName(), side);
            if (relationOrderGuid != null) {
               List<String> list = relationOrderXmlProcessor.findOrderList(type.getName(), side, relationOrderGuid);
               if (list != null) {
                  orderRelatives(relatives, list);
               } else {
                  throw new OseeCoreException(
                        String.format(
                              "Unable to find an order list for UserDefinedOrder artifact[%s] RelationType[%s] RelationSide[%s]",
                              artifact.getGuid(), type.getName(), side.name()));
               }
            }
         }
      }
   }

   private void orderRelatives(List<Artifact> relatives, List<String> list) {
      Collections.sort(relatives, new UserDefinedOrderComparator(list));
   }

   @Override
   public RelationOrderId getOrderId() {
      return RelationOrderBaseTypes.USER_DEFINED;
   }

   @Override
   public void applyOrder(Artifact artifact, RelationType type, RelationSide side, List<Artifact> relatives) throws OseeCoreException {
      if (relatives.size() > 0) {
         String value = artifact.getOrInitializeSoleAttributeValue(CoreAttributes.RELATION_ORDER.getName());
         RelationOrderXmlProcessor relationOrderXmlProcessor = new RelationOrderXmlProcessor(value);
         relationOrderXmlProcessor.putOrderList(type.getName(), getOrderId(), side, toGuidList(relatives));
         artifact.setSoleAttributeFromString(CoreAttributes.RELATION_ORDER.getName(),
               relationOrderXmlProcessor.getAsXmlString());
      }
   }

   private List<String> toGuidList(List<Artifact> relatives) {
      List<String> guids = new ArrayList<String>(relatives.size());
      for (Artifact art : relatives) {
         guids.add(art.getGuid());
      }
      return guids;
   }
}
