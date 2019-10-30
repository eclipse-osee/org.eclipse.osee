/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Ryan D. Brooks
 */
public final class AttributeMultiplicity extends ConcurrentHashMap<AttributeTypeToken, ArtifactTypeAttributeTypeMetaData> {
   private static final long serialVersionUID = 1L;
   private final ArtifactTypeToken artifactType;

   public AttributeMultiplicity(Long id, NamespaceToken namespace, String name, boolean isAbstract, ArtifactTypeToken... superTypes) {
      ArtifactTypeToken artifactType = ArtifactTypeToken.create(id, namespace, name, isAbstract, this, superTypes);
      this.artifactType = artifactType;
   }

   public AttributeMultiplicity any(AttributeTypeGeneric<?> attributeType, String defaultValue) {
      put(attributeType, new ArtifactTypeAttributeTypeMetaData(Multiplicity.ANY, defaultValue));
      return this;
   }

   public AttributeMultiplicity exactlyOne(AttributeTypeGeneric<?> attributeType, String defaultValue) {
      put(attributeType, new ArtifactTypeAttributeTypeMetaData(Multiplicity.EXACTLY_ONE, defaultValue));
      return this;
   }

   public AttributeMultiplicity exactlyOne(AttributeTypeGeneric<?> attributeType, String defaultValue, Long enumTypeId) {
      put(attributeType, new ArtifactTypeAttributeTypeMetaData(Multiplicity.ANY, defaultValue, enumTypeId));
      return this;
   }

   public AttributeMultiplicity zeroOrOne(AttributeTypeGeneric<?> attributeType, String defaultValue) {
      put(attributeType, new ArtifactTypeAttributeTypeMetaData(Multiplicity.ZERO_OR_ONE, defaultValue));
      return this;
   }

   public AttributeMultiplicity zeroOrOne(AttributeTypeGeneric<?> attributeType, String defaultValue, Long enumTypeId) {
      put(attributeType, new ArtifactTypeAttributeTypeMetaData(Multiplicity.ZERO_OR_ONE, defaultValue, enumTypeId));
      return this;
   }

   public AttributeMultiplicity atLeastOne(AttributeTypeGeneric<?> attributeType, String defaultValue, Long enumTypeId) {
      put(attributeType, new ArtifactTypeAttributeTypeMetaData(Multiplicity.AT_LEAST_ONE, defaultValue, enumTypeId));
      return this;
   }

   public AttributeMultiplicity atLeastOne(AttributeTypeGeneric<?> attributeType, String defaultValue) {
      put(attributeType, new ArtifactTypeAttributeTypeMetaData(Multiplicity.AT_LEAST_ONE, defaultValue));
      return this;
   }

   public Integer getMinimum(AttributeTypeToken attributeType) {
      return get(attributeType).getMultiplicity().matches(Multiplicity.ANY, Multiplicity.ZERO_OR_ONE) ? 0 : 1;
   }

   public Integer getMaximum(AttributeTypeToken attributeType) {
      return get(attributeType).getMultiplicity().matches(Multiplicity.EXACTLY_ONE,
         Multiplicity.ZERO_OR_ONE) ? 1 : Integer.MAX_VALUE;
   }

   public ArtifactTypeToken get() {
      return artifactType;
   }
}