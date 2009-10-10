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

import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
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

   private void checkName(AbstractOseeCache<?> cache, String typeName) throws OseeCoreException {
      if (!Strings.isValid(typeName)) {
         throw new OseeArgumentException("name cannot be null.");
      }
   }

   private void checkNameUnique(AbstractOseeCache<?> cache, String typeName) throws OseeCoreException {
      Object object = cache.getUniqueByName(typeName);
      if (object != null) {
         throw new OseeArgumentException(String.format("Item matching name [%s] already exists", typeName));
      }
   }

   @Override
   public ArtifactType createArtifactType(AbstractOseeCache<ArtifactType> cache, String guid, boolean isAbstract, String name) throws OseeCoreException {
      checkName(cache, name);
      checkNameUnique(cache, name);
      return new ArtifactType(cache, createGuidIfNeeded(guid), name, isAbstract, factoryManager);
   }

   @Override
   public AttributeType createAttributeType(AbstractOseeCache<AttributeType> cache, String guid, String name, String baseAttributeTypeId, String attributeProviderNameId, Class<? extends Attribute<?>> baseAttributeClass, Class<? extends IAttributeDataProvider> providerAttributeClass, String fileTypeExtension, String defaultValue, OseeEnumType oseeEnumType, int minOccurrences, int maxOccurrences, String tipText, String taggerId) throws OseeCoreException {
      checkName(cache, name);
      checkNameUnique(cache, name);
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
      return new AttributeType(cache, createGuidIfNeeded(guid), name, baseAttributeTypeId, attributeProviderNameId,
            baseAttributeClass, providerAttributeClass, fileTypeExtension, defaultValue, oseeEnumType, minOccurrences,
            maxOccurrences, tipText, taggerId);
   }

   @Override
   public RelationType createRelationType(AbstractOseeCache<RelationType> cache, String guid, String name, String sideAName, String sideBName, ArtifactType artifactTypeSideA, ArtifactType artifactTypeSideB, RelationTypeMultiplicity multiplicity, String defaultOrderTypeGuid) throws OseeCoreException {
      checkName(cache, name);
      checkNameUnique(cache, name);
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
      return new RelationType(cache, createGuidIfNeeded(guid), name, sideAName, sideBName, artifactTypeSideA,
            artifactTypeSideB, multiplicity, defaultOrderTypeGuid);
   }

   @Override
   public OseeEnumType createEnumType(AbstractOseeCache<OseeEnumType> cache, String guid, String name) throws OseeCoreException {
      checkName(cache, name);
      checkNameUnique(cache, name);
      return new OseeEnumType(cache, createGuidIfNeeded(guid), name);
   }

   @Override
   public OseeEnumEntry createEnumEntry(AbstractOseeCache<OseeEnumType> cache, String guid, String name, int ordinal) throws OseeCoreException {
      checkName(cache, name);
      if (ordinal < 0) {
         throw new OseeArgumentException("ordinal must be greater than or equal to zero");
      }
      return new OseeEnumEntry(cache, createGuidIfNeeded(guid), name, ordinal);
   }

   @Override
   public Branch createBranch(AbstractOseeCache<Branch> cache, String guid, String name, BranchType branchType, BranchState branchState, boolean isArchived) throws OseeCoreException {
      checkName(cache, name);
      if (branchType == null) {
         throw new OseeArgumentException("branchType cannot be null.");
      }
      if (branchState == null) {
         throw new OseeArgumentException("branchState cannot be null.");
      }
      return new Branch(cache, createGuidIfNeeded(guid), name, branchType, branchState, isArchived);
   }
}
