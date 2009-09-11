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
package org.eclipse.osee.framework.skynet.core.types;

import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.factory.ArtifactFactoryManager;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.OseeEnumEntry;
import org.eclipse.osee.framework.skynet.core.attribute.OseeEnumType;
import org.eclipse.osee.framework.skynet.core.attribute.providers.IAttributeDataProvider;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;

/**
 * @author Roberto E. Escobar
 */
public class OseeTypeFactory implements IOseeTypeFactory {
   private final ArtifactFactoryManager factoryManager;

   public OseeTypeFactory() {
      this.factoryManager = new ArtifactFactoryManager();
   }

   private String createGuidIfNeeded(String guid) {
      return guid == null ? GUID.create() : guid;
   }

   @Override
   public ArtifactType createArtifactType(String guid, boolean isAbstract, String name, OseeTypeCache oseeTypeCache) throws OseeCoreException {
      if (!Strings.isValid(name)) {
         throw new OseeArgumentException("name cannot be null.");
      }
      return new ArtifactType(createGuidIfNeeded(guid), name, isAbstract, factoryManager,
            oseeTypeCache.getArtifactTypeCache());
   }

   @Override
   public AttributeType createAttributeType(String guid, String name, String baseAttributeTypeId, String attributeProviderNameId, Class<? extends Attribute<?>> baseAttributeClass, Class<? extends IAttributeDataProvider> providerAttributeClass, String fileTypeExtension, String defaultValue, OseeEnumType oseeEnumType, int minOccurrences, int maxOccurrences, String tipText, String taggerId) throws OseeCoreException {
      if (!Strings.isValid(name)) {
         throw new OseeArgumentException("name cannot be null.");
      }
      if (baseAttributeClass == null) {
         throw new OseeArgumentException("The baseAttributeClass can not be null or empty");
      }
      if (providerAttributeClass == null) {
         throw new OseeArgumentException("The providerAttributeClass can not be null or empty");
      }
      if (!Strings.isValid(baseAttributeTypeId)) {
         throw new OseeArgumentException("The baseAttributeTypeId can not be null or empty");
      }
      if (!Strings.isValid(attributeProviderNameId)) {
         throw new OseeArgumentException("The attributeProviderNameId can not be null or empty");
      }
      if (minOccurrences > 0 && defaultValue == null) {
         throw new OseeArgumentException(
               "DefaultValue must be set for attribute [" + name + "] with minOccurrences " + minOccurrences);
      }
      if (minOccurrences < 0) {
         throw new OseeArgumentException("minOccurrences must be greater than or equal to zero");
      }
      if (maxOccurrences < minOccurrences) {
         throw new OseeArgumentException("maxOccurences can not be less than minOccurences");
      }
      return new AttributeType(createGuidIfNeeded(guid), name, baseAttributeTypeId, attributeProviderNameId,
            baseAttributeClass, providerAttributeClass, fileTypeExtension, defaultValue, oseeEnumType, minOccurrences,
            maxOccurrences, tipText, taggerId);
   }

   @Override
   public RelationType createRelationType(String guid, String name, String sideAName, String sideBName, ArtifactType artifactTypeSideA, ArtifactType artifactTypeSideB, RelationTypeMultiplicity multiplicity, boolean isUserOrdered, String defaultOrderTypeGuid) throws OseeCoreException {
      if (!Strings.isValid(name)) {
         throw new OseeArgumentException("name cannot be null.");
      }
      if (!Strings.isValid(sideAName)) {
         throw new OseeArgumentException("The sideAName can not be null or empty");
      }
      if (!Strings.isValid(sideBName)) {
         throw new OseeArgumentException("The sideBName can not be null or empty");
      }
      if (artifactTypeSideA == null) {
         throw new OseeArgumentException("The artifactTypeSideA can not be null or empty");
      }
      if (artifactTypeSideB == null) {
         throw new OseeArgumentException("The artifactTypeSideB can not be null or empty");
      }
      if (multiplicity == null) {
         throw new OseeArgumentException("Multiplicity can not be null or empty");
      }
      return new RelationType(createGuidIfNeeded(guid), name, sideAName, sideBName, artifactTypeSideA,
            artifactTypeSideB, multiplicity, isUserOrdered, defaultOrderTypeGuid);
   }

   @Override
   public OseeEnumType createEnumType(String guid, String name, OseeTypeCache oseeTypeCache) throws OseeCoreException {
      if (!Strings.isValid(name)) {
         throw new OseeArgumentException("name cannot be null.");
      }
      return new OseeEnumType(createGuidIfNeeded(guid), name, oseeTypeCache);
   }

   @Override
   public OseeEnumEntry createEnumEntry(String guid, String name, int ordinal, OseeTypeCache oseeTypeCache) throws OseeCoreException {
      if (!Strings.isValid(name)) {
         throw new OseeArgumentException("name cannot be null.");
      }
      if (ordinal < 0) {
         throw new OseeArgumentException("ordinal must be greater than or equal to zero");
      }
      return new OseeEnumEntry(createGuidIfNeeded(guid), name, ordinal, oseeTypeCache);
   }
}
