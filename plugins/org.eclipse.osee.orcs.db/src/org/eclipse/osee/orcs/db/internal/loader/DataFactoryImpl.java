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

package org.eclipse.osee.orcs.db.internal.loader;

import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
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
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.BranchCategoryData;
import org.eclipse.osee.orcs.core.ds.DataFactory;
import org.eclipse.osee.orcs.core.ds.OrcsData;
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.core.ds.TupleData;
import org.eclipse.osee.orcs.core.ds.VersionData;
import org.eclipse.osee.orcs.db.internal.IdentityManager;
import org.eclipse.osee.orcs.db.internal.OrcsObjectFactory;
import org.eclipse.osee.orcs.db.internal.loader.data.TupleDataImpl;

/**
 * @author Audrey Denk
 */
public class DataFactoryImpl implements DataFactory {
   private final IdentityManager idFactory;
   private final OrcsObjectFactory objectFactory;

   public DataFactoryImpl(IdentityManager idFactory, OrcsObjectFactory objectFactory) {
      this.idFactory = idFactory;
      this.objectFactory = objectFactory;
   }

   @Override
   public ArtifactData create(BranchId branch, ArtifactTypeToken token, String guid) {
      return create(branch, token, guid, idFactory.getNextArtifactId());
   }

   @Override
   public ArtifactData create(BranchId branch, ArtifactTypeToken token, String guid, ApplicabilityId appId) {
      return create(branch, token, guid, idFactory.getNextArtifactId(), appId);
   }

   @Override
   public ArtifactData create(BranchId branch, ArtifactTypeToken token, ArtifactId artifactId) {
      return create(branch, token, null, artifactId);
   }

   @Override
   public ArtifactData create(BranchId branch, ArtifactTypeToken token, ArtifactId artifactId, ApplicabilityId appId) {
      return create(branch, token, null, artifactId, appId);
   }

   @Override
   public ArtifactData create(BranchId branchId, ArtifactTypeToken token, String guid, ArtifactId artifactId) {
      return create(branchId, token, guid, artifactId, ApplicabilityId.BASE);
   }

   @Override
   public ArtifactData create(BranchId branchId, ArtifactTypeToken token, String guid, ArtifactId artifactId,
      ApplicabilityId appId) {
      Conditions.checkNotNull(branchId, "branch");

      Conditions.checkExpressionFailOnTrue(token.isAbstract(), "Cannot create an instance of abstract type [%s]",
         token);

      String guidToSet = idFactory.getUniqueGuid(guid);

      Conditions.checkExpressionFailOnTrue(!GUID.isValid(guidToSet),
         "Invalid guid [%s] during artifact creation [type: %s]", guidToSet, token);

      VersionData version = objectFactory.createDefaultVersionData();
      version.setBranch(branchId);

      ModificationType modType = RelationalConstants.DEFAULT_MODIFICATION_TYPE;
      ArtifactData artifactData =
         objectFactory.createArtifactData(version, ArtifactId.valueOf(artifactId), token, modType, guidToSet, appId);
      return artifactData;
   }

   @Override
   public ArtifactData copy(BranchId destination, ArtifactData source) {
      ArtifactData copy = objectFactory.createCopy(source);
      updateDataForCopy(destination, copy);
      copy.setGuid(idFactory.getUniqueGuid(null));
      copy.setLocalId(idFactory.getNextArtifactId());
      return copy;
   }

   @Override
   public AttributeData introduce(BranchId destination, AttributeData source) {
      AttributeData newVersion = objectFactory.createCopy(source);
      newVersion.setUseBackingData(true);
      updateDataForIntroduce(destination, newVersion);
      return newVersion;
   }

   @Override
   public AttributeData create(ArtifactData parent, AttributeTypeGeneric attributeType) {
      VersionData version = objectFactory.createDefaultVersionData();
      version.setBranch(parent.getVersion().getBranch());
      ModificationType modType = RelationalConstants.DEFAULT_MODIFICATION_TYPE;
      return objectFactory.createAttributeData(version, idFactory.getNextAttributeId(), attributeType, modType, parent,
         ApplicabilityId.BASE);
   }

   @Override
   public AttributeData create(ArtifactData parent, AttributeTypeGeneric attributeType, AttributeId attributeId) {
      VersionData version = objectFactory.createDefaultVersionData();
      version.setBranch(parent.getVersion().getBranch());
      ModificationType modType = RelationalConstants.DEFAULT_MODIFICATION_TYPE;
      return objectFactory.createAttributeData(version, attributeId, attributeType, modType, parent,
         ApplicabilityId.BASE);
   }

   @Override
   public AttributeData copy(BranchId destination, AttributeData orcsData) {
      AttributeData copy = objectFactory.createCopy(orcsData);
      updateDataForCopy(destination, copy);
      copy.setLocalId(AttributeId.SENTINEL);
      return copy;
   }

