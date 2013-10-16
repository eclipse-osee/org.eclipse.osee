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

import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.DataFactory;
import org.eclipse.osee.orcs.core.ds.OrcsData;
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.core.ds.VersionData;
import org.eclipse.osee.orcs.data.ArtifactTypes;
import org.eclipse.osee.orcs.data.HasLocalId;
import org.eclipse.osee.orcs.db.internal.IdentityManager;
import org.eclipse.osee.orcs.db.internal.OrcsObjectFactory;
import org.eclipse.osee.orcs.db.internal.sql.RelationalConstants;

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
   public ArtifactData create(IOseeBranch branch, IArtifactType token, String guid) throws OseeCoreException {
      Conditions.checkNotNull(branch, "branch");

      Conditions.checkExpressionFailOnTrue(artifactCache.isAbstract(token),
         "Cannot create an instance of abstract type [%s]", token);

      String guidToSet = idFactory.getUniqueGuid(guid);

      Conditions.checkExpressionFailOnTrue(!GUID.isValid(guidToSet),
         "Invalid guid [%s] during artifact creation [type: %s]", guidToSet, token);

      int branchId = idFactory.getLocalId(branch);

      VersionData version = objectFactory.createDefaultVersionData();
      version.setBranchId(branchId);

      ModificationType modType = RelationalConstants.DEFAULT_MODIFICATION_TYPE;
      int artifactId = idFactory.getNextArtifactId();
      ArtifactData artifactData = objectFactory.createArtifactData(version, artifactId, token, modType, guidToSet);
      return artifactData;
   }

   @Override
   public ArtifactData copy(IOseeBranch destination, ArtifactData source) throws OseeCoreException {
      ArtifactData copy = objectFactory.createCopy(source);
      updateDataForCopy(destination, copy);
      copy.setGuid(idFactory.getUniqueGuid(null));
      copy.setLocalId(idFactory.getNextArtifactId());
      return copy;
   }

   @Override
   public AttributeData introduce(IOseeBranch destination, AttributeData source) throws OseeCoreException {
      AttributeData newVersion = objectFactory.createCopy(source);
      updateDataForIntroduce(destination, newVersion);
      return newVersion;
   }

   @Override
   public AttributeData create(ArtifactData parent, IAttributeType attributeType) throws OseeCoreException {
      VersionData version = objectFactory.createDefaultVersionData();
      version.setBranchId(parent.getVersion().getBranchId());
      ModificationType modType = RelationalConstants.DEFAULT_MODIFICATION_TYPE;
      int attributeId = RelationalConstants.DEFAULT_ITEM_ID;
      return objectFactory.createAttributeData(version, attributeId, attributeType, modType, parent.getLocalId());
   }

   @Override
   public AttributeData copy(IOseeBranch destination, AttributeData orcsData) throws OseeCoreException {
      AttributeData copy = objectFactory.createCopy(orcsData);
      updateDataForCopy(destination, copy);
      copy.setLocalId(RelationalConstants.DEFAULT_ITEM_ID);
      return copy;
   }

   @Override
   public ArtifactData introduce(IOseeBranch destination, ArtifactData source) throws OseeCoreException {
      ArtifactData newVersion = objectFactory.createCopy(source);
      updateDataForIntroduce(destination, newVersion);
      return newVersion;
   }

   @Override
   public RelationData createRelationData(IRelationType relationType, IOseeBranch branch, HasLocalId aArt, HasLocalId bArt, String rationale) throws OseeCoreException {
      VersionData version = objectFactory.createDefaultVersionData();
      version.setBranchId(idFactory.getLocalId(branch));
      ModificationType modType = RelationalConstants.DEFAULT_MODIFICATION_TYPE;
      int relationId = RelationalConstants.DEFAULT_ITEM_ID;
      return objectFactory.createRelationData(version, relationId, relationType, modType, aArt.getLocalId(),
         bArt.getLocalId(), rationale);
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

   private void updateDataForCopy(IOseeBranch destination, OrcsData data) throws OseeCoreException {
      VersionData version = data.getVersion();
      version.setBranchId(idFactory.getLocalId(destination));
      version.setTransactionId(RelationalConstants.TRANSACTION_SENTINEL);
      version.setStripeId(RelationalConstants.TRANSACTION_SENTINEL);
      version.setHistorical(false);
      version.setGammaId(RelationalConstants.GAMMA_SENTINEL);

      data.setModType(ModificationType.NEW);
   }

   private void updateDataForIntroduce(IOseeBranch destination, OrcsData data) throws OseeCoreException {
      VersionData version = data.getVersion();
      version.setBranchId(idFactory.getLocalId(destination));
      version.setTransactionId(RelationalConstants.TRANSACTION_SENTINEL);
      version.setStripeId(RelationalConstants.TRANSACTION_SENTINEL);
      version.setHistorical(false);
      // do not clear gammaId for introduce case so we reuse the same version
      data.setModType(ModificationType.INTRODUCED);
   }
}
