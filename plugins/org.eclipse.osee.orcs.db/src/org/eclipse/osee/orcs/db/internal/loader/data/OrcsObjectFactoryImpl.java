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

import java.util.Date;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.BranchData;
import org.eclipse.osee.orcs.core.ds.DataProxy;
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.core.ds.TxOrcsData;
import org.eclipse.osee.orcs.core.ds.VersionData;
import org.eclipse.osee.orcs.db.internal.OrcsObjectFactory;
import org.eclipse.osee.orcs.db.internal.loader.ProxyDataFactory;
import org.eclipse.osee.orcs.db.internal.sql.RelationalConstants;

/**
 * @author Roberto E. Escobar
 */
public class OrcsObjectFactoryImpl implements OrcsObjectFactory {

   private final ProxyDataFactory proxyFactory;
   private final IdentityService identityService;

   public OrcsObjectFactoryImpl(ProxyDataFactory proxyFactory, IdentityService identityService) {
      super();
      this.proxyFactory = proxyFactory;
      this.identityService = identityService;
   }

   private long toUuid(int localId) throws OseeCoreException {
      return identityService.getUniversalId(localId);
   }

   @Override
   public VersionData createVersion(int branchId, int txId, long gamma, boolean historical) {
      return createVersion(branchId, txId, gamma, historical, RelationalConstants.TRANSACTION_SENTINEL);
   }

   @Override
   public VersionData createDefaultVersionData() {
      // @formatter:off
      return createVersion(
         RelationalConstants.BRANCH_SENTINEL, 
         RelationalConstants.TRANSACTION_SENTINEL, 
         RelationalConstants.GAMMA_SENTINEL, 
         RelationalConstants.IS_HISTORICAL_DEFAULT, 
         RelationalConstants.TRANSACTION_SENTINEL);
      // @formatter:on
   }

   @Override
   public VersionData createCopy(VersionData other) {
      // @formatter:off
      return createVersion(
         other.getBranchId(),
         other.getTransactionId(), 
         other.getGammaId(), 
         other.isHistorical(), 
         other.getStripeId()); 
      // @formatter:on
   }

   private VersionData createVersion(int branchId, int txId, long gamma, boolean historical, int stripeId) {
      VersionData version = new VersionDataImpl();
      version.setBranchId(branchId);
      version.setTransactionId(txId);
      version.setGammaId(gamma);
      version.setHistorical(historical);
      version.setStripeId(stripeId);
      return version;
   }

   @Override
   public ArtifactData createArtifactData(VersionData version, int localId, int localTypeID, ModificationType modType, String guid) throws OseeCoreException {
      long typeUuid = toUuid(localTypeID);
      return createArtifactFromRow(version, localId, typeUuid, modType, typeUuid, modType, guid);
   }

   @Override
   public ArtifactData createArtifactData(VersionData version, int localId, IArtifactType type, ModificationType modType, String guid) {
      long typeUuid = type.getGuid();
      return createArtifactFromRow(version, localId, typeUuid, modType, typeUuid, modType, guid);
   }

   @Override
   public ArtifactData createCopy(ArtifactData source) {
      VersionData newVersion = createCopy(source.getVersion());
      return createArtifactFromRow(newVersion, source.getLocalId(), source.getTypeUuid(), source.getModType(),
         source.getBaseTypeUuid(), source.getBaseModType(), source.getGuid());
   }

   @Override
   public AttributeData createAttributeData(VersionData version, int localId, int localTypeID, ModificationType modType, int artifactId, String value, String uri) throws OseeCoreException {
      long typeId = toUuid(localTypeID);
      DataProxy proxy = proxyFactory.createProxy(typeId, value, uri);
      return createAttributeFromRow(version, localId, typeId, modType, typeId, modType, artifactId, proxy);
   }

   @Override
   public AttributeData createCopy(AttributeData source) throws OseeCoreException {
      VersionData newVersion = createCopy(source.getVersion());
      long typeId = source.getTypeUuid();
      DataProxy sourceProxy = source.getDataProxy();
      DataProxy newProxy = proxyFactory.createProxy(typeId, sourceProxy.getData());
      return createAttributeFromRow(newVersion, source.getLocalId(), typeId, source.getModType(),
         source.getBaseTypeUuid(), source.getBaseModType(), source.getArtifactId(), newProxy);
   }

   @Override
   public AttributeData createAttributeData(VersionData version, int localId, IAttributeType type, ModificationType modType, int artId) throws OseeCoreException {
      long typeId = type.getGuid();
      DataProxy proxy = proxyFactory.createProxy(typeId, "", "");
      return createAttributeFromRow(version, localId, typeId, modType, typeId, modType, artId, proxy);
   }

