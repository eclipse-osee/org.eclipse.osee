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

package org.eclipse.osee.framework.core.model.test.mocks;

import java.util.Date;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.RelationOrderBaseTypes;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.DefaultBasicArtifact;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.core.model.OseeCachingService;
import org.eclipse.osee.framework.core.model.OseeEnumEntry;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.cache.ArtifactTypeCache;
import org.eclipse.osee.framework.core.model.cache.AttributeTypeCache;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.cache.OseeEnumTypeCache;
import org.eclipse.osee.framework.core.model.cache.RelationTypeCache;
import org.eclipse.osee.framework.core.model.cache.TransactionCache;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.core.model.type.OseeEnumType;
import org.eclipse.osee.framework.core.model.type.OseeEnumTypeFactory;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.services.IOseeCachingServiceProvider;
import org.eclipse.osee.framework.jdk.core.util.GUID;

/**
 * @author Roberto E. Escobar
 */
public final class MockDataFactory {

   private MockDataFactory() {
   }

   public static IBasicArtifact<?> createArtifact(int index) {
      return new DefaultBasicArtifact(index * 37, GUID.create(), "user_" + index);
   }

   public static AttributeType createAttributeType() throws OseeCoreException {
      OseeEnumTypeFactory oseeEnumTypeFactory = new OseeEnumTypeFactory();
      AttributeType attributeType =
         new AttributeType(GUID.create(), "name", "baseType", "providerName", ".xml", "", 1, 1, "description", "tagger");
      attributeType.setOseeEnumType(oseeEnumTypeFactory.createEnumType(GUID.create(), "enum type name"));
      return attributeType;
   }

   public static Branch createBranch(int index) {
      BranchState branchState = BranchState.values()[Math.abs(index % BranchState.values().length)];
      BranchType branchType = BranchType.values()[Math.abs(index % BranchType.values().length)];
      boolean isArchived = index % 2 == 0 ? true : false;
      return new Branch(GUID.create(), "branch_" + index, branchType, branchState, isArchived);
   }

   public static TransactionRecord createTransaction(int index, int branchId) {
      TransactionDetailsType type =
         TransactionDetailsType.values()[Math.abs(index % TransactionDetailsType.values().length)];
      int value = index;
      if (value == 0) {
         value++;
      }
      return new TransactionRecord(value * 47, branchId, "comment_" + value, new Date(), value * 37, value * 42, type);
   }

   public static OseeEnumEntry createEnumEntry(int index) {
      return new OseeEnumEntry(GUID.create(), "entry_" + index, Math.abs(index * 37));
   }

   public static OseeEnumType createEnumType(int index) {
      return new OseeEnumType(GUID.create(), "enum_" + index);
   }

   public static AttributeType createAttributeType(int index, OseeEnumType oseeEnumType) throws OseeCoreException {
      AttributeType type =
         new AttributeType(GUID.create(), "attrType_" + index, "baseClass_" + index, "providerId_" + index,
            "ext_" + index, "default_" + index, index * 2, index * 7, "description_" + index, "tag_" + index);
      type.setOseeEnumType(oseeEnumType);
      return type;
   }

   public static ArtifactType createArtifactType(int index) {
      return new ArtifactType(GUID.create(), "art_" + index, index % 2 == 0);
   }

   public static ArtifactType createBaseArtifactType() {
      IArtifactType baseType = CoreArtifactTypes.Artifact;
      return new ArtifactType(baseType.getGuid(), baseType.getName(), true);
   }

   public static RelationType createRelationType(int index, ArtifactType artTypeA, ArtifactType artTypeB) {
      RelationTypeMultiplicity multiplicity =
         RelationTypeMultiplicity.values()[Math.abs(index % RelationTypeMultiplicity.values().length)];
      String order = RelationOrderBaseTypes.values[index % RelationTypeMultiplicity.values().length].getGuid();
      return new RelationType(GUID.create(), "relType_" + index, "sideA_" + index, "sideB_" + index, artTypeA,
         artTypeB, multiplicity, order);
   }

   public static IOseeCachingServiceProvider createCachingProvider() {
      BranchCache brCache = new BranchCache(new MockOseeDataAccessor<Branch>());
      TransactionCache txCache = new TransactionCache();
      txCache.setAccessor(new MockOseeTransactionDataAccessor());
      ArtifactTypeCache artCache = new ArtifactTypeCache(new MockOseeDataAccessor<ArtifactType>());
      AttributeTypeCache attrCache = new AttributeTypeCache(new MockOseeDataAccessor<AttributeType>());
      RelationTypeCache relCache = new RelationTypeCache(new MockOseeDataAccessor<RelationType>());
      OseeEnumTypeCache enumCache = new OseeEnumTypeCache(new MockOseeDataAccessor<OseeEnumType>());

      IOseeCachingService service = new OseeCachingService(brCache, txCache, artCache, attrCache, relCache, enumCache);
      return new MockOseeCachingServiceProvider(service);
   }

}
