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
package org.eclipse.osee.ats.core.client.internal.store;

import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.version.IVersionFactory;
import org.eclipse.osee.ats.core.client.config.IAtsClientVersionService;
import org.eclipse.osee.ats.core.client.internal.config.AtsArtifactConfigCache;
import org.eclipse.osee.ats.core.config.IActionableItemFactory;
import org.eclipse.osee.ats.core.config.ITeamDefinitionFactory;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class VersionArtifactReader extends AbstractAtsArtifactReader<IAtsVersion> {

   private final IAtsClientVersionService versionService;

   public VersionArtifactReader(IActionableItemFactory actionableItemFactory, ITeamDefinitionFactory teamDefFactory, IVersionFactory versionFactory, IAtsClientVersionService versionService) {
      super(actionableItemFactory, teamDefFactory, versionFactory);
      this.versionService = versionService;
   }

   @Override
   public IAtsVersion load(AtsArtifactConfigCache cache, Artifact artifact) throws OseeCoreException {
      IAtsVersion version = getOrCreateVersion(cache, artifact);

      version.setEstimatedReleasedDate(
         artifact.getSoleAttributeValue(AtsAttributeTypes.EstimatedReleaseDate, (Date) null));
      version.setReleasedDate(artifact.getSoleAttributeValue(AtsAttributeTypes.ReleaseDate, (Date) null));
      version.setReleased(artifact.getSoleAttributeValue(AtsAttributeTypes.Released, false));
      version.setLocked(artifact.getSoleAttributeValue(AtsAttributeTypes.VersionLocked, false));
      version.setNextVersion(artifact.getSoleAttributeValue(AtsAttributeTypes.NextVersion, false));
      version.setAllowCommitBranch(artifact.getSoleAttributeValue(AtsAttributeTypes.AllowCommitBranch, false));
      version.setAllowCreateBranch(artifact.getSoleAttributeValue(AtsAttributeTypes.AllowCreateBranch, false));
      version.setBaselineBranchUuid(artifact.getSoleAttributeValue(AtsAttributeTypes.BaselineBranchUuid, ""));
      version.setDescription(artifact.getSoleAttributeValue(AtsAttributeTypes.Description, ""));
      version.setFullName(artifact.getSoleAttributeValue(AtsAttributeTypes.FullName, ""));
      List<Artifact> teamDefArts =
         artifact.getRelatedArtifacts(AtsRelationTypes.TeamDefinitionToVersion_TeamDefinition);
      if (!teamDefArts.isEmpty()) {
         Artifact teamDefArt = teamDefArts.iterator().next();
         IAtsTeamDefinition teamDef = getOrCreateTeamDefinition(cache, teamDefArt);
         versionService.setTeamDefinition(version, teamDef);
      }
      for (String staticId : artifact.getAttributesToStringList(CoreAttributeTypes.StaticId)) {
         version.getStaticIds().add(staticId);
      }
      for (Artifact parallelVerArt : artifact.getRelatedArtifacts(AtsRelationTypes.ParallelVersion_Child)) {
         IAtsVersion parallelVer = getOrCreateVersion(cache, parallelVerArt);
         version.getParallelVersions().add(parallelVer);
      }
      return version;
   }
}