   @Override
   public ArtifactData introduce(BranchId destination, ArtifactData source) {
      ArtifactData newVersion = objectFactory.createCopy(source);
      newVersion.setUseBackingData(true);
      updateDataForIntroduce(destination, newVersion);
      return newVersion;
   }

   @Override
   public RelationData createRelationData(RelationTypeToken relationType, BranchId branch, ArtifactId aArtifact,
      ArtifactId bArtifact, String rationale) {
      VersionData version = objectFactory.createDefaultVersionData();
      version.setBranch(branch);
      ModificationType modType = RelationalConstants.DEFAULT_MODIFICATION_TYPE;
      return objectFactory.createRelationData(version, idFactory.getNextRelationId(), relationType, modType, aArtifact,
         bArtifact, rationale, ApplicabilityId.BASE);
   }

   @Override
   public RelationData createRelationData(RelationTypeToken relationType, BranchId branch, ArtifactId aArtifact,
      ArtifactId bArtifact, String rationale, RelationId id) {
      VersionData version = objectFactory.createDefaultVersionData();
      version.setBranch(branch);
      ModificationType modType = RelationalConstants.DEFAULT_MODIFICATION_TYPE;
      return objectFactory.createRelationData(version, id, relationType, modType, aArtifact, bArtifact, rationale,
         ApplicabilityId.BASE);
   }

   @Override
   public RelationData createRelationData(RelationTypeToken relationType, BranchId branch, ArtifactId aArtifact,
      ArtifactId bArtifact, ArtifactId relArtifact, int order) {
      VersionData version = objectFactory.createDefaultVersionData();
      version.setBranch(branch);
      ModificationType modType = RelationalConstants.DEFAULT_MODIFICATION_TYPE;
      return objectFactory.createRelationData(version, idFactory.getNextRelationId(), relationType, modType, aArtifact,
         bArtifact, relArtifact, order, ApplicabilityId.BASE);
   }

   @Override
   public TupleData introduceTupleData(TupleTypeId tupleType, GammaId tupleGamma) {
      VersionData version = objectFactory.createDefaultVersionData();
      version.setGammaId(tupleGamma);
      TupleData tupleData = new TupleDataImpl(version);
      tupleData.setTupleType(tupleType);
      tupleData.setApplicabilityId(ApplicabilityId.BASE);
      tupleData.setUseBackingData(true);

      tupleData.setModType(ModificationType.INTRODUCED);
      return tupleData;
   }

   @Override
   public TupleData createTuple2Data(Tuple2Type<?, ?> tupleType, BranchId branch, Long e1, Long e2) {
      VersionData version = objectFactory.createDefaultVersionData();
      TupleData tupleData = objectFactory.createTuple2Data(version, branch, tupleType, e1, e2);
      return tupleData;
   }

   @Override
   public BranchCategoryData createBranchCategoryData(BranchId branch, BranchCategoryToken category) {
      VersionData version = objectFactory.createDefaultVersionData();
      BranchCategoryData categoryData = objectFactory.createBranchCategoryData(version, branch, category);
      return categoryData;
   }

   @Override
   public TupleData createTuple3Data(Tuple3Type<?, ?, ?> tupleType, BranchId branch, Long e1, Long e2, Long e3) {
      VersionData version = objectFactory.createDefaultVersionData();
      TupleData tupleData = objectFactory.createTuple3Data(version, branch, tupleType, e1, e2, e3);
      return tupleData;
   }

   @Override
   public TupleData createTuple4Data(Tuple4Type<?, ?, ?, ?> tupleType, BranchId branch, Long e1, Long e2, Long e3,
      Long e4) {
      VersionData version = objectFactory.createDefaultVersionData();
      TupleData tupleData = objectFactory.createTuple4Data(version, branch, tupleType, e1, e2, e3, e4);
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
   public AttributeData clone(AttributeData source) {
      return objectFactory.createCopy(source);
   }

   @Override
   public RelationData clone(RelationData source) {
      return objectFactory.createCopy(source);
   }

   private <T extends Id> void updateDataForCopy(BranchId destination, OrcsData<T> data) {
      VersionData version = data.getVersion();
      version.setBranch(destination);
      version.setTransactionId(TransactionId.SENTINEL);
      version.setStripeId(TransactionId.SENTINEL);
      version.setHistorical(false);
      version.setGammaId(GammaId.SENTINEL);

      data.setModType(ModificationType.NEW);
   }

   private <T extends Id> void updateDataForIntroduce(BranchId destination, OrcsData<T> data) {
      VersionData version = data.getVersion();
      version.setBranch(destination);
      version.setHistorical(false);
      version.setTransactionId(TransactionId.SENTINEL);
      // do not clear gammaId for introduce case so we reuse the same version
   }

}
