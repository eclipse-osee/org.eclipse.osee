/*********************************************************************
 * Copyright (c) 2019 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.core.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.osee.framework.core.enums.EnumToken;
import org.eclipse.osee.framework.core.enums.OseeImage;

/**
 * @author Ryan D. Brooks
 */
public final class AttributeMultiplicity extends ConcurrentHashMap<AttributeTypeToken, ArtifactTypeAttributeTypeMetaData<?>> {
   private static final long serialVersionUID = 1L;
   private final ArtifactTypeToken artifactType;
   private final List<ComputedCharacteristicToken<?>> computedCharacteristics = new ArrayList<>();

   public AttributeMultiplicity(Long id, NamespaceToken namespace, String name, boolean isAbstract, OseeImage image, List<ArtifactTypeToken> superTypes) {
      ArtifactTypeToken artifactType =
         ArtifactTypeToken.create(id, namespace, name, isAbstract, this, image, superTypes);
      this.artifactType = artifactType;
   }

   public AttributeMultiplicity(Long id, NamespaceToken namespace, String name, boolean isAbstract, List<ArtifactTypeToken> superTypes) {
      ArtifactTypeToken artifactType = ArtifactTypeToken.create(id, namespace, name, isAbstract, this, superTypes);
      this.artifactType = artifactType;
   }

   public AttributeMultiplicity(Long id, NamespaceToken namespace, String name, boolean isAbstract, OseeImage image, ArtifactTypeToken... superTypes) {
      this(id, namespace, name, isAbstract, image, Arrays.asList(superTypes));
   }

   public AttributeMultiplicity(Long id, NamespaceToken namespace, String name, boolean isAbstract, ArtifactTypeToken... superTypes) {
      ArtifactTypeToken artifactType =
         ArtifactTypeToken.create(id, namespace, name, isAbstract, this, Arrays.asList(superTypes));
      this.artifactType = artifactType;
   }

   /**
    * 0..n values
    */
   public <T> AttributeMultiplicity any(AttributeTypeGeneric<T> attributeType, T defaultValue, String description) {
      put(attributeType, new ArtifactTypeAttributeTypeMetaData<T>(Multiplicity.ANY, defaultValue));
      return this;
   }

   /**
    * 0..n values
    */
   public <T> AttributeMultiplicity any(AttributeTypeGeneric<T> attributeType, T defaultValue) {
      return any(attributeType, defaultValue, "");
   }

   /**
    * 0..n values
    */
   public <T> AttributeMultiplicity any(AttributeTypeGeneric<T> attributeType) {
      return any(attributeType, attributeType.getBaseAttributeTypeDefaultValue());
   }

   public <T> AttributeMultiplicity exactlyOne(AttributeTypeGeneric<T> attributeType, T defaultValue, String description) {
      put(attributeType, new ArtifactTypeAttributeTypeMetaData<T>(Multiplicity.EXACTLY_ONE, defaultValue));
      return this;
   }

   public <T> AttributeMultiplicity exactlyOne(AttributeTypeGeneric<T> attributeType, T defaultValue) {
      return exactlyOne(attributeType, defaultValue, "");
   }

   public <T> AttributeMultiplicity exactlyOne(AttributeTypeGeneric<T> attributeType) {
      return exactlyOne(attributeType, attributeType.getBaseAttributeTypeDefaultValue());
   }

   public <T> AttributeMultiplicity zeroOrOne(AttributeTypeGeneric<T> attributeType, T defaultValue) {
      return zeroOrOne(attributeType, defaultValue, "");
   }

   public <T> AttributeMultiplicity zeroOrOne(AttributeTypeGeneric<T> attributeType, T defaultValue, String description) {
      put(attributeType, new ArtifactTypeAttributeTypeMetaData<T>(Multiplicity.ZERO_OR_ONE, defaultValue));
      return this;
   }

   public <T> AttributeMultiplicity zeroOrOne(AttributeTypeGeneric<T> attributeType) {
      return zeroOrOne(attributeType, attributeType.getBaseAttributeTypeDefaultValue());
   }

   public <T> AttributeMultiplicity atLeastOne(AttributeTypeGeneric<T> attributeType, T defaultValue, String description) {
      put(attributeType, new ArtifactTypeAttributeTypeMetaData<T>(Multiplicity.AT_LEAST_ONE, defaultValue));
      return this;
   }

   public <T> AttributeMultiplicity atLeastOne(AttributeTypeGeneric<T> attributeType, T defaultValue) {
      return atLeastOne(attributeType, defaultValue, "");
   }

