/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.orcs.db.internal.loader.data;

import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchCategoryToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.RelationId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.data.RelationalConstants;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.Tuple2Type;
import org.eclipse.osee.framework.core.data.Tuple3Type;
import org.eclipse.osee.framework.core.data.Tuple4Type;
import org.eclipse.osee.framework.core.data.TupleTypeId;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.ArtifactDataImpl;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.BranchCategoryData;
import org.eclipse.osee.orcs.core.ds.DataProxy;
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.core.ds.TupleData;
import org.eclipse.osee.orcs.core.ds.VersionData;
import org.eclipse.osee.orcs.core.ds.VersionDataImpl;
import org.eclipse.osee.orcs.db.internal.OrcsObjectFactory;
import org.eclipse.osee.orcs.db.internal.proxy.AttributeDataProxyFactory;

/**
 * @author Roberto E. Escobar
 */
public class OrcsObjectFactoryImpl implements OrcsObjectFactory {
   private final AttributeDataProxyFactory proxyFactory;
   private final OrcsTokenService tokenService;

   public OrcsObjectFactoryImpl(AttributeDataProxyFactory proxyFactory, OrcsTokenService tokenService) {
      this.proxyFactory = proxyFactory;
      this.tokenService = tokenService;
   }

   @Override
   public VersionData createVersion(BranchId branchId, TransactionId txId, GammaId gamma, boolean historical) {
      return createVersion(branchId, txId, gamma, historical, TransactionId.SENTINEL);
   }

   @Override
   public VersionData createDefaultVersionData() {
      // @formatter:off
      return createVersion(
         BranchId.SENTINEL,
         TransactionId.SENTINEL,
         GammaId.SENTINEL,
         RelationalConstants.IS_HISTORICAL_DEFAULT,
         TransactionId.SENTINEL);
      // @formatter:on
   }

   @Override
   public VersionData createCopy(VersionData other) {
      // @formatter:off
      return createVersion(
         other.getBranch(),
         other.getTransactionId(),
         other.getGammaId(),
         other.isHistorical(),
         other.getStripeId());
      // @formatter:on
   }

   private VersionData createVersion(BranchId branchId, TransactionId txId, GammaId gamma, boolean historical,
      TransactionId stripeId) {
      VersionData version = new VersionDataImpl();
      version.setBranch(branchId);
      version.setTransactionId(txId);
      version.setGammaId(gamma);
      version.setHistorical(historical);
      version.setStripeId(stripeId);
      return version;
   }

   @Override
   public ArtifactData createArtifactData(VersionData version, ArtifactId artifactId, ArtifactTypeToken artifactType,
      ModificationType modType, String guidToSet, ApplicabilityId applicId) {
      return createArtifactFromRow(version, artifactId, artifactType, modType, artifactType, modType, guidToSet,
         applicId);
   }

   @Override
   public ArtifactData createCopy(ArtifactData source) {
      VersionData newVersion = createCopy(source.getVersion());
      return createArtifactFromRow(newVersion, source, source.getType(), source.getModType(), source.getBaseType(),
         source.getBaseModType(), source.getGuid(), source.getApplicabilityId());
   }

   @Override
   public <T> AttributeData<T> createAttributeData(VersionData version, AttributeId id,
      AttributeTypeGeneric<?> attributeType, ModificationType modType, ArtifactId artifactId, T value, String uri,
      ApplicabilityId applicId) {
      DataProxy<T> proxy = proxyFactory.createProxy(attributeType, value, uri);
      return createAttributeFromRow(version, id, attributeType, modType, attributeType, modType, artifactId, proxy,
         applicId);
   }

   @Override
   public <T> AttributeData<T> createCopy(AttributeData<T> source) {
      VersionData newVersion = createCopy(source.getVersion());
      AttributeTypeGeneric<?> attributeType = tokenService.getAttributeType(source.getType().getId());
      DataProxy<T> sourceProxy = source.getDataProxy();
      DataProxy<T> newProxy = proxyFactory.createProxy(attributeType, sourceProxy.getRawValue(), sourceProxy.getUri());
      return createAttributeFromRow(newVersion, source, attributeType, source.getModType(), source.getBaseType(),
         source.getBaseModType(), source.getArtifactId(), newProxy, source.getApplicabilityId());
   }

   @Override
   public <T> AttributeData<T> createAttributeData(VersionData version, AttributeId generateAttId,
      AttributeTypeGeneric<?> attributeType, ModificationType modType, ArtifactId artId, ApplicabilityId applicId) {
      DataProxy<T> proxy = proxyFactory.createProxy(attributeType, "", "");
      return createAttributeFromRow(version, generateAttId, attributeType, modType, attributeType, modType, artId,
         proxy, applicId);
   }

   @Override
   public RelationData createRelationData(VersionData version, RelationId id, RelationTypeToken relationType,
      ModificationType modType, ArtifactId aArtId, ArtifactId bArtId, String rationale, ApplicabilityId applicId) {
      return createRelationData(version, id, relationType, modType, relationType, modType, aArtId, bArtId,
         ArtifactId.SENTINEL, 0, rationale, applicId);
   }

   @Override
   public RelationData createRelationData(VersionData version, RelationId id, RelationTypeToken relationType,
      ModificationType modType, ArtifactId aArtId, ArtifactId bArtId, ArtifactId relArtId, int relOrder,
      ApplicabilityId applicId) {
      return createRelationData(version, id, relationType, modType, relationType, modType, aArtId, bArtId, relArtId,
         relOrder, "", applicId);
   }

