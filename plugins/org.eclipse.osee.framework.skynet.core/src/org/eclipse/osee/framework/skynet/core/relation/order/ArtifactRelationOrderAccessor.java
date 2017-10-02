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

import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.model.event.DefaultBasicUuidRelationReorder;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactRelationOrderAccessor implements IRelationOrderAccessor {

   private final RelationOrderParser parser;

   public ArtifactRelationOrderAccessor(RelationOrderParser parser) {
      this.parser = parser;
   }

   @Override
   public void load(Artifact artifact, RelationOrderData orderData) {
      String value = artifact.getSoleAttributeValueAsString(CoreAttributeTypes.RelationOrder, Strings.emptyString());
      parser.loadFromXml(orderData, value);
   }

   @Override
   public void store(Artifact artifact, RelationOrderData orderData, DefaultBasicUuidRelationReorder relationOrderRecord) {
      artifact.getRelationOrderRecords().add(relationOrderRecord);
      if (orderData.hasEntries() && !artifact.isDeleted()) {
         artifact.setSoleAttributeFromString(CoreAttributeTypes.RelationOrder, parser.toXml(orderData));
      } else {
         artifact.deleteSoleAttribute(CoreAttributeTypes.RelationOrder);
      }
   }
}
