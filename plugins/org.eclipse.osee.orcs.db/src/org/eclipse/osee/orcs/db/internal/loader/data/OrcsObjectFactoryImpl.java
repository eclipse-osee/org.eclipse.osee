/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.loader.data;

import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.RelationTypeId;
import org.eclipse.osee.framework.core.data.RelationalConstants;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.Tuple2Type;
import org.eclipse.osee.framework.core.data.Tuple3Type;
import org.eclipse.osee.framework.core.data.Tuple4Type;
import org.eclipse.osee.framework.core.data.TupleTypeId;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.DataProxy;
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.core.ds.TupleData;
import org.eclipse.osee.orcs.core.ds.VersionData;
import org.eclipse.osee.orcs.db.internal.OrcsObjectFactory;
import org.eclipse.osee.orcs.db.internal.proxy.AttributeDataProxyFactory;

/**
 * @author Roberto E. Escobar
 */
public class OrcsObjectFactoryImpl implements OrcsObjectFactory {

   private final AttributeDataProxyFactory proxyFactory;

   public OrcsObjectFactoryImpl(AttributeDataProxyFactory proxyFactory) {
      super();
      this.proxyFactory = proxyFactory;
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

   private VersionData createVersion(BranchId branchId, TransactionId txId, GammaId gamma, boolean historical, TransactionId stripeId) {
      VersionData version = new VersionDataImpl();
      version.setBranch(branchId);
      version.setTransactionId(txId);
      version.setGammaId(gamma);
      version.setHistorical(historical);
      version.setStripeId(stripeId);
      return version;
   }

   @Override
   public ArtifactData createArtifactData(VersionData version, Integer id, long typeUuid, ModificationType modType, String guid, ApplicabilityId applicId) {
      return createArtifactFromRow(version, id, typeUuid, modType, typeUuid, modType, guid, applicId);
   }

   @Override
   public ArtifactData createArtifactData(VersionData version, int id, ArtifactTypeId type, ModificationType modType, String guid, ApplicabilityId applicId) {
      return createArtifactFromRow(version, id, type.getId(), modType, type.getId(), modType, guid, applicId);
   }

   @Override
   public ArtifactData createCopy(ArtifactData source) {
      VersionData newVersion = createCopy(source.getVersion());
      return createArtifactFromRow(newVersion, source.getLocalId(), source.getTypeUuid(), source.getModType(),
         source.getBaseTypeUuid(), source.getBaseModType(), source.getGuid(), source.getApplicabilityId());
   }

   @Override
   public AttributeData createAttributeData(VersionData version, Integer id, AttributeTypeId attributeType, ModificationType modType, int artifactId, Object value, String uri, ApplicabilityId applicId) {
      Long typeId = attributeType.getId();
      DataProxy proxy = proxyFactory.createProxy(typeId, value, uri);
      return createAttributeFromRow(version, id, typeId, modType, typeId, modType, artifactId, proxy, applicId);
   }

   @Override
   public AttributeData createCopy(AttributeData source) {
      VersionData newVersion = createCopy(source.getVersion());
      long typeId = source.getTypeUuid();
      DataProxy sourceProxy = source.getDataProxy();
      DataProxy newProxy = proxyFactory.createProxy(typeId, sourceProxy.getRawValue(), sourceProxy.getUri());
      return createAttributeFromRow(newVersion, source.getLocalId(), typeId, source.getModType(),
         source.getBaseTypeUuid(), source.getBaseModType(), source.getArtifactId(), newProxy,
         source.getApplicabilityId());
   }

   @Override
   public AttributeData createAttributeData(VersionData version, Integer id, AttributeTypeId attributeType, ModificationType modType, int artId, ApplicabilityId applicId) {
      long typeId = attributeType.getId();
      DataProxy proxy = proxyFactory.createProxy(typeId, "", "");
      return createAttributeFromRow(version, id, typeId, modType, typeId, modType, artId, proxy, applicId);
   }

   @Override
   public RelationData createRelationData(VersionData version, Integer id, RelationTypeId relationType, ModificationType modType, ArtifactId aArtId, ArtifactId bArtId, String rationale, ApplicabilityId applicId) {
      long typeId = relationType.getId();
      return createRelationData(version, id, typeId, modType, typeId, modType, aArtId, bArtId, rationale, applicId);
   }

   private ArtifactData createArtifactFromRow(VersionData version, int id, long localTypeID, ModificationType modType, long baseLocalTypeID, ModificationType baseModType, String guid, ApplicabilityId applicId) {
      ArtifactData data = new ArtifactDataImpl(version);
      data.setLocalId(id);
      data.setTypeUuid(localTypeID);
      data.setBaseTypeUuid(baseLocalTypeID);
      data.setModType(modType);
      data.setBaseModType(baseModType);
      data.setGuid(guid);
      data.setApplicabilityId(applicId);
      return data;
   }

   private AttributeData createAttributeFromRow(VersionData version, int id, long localTypeID, ModificationType modType, long baseLocalTypeID, ModificationType baseModType, int artifactId, DataProxy proxy, ApplicabilityId applicId) {
      AttributeData data = new AttributeDataImpl(version);
      data.setLocalId(id);
      data.setTypeUuid(localTypeID);
      data.setBaseTypeUuid(baseLocalTypeID);
      data.setModType(modType);
      data.setBaseModType(baseModType);
      data.setArtifactId(artifactId);
      data.setDataProxy(proxy);
      data.setApplicabilityId(applicId);
      return data;
   }

   private RelationData createRelationData(VersionData version, int id, long relationType, ModificationType modType, long baseLocalTypeID, ModificationType baseModType, ArtifactId aArtId, ArtifactId bArtId, String rationale, ApplicabilityId applicId) {
      RelationData data = new RelationDataImpl(version);
      data.setLocalId(id);
      data.setTypeUuid(relationType);
      data.setBaseTypeUuid(baseLocalTypeID);
      data.setModType(modType);
      data.setBaseModType(baseModType);
      data.setArtIdA(aArtId);
      data.setArtIdB(bArtId);
      Conditions.assertNotNull(rationale,
         "rationale can't be null for RelationData id [%s], type [%s], aArtId [%s], bArtId", id, relationType, aArtId,
         bArtId);
      data.setRationale(rationale);
      data.setApplicabilityId(applicId);
      return data;
   }

   @Override
   public RelationData createCopy(RelationData source) {
      VersionData newVersion = createCopy(source.getVersion());
      return createRelationData(newVersion, source.getLocalId(), source.getTypeUuid(), source.getModType(),
         source.getBaseTypeUuid(), source.getBaseModType(), source.getArtifactIdA(), source.getArtifactIdB(),
         source.getRationale(), source.getApplicabilityId());
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
   public TupleData createTuple2Data(VersionData version, BranchId branch, Tuple2Type<?, ?> tupleType, Long e1, Long e2) {
      return createTuple2Data(version, branch, (TupleTypeId) tupleType, e1, e2);
   }

   @Override
   public TupleData createTuple3Data(VersionData version, BranchId branch, Tuple3Type<?, ?, ?> tupleType, Long e1, Long e2, Long e3) {
      TupleData data = createTuple2Data(version, branch, tupleType, e1, e2);
      data.setElement3(e3);
      return data;
   }

   @Override
   public TupleData createTuple4Data(VersionData version, BranchId branch, Tuple4Type<?, ?, ?, ?> tupleType, Long e1, Long e2, Long e3, Long e4) {
      TupleData data = createTuple2Data(version, branch, tupleType, e1, e2);
      data.setElement3(e3);
      data.setElement4(e4);
      return data;
   }
}