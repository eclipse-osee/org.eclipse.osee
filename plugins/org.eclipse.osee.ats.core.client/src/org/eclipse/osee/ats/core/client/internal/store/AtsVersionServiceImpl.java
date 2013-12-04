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
package org.eclipse.osee.ats.core.client.internal.store;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.client.IAtsVersionAdmin;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.ats.core.client.internal.IAtsArtifactStore;
import org.eclipse.osee.ats.core.client.internal.config.AtsArtifactConfigCache;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowManager;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.core.util.CacheProvider;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.jdk.core.type.Identity;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Donald G Dunne
 */
public class AtsVersionServiceImpl implements IAtsVersionAdmin {

   private final CacheProvider<AtsArtifactConfigCache> cacheProvider;
   private final IAtsArtifactStore artifactStore;
   private final AtsVersionCache versionCache;

   public AtsVersionServiceImpl(CacheProvider<AtsArtifactConfigCache> configCacheProvider, IAtsArtifactStore artifactStore, AtsVersionCache versionCache) {
      super();
      this.cacheProvider = configCacheProvider;
      this.artifactStore = artifactStore;
      this.versionCache = versionCache;
   }

   @Override
   public IAtsVersion getTargetedVersion(Object object) throws OseeCoreException {
      IAtsVersion version = null;
      if (object instanceof AbstractWorkflowArtifact) {
         TeamWorkFlowArtifact teamArt = ((AbstractWorkflowArtifact) object).getParentTeamWorkflow();
         if (teamArt != null) {
            version = getTargetedVersionByTeamWf(teamArt);
         }
      }
      return version;
   }

   private IAtsVersion getTargetedVersionByTeamWf(TeamWorkFlowArtifact teamWf) throws OseeCoreException {
      IAtsVersion version = versionCache.getVersion(teamWf);
      if (version == null) {
         if (teamWf.getRelatedArtifactsCount(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version) > 0) {
            List<Artifact> verArts =
               teamWf.getRelatedArtifacts(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version);
            if (verArts.size() > 1) {
               OseeLog.log(Activator.class, Level.SEVERE,
                  "Multiple targeted versions for artifact " + teamWf.toStringWithId());
               version = cacheProvider.get().getSoleByGuid(verArts.iterator().next().getGuid(), IAtsVersion.class);
            } else {
               version = cacheProvider.get().getSoleByGuid(verArts.iterator().next().getGuid(), IAtsVersion.class);
            }
            versionCache.cache(teamWf, version);
         }
      }
      return version;
   }

   @Override
   public void removeTargetedVersion(IAtsTeamWorkflow teamWf) throws OseeCoreException {
      removeTargetedVersion(teamWf, false);
   }

   private void removeTargetedVersion(IAtsTeamWorkflow teamWf, boolean store) throws OseeCoreException {
      if (store) {
         TeamWorkFlowArtifact teamArt = TeamWorkFlowManager.getTeamWorkflowArt(teamWf);
         teamArt.deleteRelations(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version);
      }
      versionCache.deCache(teamWf);
   }

   @Override
   public IAtsVersion setTargetedVersion(IAtsTeamWorkflow teamWf, IAtsVersion version) throws OseeCoreException {
      return setTargetedVersion(teamWf, version, false);
   }

   @Override
   public IAtsVersion setTargetedVersionAndStore(IAtsTeamWorkflow teamWf, IAtsVersion version) throws OseeCoreException {
      return setTargetedVersion(teamWf, version, true);
   }

   private IAtsVersion setTargetedVersion(IAtsTeamWorkflow teamWf, IAtsVersion version, boolean store) throws OseeCoreException {
      if (store) {
         setTargetedVersionLink(teamWf, version);
      }

      IAtsVersion toReturn = null;
      if (version == null) {
         versionCache.deCache(teamWf);
      } else {
         toReturn = versionCache.cache(teamWf, version);
      }
      return toReturn;
   }

   private IAtsTeamWorkflow setTargetedVersionLink(IAtsTeamWorkflow teamWf, IAtsVersion version) throws OseeCoreException {
      Artifact versionArt = ArtifactQuery.checkArtifactFromId(version.getGuid(), AtsUtilCore.getAtsBranchToken());
      if (versionArt != null) {
         TeamWorkFlowArtifact teamArt = TeamWorkFlowManager.getTeamWorkflowArt(teamWf);
         if (teamArt != null) {
            teamArt.setRelations(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version,
               Collections.singleton(versionArt));
            return teamArt;
         } else {
            throw new OseeStateException("Team Workflow artifact does not exist [%s]", teamWf.toStringWithId());
         }
      } else {
         throw new OseeStateException("Version artifact does not exist [%s]", version.toStringWithId());
      }
   }

