/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.util.validate;

import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.model.type.ArtifactType;

/**
 * @author Donald G. Dunne
 */
public final class AttributeSetRule {
   private final IArtifactType baseArtifactType;
   private final IAttributeType attributeType;
   private final Integer minimumValues;
   private final String invalidValue;

   public AttributeSetRule(IArtifactType artifactType, IAttributeType attributeType, Integer minimumValues, String invalidValue) {
      this.baseArtifactType = artifactType;
      this.attributeType = attributeType;
      this.minimumValues = minimumValues;
      this.invalidValue = invalidValue;
   }

   public boolean hasArtifactType(ArtifactType artifactType) {
      return artifactType.inheritsFrom(baseArtifactType);
   }

   public IAttributeType getAttributeName() {
      return attributeType;
   }

   public Integer getMinimumValues() {
      return minimumValues;
   }

   public String getInvalidValue() {
      return invalidValue;
   }

   @Override
   public String toString() {
      return "For \"" + baseArtifactType + "\", ensure \"" + attributeType + "\" attribute has at least " + minimumValues + " value(s)" + (invalidValue != null ? " and does NOT have \"" + invalidValue + "\" values" : "");
   }
}