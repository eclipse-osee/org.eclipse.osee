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
import org.eclipse.osee.framework.skynet.core.attribute.CoreAttributes;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;

/**
 * @author Andrew M. Finkbeiner
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
   public void applyOrder(Artifact artifact, RelationType type, RelationSide side, List<Artifact> relatives) throws OseeCoreException {
      if (!type.getDefaultOrderTypeGuid().equals(getOrderId().getGuid())) {
         String value = artifact.getOrInitializeSoleAttributeValue(CoreAttributes.RELATION_ORDER.getName());
         RelationOrderXmlProcessor relationOrderXmlProcessor = new RelationOrderXmlProcessor(value);
         List<String> list = Collections.emptyList();
         relationOrderXmlProcessor.putOrderList(type.getName(), getOrderId(), side, list);
         artifact.setSoleAttributeFromString(CoreAttributes.RELATION_ORDER.getName(),
               relationOrderXmlProcessor.getAsXmlString());
      }
   }

}