   /**
    * @return true if this is a TeamWorkflow and the version it's been targeted for has been released
    */
   @Override
   public boolean isReleased(IAtsTeamWorkflow teamWf) throws OseeCoreException {
      boolean released = false;
      IAtsVersion verArt = getTargetedVersion(teamWf);
      if (verArt != null) {
         released = verArt.isReleased();
      }
      return released;
   }

   @Override
   public boolean isVersionLocked(IAtsTeamWorkflow teamWf) throws OseeCoreException {
      boolean locked = false;
      IAtsVersion verArt = getTargetedVersion(teamWf);
      if (verArt != null) {
         locked = verArt.isVersionLocked();
      }
      return locked;
   }

   @Override
   public boolean hasTargetedVersion(Object object) throws OseeCoreException {
      return getTargetedVersion(object) != null;
   }

   private <T extends IAtsConfigObject> T loadFromStore(Artifact art) throws OseeCoreException {
      AtsArtifactConfigCache atsConfigCache = cacheProvider.get();
      return artifactStore.load(atsConfigCache, art);
   }

   @Override
   public void setTeamDefinition(IAtsVersion version, IAtsTeamDefinition teamDef) throws OseeCoreException {
      Artifact verArt = ArtifactQuery.getArtifactFromId(version.getGuid(), AtsUtilCore.getAtsBranchToken());
      if (verArt == null) {
         throw new OseeStateException("Version [%s] does not exist.", version);
      }
      Artifact teamDefArt = ArtifactQuery.getArtifactFromId(teamDef.getGuid(), AtsUtilCore.getAtsBranchToken());
      if (teamDefArt == null) {
         throw new OseeStateException("Team Definition [%s] does not exist.", teamDef);
      }
      if (!verArt.getRelatedArtifacts(AtsRelationTypes.TeamDefinitionToVersion_TeamDefinition).contains(teamDefArt)) {
         verArt.addRelation(AtsRelationTypes.TeamDefinitionToVersion_TeamDefinition, teamDefArt);
      }
   }

   @Override
   public IAtsTeamDefinition getTeamDefinition(IAtsVersion version) throws OseeCoreException {
      IAtsTeamDefinition result = null;
      Artifact verArt = ArtifactQuery.getArtifactFromId(version.getGuid(), AtsUtilCore.getAtsBranchToken());
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
            result = loadFromStore(teamDefArt);
         }
      }
      return result;
   }

   @Override
   public IAtsVersion getById(Identity<String> id) throws OseeCoreException {
      IAtsVersion version = null;
      Artifact verArt = ArtifactQuery.getArtifactFromId(id.getGuid(), AtsUtilCore.getAtsBranchToken());
      if (verArt != null) {
         version = loadFromStore(verArt);
      }
      return version;
   }

   @Override
   public Collection<IAtsTeamWorkflow> getTargetedForTeamWorkflows(IAtsVersion version) throws OseeCoreException {
      Collection<IAtsTeamWorkflow> toReturn = new LinkedList<IAtsTeamWorkflow>();
      Collection<TeamWorkFlowArtifact> tmWfs = getTargetedForTeamWorkflowArtifacts(version);
      for (TeamWorkFlowArtifact tmWf : tmWfs) {
         IAtsTeamWorkflow atsTmWf = loadFromStore(tmWf);
         toReturn.add(atsTmWf);
      }
      return toReturn;
   }

   @Override
   public Collection<TeamWorkFlowArtifact> getTargetedForTeamWorkflowArtifacts(IAtsVersion verArt) throws OseeCoreException {
      Artifact artifact = cacheProvider.get().getArtifact(verArt);
      List<TeamWorkFlowArtifact> teamWorkflows;
      if (artifact != null) {
         teamWorkflows =
            artifact.getRelatedArtifactsOfType(AtsRelationTypes.TeamWorkflowTargetedForVersion_Workflow,
               TeamWorkFlowArtifact.class);
      } else {
         teamWorkflows = Collections.emptyList();
      }
      return teamWorkflows;
   }

   @Override
   public void invalidateVersionCache() {
      versionCache.invalidateCache();
   }

   @Override
   public void invalidateVersionCache(IAtsTeamWorkflow teamWf) throws OseeCoreException {
      versionCache.deCache(teamWf);
   }

}
