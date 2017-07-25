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
package org.eclipse.osee.orcs.db.internal.loader;

import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.HasLocalId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.RelationTypeId;
import org.eclipse.osee.framework.core.data.RelationalConstants;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.DataFactory;
import org.eclipse.osee.orcs.core.ds.OrcsData;
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.core.ds.TupleData;
import org.eclipse.osee.orcs.core.ds.VersionData;
import org.eclipse.osee.orcs.data.ArtifactTypes;
import org.eclipse.osee.orcs.db.internal.IdentityManager;
import org.eclipse.osee.orcs.db.internal.OrcsObjectFactory;

/**
 * @author Roberto E. Escobar
 */
public class DataFactoryImpl implements DataFactory {

   private final IdentityManager idFactory;
   private final OrcsObjectFactory objectFactory;
   private final ArtifactTypes artifactCache;

   public DataFactoryImpl(IdentityManager idFactory, OrcsObjectFactory objectFactory, ArtifactTypes artifactTypes) {
      super();
      this.idFactory = idFactory;
      this.objectFactory = objectFactory;
      this.artifactCache = artifactTypes;
   }

   @Override
   public ArtifactData create(BranchId branch, IArtifactType token, String guid) throws OseeCoreException {
      return this.create(branch, token, guid, idFactory.getNextArtifactId());
   }

   @Override
   public ArtifactData create(BranchId branchId, IArtifactType token, String guid, long artifactId) throws OseeCoreException {
      Conditions.checkNotNull(branchId, "branch");

      Conditions.checkExpressionFailOnTrue(artifactCache.isAbstract(token),
         "Cannot create an instance of abstract type [%s]", token);

      String guidToSet = idFactory.getUniqueGuid(guid);

      Conditions.checkExpressionFailOnTrue(!GUID.isValid(guidToSet),
         "Invalid guid [%s] during artifact creation [type: %s]", guidToSet, token);

      VersionData version = objectFactory.createDefaultVersionData();
      version.setBranch(branchId);

      ModificationType modType = RelationalConstants.DEFAULT_MODIFICATION_TYPE;
      ArtifactData artifactData =
         objectFactory.createArtifactData(version, (int) artifactId, token, modType, guidToSet, ApplicabilityId.BASE);
      return artifactData;
   }

   @Override
   public ArtifactData copy(BranchId destination, ArtifactData source) throws OseeCoreException {
      ArtifactData copy = objectFactory.createCopy(source);
      updateDataForCopy(destination, copy);
      copy.setGuid(idFactory.getUniqueGuid(null));
      copy.setLocalId(idFactory.getNextArtifactId());
      return copy;
   }

   @Override
   public AttributeData introduce(BranchId destination, AttributeData source) throws OseeCoreException {
      AttributeData newVersion = objectFactory.createCopy(source);
      newVersion.setUseBackingData(true);
      updateDataForIntroduce(destination, newVersion);
      return newVersion;
   }

   @Override
   public AttributeData create(ArtifactData parent, AttributeTypeId attributeType) throws OseeCoreException {
      VersionData version = objectFactory.createDefaultVersionData();
      version.setBranch(parent.getVersion().getBranch());
      ModificationType modType = RelationalConstants.DEFAULT_MODIFICATION_TYPE;
      return objectFactory.createAttributeData(version, idFactory.getNextAttributeId(), attributeType, modType,
         parent.getLocalId(), ApplicabilityId.BASE);
   }

   @Override
   public AttributeData copy(BranchId destination, AttributeData orcsData) throws OseeCoreException {
      AttributeData copy = objectFactory.createCopy(orcsData);
      updateDataForCopy(destination, copy);
      copy.setLocalId(RelationalConstants.DEFAULT_ITEM_ID);
      return copy;
   }

   @Override
   public ArtifactData introduce(BranchId destination, ArtifactData source) throws OseeCoreException {
      ArtifactData newVersion = objectFactory.createCopy(source);
      newVersion.setUseBackingData(true);
      updateDataForIntroduce(destination, newVersion);
      return newVersion;
   }

   @Override
   public RelationData createRelationData(RelationTypeId relationType, BranchId branch, HasLocalId<Integer> aArt, HasLocalId<Integer> bArt, String rationale) throws OseeCoreException {
      VersionData version = objectFactory.createDefaultVersionData();
      version.setBranch(branch);
      ModificationType modType = RelationalConstants.DEFAULT_MODIFICATION_TYPE;
      return objectFactory.createRelationData(version, idFactory.getNextRelationId(), relationType, modType,
         ArtifactId.valueOf(aArt.getLocalId()), ArtifactId.valueOf(bArt.getLocalId()), rationale, ApplicabilityId.BASE);
   }

   @Override
   public TupleData createTuple2Data(Long tupleTypeId, BranchId branch, Long e1, Long e2) {
      VersionData version = objectFactory.createDefaultVersionData();
      TupleData tupleData = objectFactory.createTuple2Data(version, branch, tupleTypeId, e1, e2);
      return tupleData;
   }

   @Override
   public TupleData createTuple3Data(Long tupleTypeId, BranchId branch, Long e1, Long e2, Long e3) {
      VersionData version = objectFactory.createDefaultVersionData();
      TupleData tupleData = objectFactory.createTuple3Data(version, branch, tupleTypeId, e1, e2, e3);
      return tupleData;
   }

   @Override
   public TupleData createTuple4Data(Long tupleTypeId, BranchId branch, Long e1, Long e2, Long e3, Long e4) {
      VersionData version = objectFactory.createDefaultVersionData();
      TupleData tupleData = objectFactory.createTuple4Data(version, branch, tupleTypeId, e1, e2, e3, e4);
      return tupleData;
   }

   @Override
   public RelationData introduce(BranchId destination, RelationData source) {
      RelationData newVersion = objectFactory.createCopy(source);
      newVersion.setUseBackingData(true);
      updateDataForIntroduce(destination, newVersion);
      return newVersion;
   }

   @Override
   public ArtifactData clone(ArtifactData source) {
      return objectFactory.createCopy(source);
   }

   @Override
   public AttributeData clone(AttributeData source) throws OseeCoreException {
      return objectFactory.createCopy(source);
   }

   @Override
   public RelationData clone(RelationData source) throws OseeCoreException {
      return objectFactory.createCopy(source);
   }

   private void updateDataForCopy(BranchId destination, OrcsData data) throws OseeCoreException {
      VersionData version = data.getVersion();
      version.setBranch(destination);
      version.setTransactionId(TransactionId.SENTINEL);
      version.setStripeId(TransactionId.SENTINEL);
      version.setHistorical(false);
      version.setGammaId(RelationalConstants.GAMMA_SENTINEL);

      data.setModType(ModificationType.NEW);
   }

   private void updateDataForIntroduce(BranchId destination, OrcsData data) throws OseeCoreException {
      VersionData version = data.getVersion();
      version.setBranch(destination);
      version.setHistorical(false);
      version.setTransactionId(TransactionId.SENTINEL);
      // do not clear gammaId for introduce case so we reuse the same version
   }
}
