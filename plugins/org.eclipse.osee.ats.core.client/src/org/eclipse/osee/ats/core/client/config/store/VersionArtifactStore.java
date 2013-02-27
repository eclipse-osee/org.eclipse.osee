/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.config.store;

import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.core.client.util.AtsUtilCore;
import org.eclipse.osee.ats.core.config.AtsConfigCache;
import org.eclipse.osee.ats.core.config.AtsVersionService;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

/**
 * @author Donald G. Dunne
 */
public class VersionArtifactStore extends ArtifactAtsObjectStore {

   AtsConfigCache cache = AtsConfigCache.instance;

   public VersionArtifactStore(IAtsVersion version) {
      super(version, AtsArtifactTypes.Version, AtsUtilCore.getAtsBranchToken());
   }

   public VersionArtifactStore(Artifact artifact, AtsConfigCache atsConfigCache) throws OseeCoreException {
      super(null, AtsArtifactTypes.Version, AtsUtilCore.getAtsBranchToken());
      this.artifact = artifact;
      if (atsConfigCache != null) {
         cache = atsConfigCache;
      }
      loadFromArtifact();
   }

   @Override
   public Result saveToArtifact(SkynetTransaction transaction) throws OseeCoreException {
      Artifact verArt = getArtifact();
      if (verArt == null) {
         throw new OseeArgumentException("Version must be created first before save");
      }
      IAtsVersion version = getVersion();
      verArt.setName(version.getName());
      boolean allowCommitBranch = verArt.getSoleAttributeValue(AtsAttributeTypes.AllowCreateBranch, true);
      if (allowCommitBranch != version.isAllowCommitBranch()) {
         verArt.setSoleAttributeValue(AtsAttributeTypes.AllowCommitBranch, version.isAllowCommitBranch());
      }
      boolean allowCreateBranch = verArt.getSoleAttributeValue(AtsAttributeTypes.AllowCreateBranch, true);
      if (allowCreateBranch != version.isAllowCreateBranch()) {
         verArt.setSoleAttributeValue(AtsAttributeTypes.AllowCreateBranch, version.isAllowCreateBranch());
      }
      boolean next = verArt.getSoleAttributeValue(AtsAttributeTypes.NextVersion, false);
      if (next != version.isNextVersion()) {
         verArt.setSoleAttributeValue(AtsAttributeTypes.NextVersion, version.isNextVersion());
      }
      boolean released = verArt.getSoleAttributeValue(AtsAttributeTypes.Released, false);
      if (released != version.isReleased()) {
         verArt.setSoleAttributeValue(AtsAttributeTypes.Released, version.isReleased());
      }
      boolean versionLocked = verArt.getSoleAttributeValue(AtsAttributeTypes.VersionLocked, false);
      if (versionLocked != version.isLocked()) {
         verArt.setSoleAttributeValue(AtsAttributeTypes.VersionLocked, version.isLocked());
      }
      if (Strings.isValid(version.getBaslineBranchGuid())) {
         verArt.setSoleAttributeValue(AtsAttributeTypes.BaselineBranchGuid, version.getBaslineBranchGuid());
      }
      Date releaseDate = verArt.getSoleAttributeValue(AtsAttributeTypes.ReleaseDate, null);
      if (releaseDate != version.getReleaseDate()) {
         verArt.setSoleAttributeValue(AtsAttributeTypes.ReleaseDate, version.getReleaseDate());
      }
      if (Strings.isValid(version.getDescription())) {
         verArt.setSoleAttributeValue(AtsAttributeTypes.Description, version.getDescription());
      }
      if (Strings.isValid(version.getFullName())) {
         verArt.setSoleAttributeValue(AtsAttributeTypes.FullName, version.getFullName());
      }

      // set parent artifact to top team def
      IAtsTeamDefinition teamDefinition = AtsVersionService.get().getTeamDefinition(version);
      if (teamDefinition != null) {
         Artifact teamDefArt = new TeamDefinitionArtifactStore(teamDefinition).getArtifact();
         if (teamDefArt != null) {
            if (!teamDefArt.getRelatedArtifacts(AtsRelationTypes.TeamDefinitionToVersion_Version).contains(verArt)) {
               teamDefArt.addRelation(AtsRelationTypes.TeamDefinitionToVersion_Version, verArt);
               teamDefArt.persist(transaction);
            }
         }
      }
      verArt.persist(transaction);
      cache.cache(version);
      return Result.TrueResult;
   }

   public void loadFromArtifact() throws OseeCoreException {
      Artifact artifact = getArtifact();
      if (artifact != null) {
         IAtsVersion version = cache.getSoleByGuid(artifact.getGuid(), IAtsVersion.class);
         if (version == null) {
            version =
               cache.getVersionFactory().createVersion(artifact.getName(), artifact.getGuid(),
                  artifact.getHumanReadableId());
         } else {
            version.setHumanReadableId(artifact.getHumanReadableId());
            version.setName(artifact.getName());
         }

         atsObject = version;
         version.setEstimatedReleasedDate(artifact.getSoleAttributeValue(AtsAttributeTypes.EstimatedReleaseDate,
            (Date) null));
         version.setReleasedDate(artifact.getSoleAttributeValue(AtsAttributeTypes.ReleaseDate, (Date) null));
         version.setReleased(artifact.getSoleAttributeValue(AtsAttributeTypes.Released, false));
         version.setLocked(artifact.getSoleAttributeValue(AtsAttributeTypes.VersionLocked, false));
         version.setNextVersion(artifact.getSoleAttributeValue(AtsAttributeTypes.NextVersion, false));
         version.setAllowCommitBranch(artifact.getSoleAttributeValue(AtsAttributeTypes.AllowCommitBranch, false));
         version.setAllowCreateBranch(artifact.getSoleAttributeValue(AtsAttributeTypes.AllowCreateBranch, false));
         version.setBaselineBranchGuid(artifact.getSoleAttributeValue(AtsAttributeTypes.BaselineBranchGuid, ""));
         version.setDescription(artifact.getSoleAttributeValue(AtsAttributeTypes.Description, ""));
         version.setFullName(artifact.getSoleAttributeValue(AtsAttributeTypes.FullName, ""));
         List<Artifact> teamDefArts =
            artifact.getRelatedArtifacts(AtsRelationTypes.TeamDefinitionToVersion_TeamDefinition);
         if (!teamDefArts.isEmpty()) {
            Artifact teamDefArt = teamDefArts.iterator().next();
            IAtsTeamDefinition teamDef =
               cache.getTeamDefinitionFactory().getOrCreate(teamDefArt.getGuid(), teamDefArt.getName());
            AtsVersionService.get().setTeamDefinition(version, teamDef);
         }
         for (String staticId : artifact.getAttributesToStringList(CoreAttributeTypes.StaticId)) {
            version.getStaticIds().add(staticId);
         }
         for (Artifact parallelVerArt : artifact.getRelatedArtifacts(AtsRelationTypes.ParallelVersion_Child)) {
            IAtsVersion parallelVer =
               cache.getVersionFactory().getOrCreate(parallelVerArt.getGuid(), parallelVerArt.getName());
            version.getParallelVersions().add(parallelVer);
         }

      }
   }

   public IAtsVersion getVersion() {
      return (IAtsVersion) atsObject;
   }

}