   public <T> AttributeMultiplicity atLeastOne(AttributeTypeGeneric<T> attributeType) {
      return atLeastOne(attributeType, attributeType.getBaseAttributeTypeDefaultValue());
   }

   /**
    * 0..n values
    */
   public <T extends EnumToken> AttributeMultiplicity any(AttributeTypeEnum<T> attributeType, T defaultValue, String[] enumeratedValues) {
      put(attributeType, new ArtifactTypeAttributeTypeMetaData<T>(Multiplicity.ANY, defaultValue, enumeratedValues));
      return this;
   }

   public <T extends EnumToken> AttributeMultiplicity exactlyOne(AttributeTypeEnum<T> attributeType, T defaultValue, String[] enumeratedValues) {
      put(attributeType,
         new ArtifactTypeAttributeTypeMetaData<T>(Multiplicity.EXACTLY_ONE, defaultValue, enumeratedValues));
      return this;
   }

   public <T extends EnumToken> AttributeMultiplicity zeroOrOne(AttributeTypeEnum<T> attributeType, T defaultValue, String[] enumeratedValues) {
      put(attributeType,
         new ArtifactTypeAttributeTypeMetaData<T>(Multiplicity.ZERO_OR_ONE, defaultValue, enumeratedValues));
      return this;
   }

   public <T extends EnumToken> AttributeMultiplicity atLeastOne(AttributeTypeEnum<T> attributeType, T defaultValue, String[] enumeratedValues) {
      put(attributeType,
         new ArtifactTypeAttributeTypeMetaData<T>(Multiplicity.AT_LEAST_ONE, defaultValue, enumeratedValues));
      return this;
   }

   public <T> AttributeMultiplicity computed(ComputedCharacteristic<T> computedCharacteristic) {
      computedCharacteristics.add(computedCharacteristic);
      return this;
   }

   public List<ComputedCharacteristicToken<?>> getComputedCharacteristics() {
      return computedCharacteristics;
   }

   public Integer getMinimum(AttributeTypeToken attributeType) {
      return getMultiplicity(attributeType).matches(Multiplicity.ANY, Multiplicity.ZERO_OR_ONE) ? 0 : 1;
   }

   public Integer getMaximum(AttributeTypeToken attributeType) {
      return getMultiplicity(attributeType).matches(Multiplicity.EXACTLY_ONE,
         Multiplicity.ZERO_OR_ONE) ? 1 : Integer.MAX_VALUE;
   }

   public Multiplicity getMultiplicity(AttributeTypeToken attributeType) {
      ArtifactTypeAttributeTypeMetaData<?> typeMetaData = get(attributeType);
      if (typeMetaData == null) {
         return Multiplicity.SENTINEL;
      }
      return typeMetaData.getMultiplicity();
   }

   @SuppressWarnings("unchecked")
   public <T> T getAttributeDefault(AttributeTypeGeneric<T> attributeType) {
      try {
         return (T) get(attributeType).getDefaultValue();
      } catch (Exception ex) {
         return attributeType.getBaseAttributeTypeDefaultValue();
      }
   }

   public ArtifactTypeToken get() {
      return artifactType;
   }

   public <T extends EnumToken> List<T> getValidEnumValues(AttributeTypeEnum<T> attributeType) {
      List<T> validEnumTokens = new ArrayList<T>();
      try {
         for (T enumToken : attributeType.getEnumValues()) {
            //get(attributeType) will fail on some attributes for overridden artifact types. Hence the try/catch.
            for (String enumeratedValue : get(attributeType).getValidEnumValues()) {
               if (enumToken.getName().equals(enumeratedValue)) {
                  validEnumTokens.add(enumToken);
               }
            }
         }
      } catch (Exception e) {
         //TODO: Catch should be removed/replaced with error when overridden type handling is updated
         validEnumTokens.addAll(attributeType.getEnumValues());
      }
      if (validEnumTokens.isEmpty()) {
         validEnumTokens.addAll(attributeType.getEnumValues());
      }
      return validEnumTokens;
   }

   public List<AttributeTypeToken> getValidAttributeTypes() {
      List<AttributeTypeToken> attributeTypes = new ArrayList<AttributeTypeToken>(size());
      forEachKey(50000, attributeTypes::add);
      return attributeTypes;
   }

   public void getSingletonAttributeTypes(Set<AttributeTypeToken> attributeTypeTokens) {

      for (Map.Entry<AttributeTypeToken, ArtifactTypeAttributeTypeMetaData<?>> entry : entrySet()) {
         if (entry.getValue().getMultiplicity().matches(Multiplicity.EXACTLY_ONE, Multiplicity.ZERO_OR_ONE)) {
            attributeTypeTokens.add(entry.getKey());
         }
      }
   }
}