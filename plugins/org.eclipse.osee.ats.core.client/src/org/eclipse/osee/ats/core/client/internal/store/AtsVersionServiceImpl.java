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

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.client.IAtsClient;
import org.eclipse.osee.ats.core.client.config.IAtsClientVersionService;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.ats.core.client.internal.config.AtsArtifactConfigCache;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowManager;
import org.eclipse.osee.ats.core.client.util.AtsChangeSet;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.core.util.CacheProvider;
import org.eclipse.osee.ats.core.version.AbstractAtsVersionServiceImpl;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;

/**
 * @author Donald G Dunne
 */
public class AtsVersionServiceImpl extends AbstractAtsVersionServiceImpl implements IAtsClientVersionService {

   private final CacheProvider<AtsArtifactConfigCache> cacheProvider;
   private final AtsVersionCache versionCache;
   private final IAtsClient atsClient;

   public AtsVersionServiceImpl(IAtsClient atsClient, CacheProvider<AtsArtifactConfigCache> configCacheProvider, AtsVersionCache versionCache) {
      super(atsClient.getServices());
      this.atsClient = atsClient;
      this.cacheProvider = configCacheProvider;
      this.versionCache = versionCache;
   }

   @Override
   public IAtsVersion getTargetedVersionByTeamWf(IAtsTeamWorkflow teamWf) throws OseeCoreException {
      IAtsVersion version = versionCache.getVersion(teamWf);
      if (version == null) {
         if (getArtifact(teamWf).getRelatedArtifactsCount(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version) > 0) {
            List<Artifact> verArts =
               getArtifact(teamWf).getRelatedArtifacts(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version);
            if (verArts.size() > 1) {
               OseeLog.log(Activator.class, Level.SEVERE,
                  "Multiple targeted versions for artifact " + teamWf.toStringWithId());
               version = cacheProvider.get().getSoleByUuid(verArts.iterator().next().getUuid(), IAtsVersion.class);
            } else {
               version = cacheProvider.get().getSoleByUuid(verArts.iterator().next().getUuid(), IAtsVersion.class);
            }
            versionCache.cache(teamWf, version);
         }
      }
      return version;
   }

   private Artifact getArtifact(IAtsObject object) {
      return (Artifact) object.getStoreObject();
   }

   @Override
   public void removeTargetedVersion(IAtsTeamWorkflow teamWf, boolean store) throws OseeCoreException {
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
      Artifact versionArt = atsClient.checkArtifactFromId(version.getUuid(), AtsUtilCore.getAtsBranch());
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

   @Override
   public void setTeamDefinition(IAtsVersion version, IAtsTeamDefinition teamDef) throws OseeCoreException {
      Artifact verArt = atsClient.getArtifact(version);
      if (verArt == null) {
         throw new OseeStateException("Version [%s] does not exist.", version);
      }
      Artifact teamDefArt = getArtifact(teamDef);
      if (teamDefArt == null) {
         throw new OseeStateException("Team Definition [%s] does not exist.", teamDef);
      }
      if (!verArt.getRelatedArtifacts(AtsRelationTypes.TeamDefinitionToVersion_TeamDefinition).contains(teamDefArt)) {
         verArt.addRelation(AtsRelationTypes.TeamDefinitionToVersion_TeamDefinition, teamDefArt);
      }
   }

   @Override
   public void invalidateVersionCache() {
      versionCache.invalidateCache();
   }

   @Override
   public void invalidateVersionCache(IAtsTeamWorkflow teamWf) throws OseeCoreException {
      versionCache.deCache(teamWf);
   }

   @Override
   public Branch getBranch(IAtsVersion version) {
      Branch branch = null;
      long branchUuid = getBranchId(version);
      if (branchUuid > 0) {
         branch = BranchManager.getBranch(branchUuid);
      }
      return branch;
   }

   @Override
   public IAtsVersion store(IAtsVersion version, IAtsTeamDefinition teamDef) {
      Conditions.checkNotNull(version, "version");
      Conditions.checkNotNull(teamDef, "teamDef");
      Conditions.checkNotNull(teamDef.getStoreObject(), "teamDef storeObject");
      IAtsVersion result = version;
      if (version.getStoreObject() == null) {
         Artifact verArt = cacheProvider.get().getArtifact(version);
         if (verArt == null) {
            AtsChangeSet changes = new AtsChangeSet("Create " + version);
            VersionArtifactWriter writer = new VersionArtifactWriter();
            verArt = writer.store(version, cacheProvider.get(), changes);
            changes.relate(teamDef, AtsRelationTypes.TeamDefinitionToVersion_Version, verArt);
            version.setStoreObject(verArt);
            changes.execute();
         }
      }
      return result;
   }

   @Override
   public IAtsVersion createVersion(String title, String guid, long uuid) throws OseeCoreException {
      IAtsVersion item = atsClient.getVersionFactory().createVersion(title, guid, uuid);
      AtsArtifactConfigCache cache = cacheProvider.get();
      cache.cache(item);
      return item;
   }

   @Override
   public IAtsVersion createVersion(String name) throws OseeCoreException {
      IAtsVersion item = atsClient.getVersionFactory().createVersion(name);
      AtsArtifactConfigCache cache = cacheProvider.get();
      cache.cache(item);
      return item;
   }

}