   @Override
   public RelationData createRelationData(VersionData version, int localId, int localTypeID, ModificationType modType, int aArtId, int bArtId, String rationale) throws OseeCoreException {
      long typeId = toUuid(localTypeID);
      return createRelationData(version, localId, typeId, modType, typeId, modType, aArtId, bArtId, rationale);
   }

   @Override
   public RelationData createRelationData(VersionData version, int localId, IRelationType type, ModificationType modType, int aArtId, int bArtId, String rationale) {
      long typeId = type.getGuid();
      return createRelationData(version, localId, typeId, modType, typeId, modType, aArtId, bArtId, rationale);
   }

   private ArtifactData createArtifactFromRow(VersionData version, int localId, long localTypeID, ModificationType modType, long baseLocalTypeID, ModificationType baseModType, String guid) {
      ArtifactData data = new ArtifactDataImpl(version);
      data.setLocalId(localId);
      data.setTypeUuid(localTypeID);
      data.setBaseTypeUuid(baseLocalTypeID);
      data.setModType(modType);
      data.setBaseModType(baseModType);
      data.setGuid(guid);
      return data;
   }

   private AttributeData createAttributeFromRow(VersionData version, int localId, long localTypeID, ModificationType modType, long baseLocalTypeID, ModificationType baseModType, int artifactId, DataProxy proxy) {
      AttributeData data = new AttributeDataImpl(version);
      data.setLocalId(localId);
      data.setTypeUuid(localTypeID);
      data.setBaseTypeUuid(baseLocalTypeID);
      data.setModType(modType);
      data.setBaseModType(baseModType);
      data.setArtifactId(artifactId);
      data.setDataProxy(proxy);
      return data;
   }

   private RelationData createRelationData(VersionData version, int localId, long localTypeID, ModificationType modType, long baseLocalTypeID, ModificationType baseModType, int aArtId, int bArtId, String rationale) {
      RelationData data = new RelationDataImpl(version);
      data.setLocalId(localId);
      data.setTypeUuid(localTypeID);
      data.setBaseTypeUuid(baseLocalTypeID);
      data.setModType(modType);
      data.setBaseModType(baseModType);
      data.setArtIdA(aArtId);
      data.setArtIdB(bArtId);
      data.setRationale(rationale);
      return data;
   }

   @Override
   public RelationData createCopy(RelationData source) {
      VersionData newVersion = createCopy(source.getVersion());
      return createRelationData(newVersion, source.getLocalId(), source.getTypeUuid(), source.getModType(),
         source.getBaseTypeUuid(), source.getBaseModType(), source.getArtIdA(), source.getArtIdB(),
         source.getRationale());
   }

   @Override
   public BranchData createBranchData(int localId, String guid, BranchType branchType, String name, int parentBranch, int baseTransaction, int sourceTransaction, BranchArchivedState archiveState, BranchState branchState, int associatedArtifactId) {
      BranchData data = new BranchDataImpl();
      data.setArchiveState(archiveState);
      data.setAssociatedArtifactId(associatedArtifactId);
      data.setBaseTransaction(baseTransaction);
      data.setBranchState(branchState);
      data.setBranchType(branchType);
      data.setGuid(guid);
      data.setLocalId(localId);
      data.setName(name);
      data.setParentBranch(parentBranch);
      data.setSourceTransaction(sourceTransaction);
      return data;
   }

   @Override
   public BranchData createCopy(BranchData source) {
      return createBranchData(source.getLocalId(), source.getGuid(), source.getBranchType(), source.getName(),
         source.getParentBranch(), source.getBaseTransaction(), source.getSourceTransaction(),
         source.getArchiveState(), source.getBranchState(), source.getAssociatedArtifactId());
   }

   @Override
   public TxOrcsData createTxData(int localId, TransactionDetailsType type, Date date, String comment, int branchId, int authorId, int commitId) {
      TxOrcsData data = new TransactionDataImpl();
      data.setLocalId(localId);
      data.setTxType(type);
      data.setDate(date);
      data.setComment(comment);
      data.setBranchId(branchId);
      data.setAuthorId(authorId);
      data.setCommit(commitId);
      return data;
   }

   @Override
   public TxOrcsData createCopy(TxOrcsData source) {
      return createTxData(source.getLocalId(), source.getTxType(), source.getDate(), source.getComment(),
         source.getBranchId(), source.getAuthorId(), source.getCommit());
   }

}
