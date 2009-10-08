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

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.CoreAttributes;
import org.eclipse.osee.framework.skynet.core.types.IArtifact;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactRelationOrderAccessor implements IRelationOrderAccessor {

   private final RelationOrderParser parser;

   public ArtifactRelationOrderAccessor(RelationOrderParser parser) {
      this.parser = parser;
   }

   @Override
   public void load(IArtifact artifact, RelationOrderData orderData) throws OseeCoreException {
      Artifact fullArtifact = artifact.getFullArtifact();
      String value =
            fullArtifact.getSoleAttributeValueAsString(CoreAttributes.RELATION_ORDER.getName(), Strings.emptyString());

      parser.loadFromXml(orderData, value);
   }

   @Override
   public void store(IArtifact artifact, RelationOrderData orderData) throws OseeCoreException {
      Artifact fullArtifact = artifact.getFullArtifact();
      if (orderData.hasEntries() && !fullArtifact.isDeleted()) {
         fullArtifact.setSoleAttributeFromString(CoreAttributes.RELATION_ORDER.getName(), parser.toXml(orderData));
      } else {
         fullArtifact.deleteSoleAttribute(CoreAttributes.RELATION_ORDER.getName());
      }
   }
}
