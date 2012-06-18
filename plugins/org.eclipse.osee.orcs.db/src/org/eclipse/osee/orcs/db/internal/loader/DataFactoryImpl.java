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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.cache.ArtifactTypeCache;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.HumanReadableId;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.DataFactory;
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.core.ds.VersionData;
import org.eclipse.osee.orcs.data.HasLocalId;

/**
 * @author Roberto E. Escobar
 */
public class DataFactoryImpl implements DataFactory {

   private final OrcsObjectFactory rowFactory;
   private final IOseeDatabaseService dbService;

   private final BranchCache branchCache;
   private final ArtifactTypeCache artifactCache;

   public DataFactoryImpl(OrcsObjectFactory rowFactory, IOseeDatabaseService dbService, BranchCache branchCache, ArtifactTypeCache artifactCache) {
      super();
      this.rowFactory = rowFactory;
      this.dbService = dbService;
      this.branchCache = branchCache;
      this.artifactCache = artifactCache;
   }

   /////////////////////////////////////

   private boolean isUniqueHRID(String id) throws OseeCoreException {
      String DUPLICATE_HRID_SEARCH =
         "select count(1) from (select DISTINCT(art_id) from osee_artifact where human_readable_id = ?) t1";
      return dbService.runPreparedQueryFetchObject(0L, DUPLICATE_HRID_SEARCH, id) <= 0;
   }

   private String getUniqueGuid(String guid) {
      String toReturn = guid;
      if (toReturn == null) {
         toReturn = GUID.create();
      }
      return toReturn;
   }

   private String getUniqueHumanReadableId(String humanReadableId) throws OseeCoreException {
      String toReturn = humanReadableId;
      if (toReturn == null) {
         String hrid = HumanReadableId.generate();
         toReturn = isUniqueHRID(hrid) ? hrid : HumanReadableId.generate();
      }
      return toReturn;
   }

   private int getBranchId(IOseeBranch branch) throws OseeCoreException {
      return branchCache.getLocalId(branch);
   }

   private int generateArtId() throws OseeCoreException {
      return dbService.getSequence().getNextArtifactId();
   }

   private int generateAttrId() throws OseeCoreException {
      return dbService.getSequence().getNextAttributeId();
   }

   private int generateRelationId() throws OseeCoreException {
      return dbService.getSequence().getNextRelationId();
   }

   /////////////////////////////////////

   @Override
   public ArtifactData create(IOseeBranch branch, IArtifactType artifactType, String guid) throws OseeCoreException {
      return create(branch, artifactType, guid);
   }

   @Override
   public ArtifactData create(IOseeBranch branch, IArtifactType token, String guid, String hrid) throws OseeCoreException {
      Conditions.checkNotNull(branch, "branch");

      ArtifactType artifactType = artifactCache.get(token);
      Conditions.checkExpressionFailOnTrue(artifactType.isAbstract(),
         "Cannot create an instance of abstract type [%s]", artifactType);

      String guidToSet = getUniqueGuid(guid);
      String humanReadableId = getUniqueHumanReadableId(hrid);

      Conditions.checkExpressionFailOnTrue(!GUID.isValid(guidToSet),
         "Invalid guid [%s] during artifact creation [type: %s]", guidToSet, artifactType);

      Conditions.checkExpressionFailOnTrue(!HumanReadableId.isValid(humanReadableId),
         "Invalid human readable id [%s] during artifact creation [type: %s, guid: %s]", humanReadableId, artifactType,
         guid);

      int branchId = getBranchId(branch);

      VersionData version = rowFactory.createDefaultVersionData();
      version.setBranchId(branchId);

      ModificationType modType = RelationalConstants.DEFAULT_MODIFICATION_TYPE;
      ArtifactData artifactData =
         rowFactory.createArtifactData(version, generateArtId(), artifactType, modType, guidToSet, humanReadableId);

      //      artifact.meetMinimumAttributeCounts(true);
      //      ArtifactCache.cache(artifact);
      //      artifact.setLinksLoaded(true);
      return artifactData;
   }

   @Override
   public ArtifactData copy(IOseeBranch destination, ArtifactData source) throws OseeCoreException {
      ArtifactData copy = rowFactory.createCopy(source);
      copy.getVersion().setBranchId(getBranchId(destination));
      copy.setModType(ModificationType.NEW);
      return copy;
   }

   @Override
   public AttributeData introduce(IOseeBranch destination, AttributeData source) throws OseeCoreException {
      AttributeData newVersion = copy(destination, source);
      newVersion.getVersion().setHistorical(false);
      newVersion.setModType(ModificationType.INTRODUCED);
      return newVersion;
   }

   @Override
   public AttributeData create(HasLocalId parent, IAttributeType attributeType) throws OseeCoreException {
      VersionData version = rowFactory.createDefaultVersionData();
      ModificationType modType = RelationalConstants.DEFAULT_MODIFICATION_TYPE;
      return rowFactory.createAttributeData(version, generateAttrId(), attributeType, modType, parent.getLocalId());
   }

   @Override
   public AttributeData copy(IOseeBranch destination, AttributeData orcsData) throws OseeCoreException {
      int branchId = getBranchId(destination);
      AttributeData copy = rowFactory.createCopy(orcsData);
      copy.getVersion().setBranchId(branchId);
      copy.setModType(ModificationType.NEW);
      return copy;
   }

   @Override
   public ArtifactData introduce(IOseeBranch destination, ArtifactData source) throws OseeCoreException {
      ArtifactData newVersion = copy(destination, source);
      newVersion.getVersion().setHistorical(false);
      newVersion.setModType(ModificationType.INTRODUCED);
      return newVersion;
   }

   @Override
   public RelationData createRelationData(IRelationType relationType, HasLocalId parent, HasLocalId aArt, HasLocalId bArt, String rationale) throws OseeCoreException {
      VersionData version = rowFactory.createDefaultVersionData();
      ModificationType modType = RelationalConstants.DEFAULT_MODIFICATION_TYPE;
      return rowFactory.createRelationData(version, generateRelationId(), relationType, modType, parent.getLocalId(),
         aArt.getLocalId(), bArt.getLocalId(), rationale);
   }

}