   private ArtifactData createArtifactFromRow(VersionData version, ArtifactId artifactId,
      ArtifactTypeToken artifactType, ModificationType modType, ArtifactTypeToken baseArtifactType,
      ModificationType baseModType, String guid, ApplicabilityId applicId) {
      ArtifactData data = new ArtifactDataImpl(version);
      data.setLocalId(artifactId);
      data.setType(artifactType);
      data.setBaseType(baseArtifactType);
      data.setModType(modType);
      data.setBaseModType(baseModType);
      data.setGuid(guid);
      data.setApplicabilityId(applicId);
      return data;
   }

   private <T> AttributeData<T> createAttributeFromRow(VersionData version, AttributeId id,
      AttributeTypeGeneric<?> attributeType, ModificationType modType, AttributeTypeToken baseAttributeType,
      ModificationType baseModType, ArtifactId artifactId, DataProxy<T> proxy, ApplicabilityId applicId) {
      AttributeData<T> data = new AttributeDataImpl<>(version);
      data.setLocalId(id);
      data.setType(attributeType);
      data.setBaseType(baseAttributeType);
      data.setModType(modType);
      data.setBaseModType(baseModType);
      data.setArtifactId(artifactId);
      data.setDataProxy(proxy);
      data.setApplicabilityId(applicId);
      return data;
   }

   private RelationData createRelationData(VersionData version, RelationId id, RelationTypeToken relationType,
      ModificationType modType, RelationTypeToken baseRelationType, ModificationType baseModType, ArtifactId aArtId,
      ArtifactId bArtId, ArtifactId relArtId, int relOrder, String rationale, ApplicabilityId applicId) {
      RelationData data = new RelationDataImpl(version);
      if (relationType.isNewRelationTable()) {
         if (version.getGammaId().isValid()) {
            data.setLocalId(version.getGammaId()); //use this when creating many new relations to the same artA
                                                   //currently relations are stored using a hash with the local id as a key so it needs to be
                                                   //something unique and valid if gamma_id isn't valid use the id which is unique but in
                                                   //case won't be stored in db, just used in the creation of the hash to ensure uniqueness
         } else {
            data.setLocalId(id);
         }
         data.setRelOrder(relOrder);
         data.setRelationArtifact(relArtId);
      } else {
         data.setLocalId(id);
      }
      data.setType(relationType);
      data.setBaseType(baseRelationType);
      data.setModType(modType);
      data.setBaseModType(baseModType);
      data.setArtIdA(aArtId);
      data.setArtIdB(bArtId);
      if (Strings.isValid(rationale)) {
         data.setRationale(rationale);
      }
      data.setApplicabilityId(applicId);
      return data;
   }

   @Override
   public RelationData createCopy(RelationData source) {
      VersionData newVersion = createCopy(source.getVersion());
      return createRelationData(newVersion, source, source.getType(), source.getModType(), source.getBaseType(),
         source.getBaseModType(), source.getArtifactIdA(), source.getArtifactIdB(), source.getRelationArtifact(),
         source.getRelOrder(), source.getRationale(), source.getApplicabilityId());
   }

   @Override
   public BranchCategoryData createBranchCategoryData(VersionData version, BranchId branchId,
      BranchCategoryToken category) {
      BranchCategoryData data = new BranchCategoryDataImpl(version);
      data.setBaseModType(ModificationType.NEW);
      data.setModType(ModificationType.NEW);
      data.setApplicabilityId(ApplicabilityId.BASE);
      data.getVersion().setGammaId(GammaId.valueOf(Lib.generateUuid()));
      data.getVersion().setBranch(branchId);
      data.setBranchId(branchId);
      data.setCategory(category);
      return data;
   }

   private TupleData createTuple2Data(VersionData version, BranchId branch, TupleTypeId tupleType, Long e1, Long e2) {
      TupleData data = new TupleDataImpl(version);
      data.setBaseModType(ModificationType.NEW);
      data.setModType(ModificationType.NEW);
      data.setApplicabilityId(ApplicabilityId.BASE);
      data.setTupleType(tupleType);
      data.getVersion().setGammaId(GammaId.valueOf(Lib.generateUuid()));
      data.getVersion().setBranch(branch);
      data.setElement1(e1);
      data.setElement2(e2);
      return data;
   }

   @Override
   public TupleData createTuple2Data(VersionData version, BranchId branch, Tuple2Type<?, ?> tupleType, Long e1,
      Long e2) {
      return createTuple2Data(version, branch, (TupleTypeId) tupleType, e1, e2);
   }

   @Override
   public TupleData createTuple3Data(VersionData version, BranchId branch, Tuple3Type<?, ?, ?> tupleType, Long e1,
      Long e2, Long e3) {
      TupleData data = createTuple2Data(version, branch, tupleType, e1, e2);
      data.setElement3(e3);
      return data;
   }

   @Override
   public TupleData createTuple4Data(VersionData version, BranchId branch, Tuple4Type<?, ?, ?, ?> tupleType, Long e1,
      Long e2, Long e3, Long e4) {
      TupleData data = createTuple2Data(version, branch, tupleType, e1, e2);
      data.setElement3(e3);
      data.setElement4(e4);
      return data;
   }

   @Override
   public ArtifactData createArtifactData(VersionData version, ArtifactId artifactId, Long artifactType,
      ModificationType modType, String guidToSet, ApplicabilityId applicId) {
      return createArtifactData(version, artifactId, tokenService.getArtifactType(artifactType), modType, guidToSet,
         applicId);
   }

}