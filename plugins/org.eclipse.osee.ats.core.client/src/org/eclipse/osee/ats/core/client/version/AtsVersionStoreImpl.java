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
package org.eclipse.osee.ats.core.client.version;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.version.IAtsVersionStore;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.client.config.store.TeamDefinitionArtifactStore;
import org.eclipse.osee.ats.core.client.config.store.VersionArtifactStore;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowManager;
import org.eclipse.osee.ats.core.client.util.AtsUtilCore;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.config.AtsConfigCache;
import org.eclipse.osee.ats.core.config.AtsVersionService;
import org.eclipse.osee.framework.core.data.Identity;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Donald G. Dunne
 */
public class AtsVersionStoreImpl implements IAtsVersionStore {

   public AtsVersionStoreImpl() {
   }

   @Override
   public IAtsTeamWorkflow removeTargetedVersionLink(IAtsTeamWorkflow teamWf) throws OseeCoreException {
      TeamWorkFlowArtifact teamArt = TeamWorkFlowManager.getTeamWorkflowArt(teamWf);
      teamArt.deleteRelations(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version);
      AtsVersionService.get().removeTargetedVersion(teamWf);
      return teamWf;
   }

   @Override
   public IAtsTeamWorkflow setTargetedVersionLink(IAtsTeamWorkflow teamWf, IAtsVersion version) throws OseeCoreException {
      VersionArtifactStore store = new VersionArtifactStore(version);
      Artifact versionArt = store.getArtifact();
      if (versionArt != null) {
         TeamWorkFlowArtifact teamArt = TeamWorkFlowManager.getTeamWorkflowArt(teamWf);
         if (teamArt != null) {
            teamArt.setRelations(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version,
               Collections.singleton(versionArt));
            AtsVersionService.get().setTargetedVersion(teamWf, version);
            return teamArt;
         } else {
            throw new OseeStateException("Team Workflow artifact does not exist [%s]", teamWf.toStringWithId());
         }
      } else {
         throw new OseeStateException("Version artifact does not exist [%s]", version.toStringWithId());
      }
   }

   @Override
   public IAtsVersion getTargetedVersion(Object object) throws OseeCoreException {
      IAtsVersion version = null;
      if (object instanceof AbstractWorkflowArtifact) {
         TeamWorkFlowArtifact teamArt = ((AbstractWorkflowArtifact) object).getParentTeamWorkflow();
         if (teamArt != null) {
            if (teamArt.getRelatedArtifactsCount(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version) > 0) {
               List<Artifact> verArts =
                  teamArt.getRelatedArtifacts(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version);
               if (verArts.size() > 1) {
                  OseeLog.log(Activator.class, Level.SEVERE,
                     "Multiple targeted versions for artifact " + teamArt.toStringWithId());
                  version =
                     AtsConfigCache.instance.getSoleByGuid(verArts.iterator().next().getGuid(), IAtsVersion.class);
               } else {
                  version =
                     AtsConfigCache.instance.getSoleByGuid(verArts.iterator().next().getGuid(), IAtsVersion.class);
               }
            }
         }
      }
      return version;
   }

   @Override
   public void setTeamDefinition(IAtsVersion version, IAtsTeamDefinition teamDef) throws OseeCoreException {
      VersionArtifactStore verStore = new VersionArtifactStore(version);
      Artifact verArt = verStore.getArtifact();
      if (verArt == null) {
         throw new OseeStateException("Version [%s] does not exist.", version);
      }
      TeamDefinitionArtifactStore teamDefStore = new TeamDefinitionArtifactStore(teamDef);
      Artifact teamDefArt = teamDefStore.getArtifact();
      if (teamDefArt == null) {
         throw new OseeStateException("Team Definition [%s] does not exist.", teamDef);
      }
      if (!verArt.getRelatedArtifacts(AtsRelationTypes.TeamDefinitionToVersion_TeamDefinition).contains(teamDefArt)) {
         verArt.addRelation(AtsRelationTypes.TeamDefinitionToVersion_TeamDefinition, teamDefArt);
      }
   }

   @Override
   public Collection<IAtsVersion> getVersions(IAtsTeamDefinition teamDef) throws OseeCoreException {
      List<IAtsVersion> versions = new ArrayList<IAtsVersion>();
      TeamDefinitionArtifactStore teamDefStore = new TeamDefinitionArtifactStore(teamDef);
      Artifact teamDefArt = teamDefStore.getArtifact();
      if (teamDefArt == null) {
         throw new OseeStateException("Team Definition [%s] does not exist.", teamDef);
      }
      for (Artifact verArt : teamDefArt.getRelatedArtifacts(AtsRelationTypes.TeamDefinitionToVersion_Version)) {
         IAtsVersion version = AtsConfigCache.instance.getSoleByGuid(verArt.getGuid(), IAtsVersion.class);
         versions.add(version);
      }
      return versions;
   }

   @Override
   public IAtsTeamDefinition getTeamDefinition(IAtsVersion version) throws OseeCoreException {
      IAtsTeamDefinition result = null;
      VersionArtifactStore store = new VersionArtifactStore(version);
      Artifact verArt = store.getArtifact();
      if (verArt != null) {
         Artifact teamDefArt = null;
         try {
            teamDefArt = verArt.getRelatedArtifact(AtsRelationTypes.TeamDefinitionToVersion_TeamDefinition);
         } catch (ArtifactDoesNotExist ex) {
            if (!verArt.isDeleted() && verArt.isInDb()) {
               OseeLog.logf(Activator.class, Level.SEVERE, "Version [%s] has no related team defininition",
                  verArt.toStringWithId());
            }
         }
         if (teamDefArt != null) {
            result = AtsConfigCache.instance.getSoleByGuid(teamDefArt.getGuid(), IAtsTeamDefinition.class);
         }
      }
      return result;
   }

   @Override
   public IAtsVersion getById(Identity<String> id) throws OseeCoreException {
      IAtsVersion result = null;
      Artifact verArt = ArtifactQuery.getArtifactFromId(id.getGuid(), AtsUtilCore.getAtsBranchToken());
      if (verArt != null) {
         VersionArtifactStore store = new VersionArtifactStore(verArt, AtsConfigCache.instance);
         result = store.getVersion();
      }
      return result;
   }
}
