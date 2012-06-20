/*
 * Created on Jun 6, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.client.config.store;

import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.core.client.util.AtsUtilCore;
import org.eclipse.osee.ats.core.config.AtsConfigCache;
import org.eclipse.osee.ats.core.config.TeamDefinitionFactory;
import org.eclipse.osee.ats.core.config.VersionFactory;
import org.eclipse.osee.ats.core.model.IAtsTeamDefinition;
import org.eclipse.osee.ats.core.model.IAtsVersion;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

public class VersionArtifactStore extends ArtifactAtsObjectStore {

   public VersionArtifactStore(IAtsVersion version) {
      super(version, AtsArtifactTypes.Version, AtsUtilCore.getAtsBranchToken());
   }

   public VersionArtifactStore(Artifact artifact) throws OseeCoreException {
      super(null, AtsArtifactTypes.Version, AtsUtilCore.getAtsBranchToken());
      this.artifact = artifact;
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
         verArt.setSoleAttributeValue(AtsAttributeTypes.NextVersion, next);
      }
      boolean released = verArt.getSoleAttributeValue(AtsAttributeTypes.Released, false);
      if (released != version.isReleased()) {
         verArt.setSoleAttributeValue(AtsAttributeTypes.Released, released);
      }
      boolean versionLocked = verArt.getSoleAttributeValue(AtsAttributeTypes.VersionLocked, false);
      if (versionLocked != version.isLocked()) {
         verArt.setSoleAttributeValue(AtsAttributeTypes.VersionLocked, versionLocked);
      }
      if (Strings.isValid(version.getBaslineBranchGuid())) {
         verArt.setSoleAttributeValue(AtsAttributeTypes.BaselineBranchGuid, version.getBaslineBranchGuid());
      }
      if (Strings.isValid(version.getDescription())) {
         verArt.setSoleAttributeValue(AtsAttributeTypes.Description, version.getDescription());
      }
      if (Strings.isValid(version.getFullName())) {
         verArt.setSoleAttributeValue(AtsAttributeTypes.FullName, version.getFullName());
      }

      // set parent artifact to top team def
      if (version.getTeamDefinition() != null) {
         Artifact teamDefArt = new TeamDefinitionArtifactStore(version.getTeamDefinition()).getArtifact();
         if (teamDefArt != null) {
            if (!teamDefArt.getRelatedArtifacts(AtsRelationTypes.TeamDefinitionToVersion_Version).contains(verArt)) {
               teamDefArt.addRelation(AtsRelationTypes.TeamDefinitionToVersion_Version, verArt);
               teamDefArt.persist(transaction);
            }
         }
      }
      verArt.persist(transaction);
      return Result.TrueResult;
   }

   public void loadFromArtifact() throws OseeCoreException {
      Artifact artifact = getArtifact();
      if (artifact != null) {
         IAtsVersion version = AtsConfigCache.getSoleByGuid(artifact.getGuid(), IAtsVersion.class);
         if (version == null) {
            version =
               VersionFactory.createVersion(artifact.getName(), artifact.getGuid(), artifact.getHumanReadableId());
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
         version.setAllowCommitBranch(artifact.getSoleAttributeValue(AtsAttributeTypes.AllowCommitBranch, true));
         version.setAllowCreateBranch(artifact.getSoleAttributeValue(AtsAttributeTypes.AllowCreateBranch, true));
         version.setBaselineBranchGuid(artifact.getSoleAttributeValue(AtsAttributeTypes.BaselineBranchGuid, ""));
         version.setDescription(artifact.getSoleAttributeValue(AtsAttributeTypes.Description, ""));
         version.setFullName(artifact.getSoleAttributeValue(AtsAttributeTypes.FullName, ""));
         List<Artifact> teamDefArts =
            artifact.getRelatedArtifacts(AtsRelationTypes.TeamDefinitionToVersion_TeamDefinition);
         if (!teamDefArts.isEmpty()) {
            Artifact teamDefArt = teamDefArts.iterator().next();
            IAtsTeamDefinition teamDef = TeamDefinitionFactory.getOrCreate(teamDefArt.getGuid(), teamDefArt.getName());
            version.setTeamDefinition(teamDef);
         }
         for (String staticId : artifact.getAttributesToStringList(CoreAttributeTypes.StaticId)) {
            version.getStaticIds().add(staticId);
         }
         for (Artifact parallelVerArt : artifact.getRelatedArtifacts(AtsRelationTypes.ParallelVersion_Child)) {
            IAtsVersion parallelVer = VersionFactory.getOrCreate(parallelVerArt.getGuid(), parallelVerArt.getName());
            version.getParallelVersions().add(parallelVer);
         }

      }
   }

   public IAtsVersion getVersion() {
      return (IAtsVersion) atsObject;
   }

}
