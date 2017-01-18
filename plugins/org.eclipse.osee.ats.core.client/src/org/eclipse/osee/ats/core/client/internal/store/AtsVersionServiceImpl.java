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
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.config.IAtsCache;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.client.IAtsClient;
import org.eclipse.osee.ats.core.client.config.IAtsClientVersionService;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.version.AbstractAtsVersionServiceImpl;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.osgi.service.event.EventAdmin;

/**
 * @author Donald G Dunne
 */
public class AtsVersionServiceImpl extends AbstractAtsVersionServiceImpl implements IAtsClientVersionService {

   private final IAtsCache cache;
   private final IAtsClient atsClient;

   public AtsVersionServiceImpl(IAtsClient atsClient, IAtsCache cache, EventAdmin eventAdmin) {
      super(atsClient.getServices(), eventAdmin);
      this.atsClient = atsClient;
      this.cache = cache;
   }

   @Override
   public IAtsVersion getTargetedVersionByTeamWf(IAtsTeamWorkflow teamWf) throws OseeCoreException {
      IAtsVersion version = null;
      if (getArtifact(teamWf).getRelatedArtifactsCount(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version) > 0) {
         List<Artifact> verArts =
            getArtifact(teamWf).getRelatedArtifacts(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version);
         if (verArts.size() > 1) {
            OseeLog.log(Activator.class, Level.SEVERE,
               "Multiple targeted versions for artifact " + teamWf.toStringWithId());
            version = cache.getAtsObject(verArts.iterator().next().getId());
         } else {
            version = cache.getAtsObject(verArts.iterator().next().getId());
         }
      }
      return version;
   }

   private Artifact getArtifact(IAtsObject object) {
      return (Artifact) object.getStoreObject();
   }

   @Override
   public void removeTargetedVersion(IAtsTeamWorkflow teamWf) throws OseeCoreException {
      TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) teamWf.getStoreObject();
      teamArt.deleteRelations(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version);
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
   public BranchId getBranch(IAtsVersion version) {
      return getBranchId(version);
   }

   @Override
   public IAtsVersion createVersion(String title, String guid, long uuid, IAtsChangeSet changes) throws OseeCoreException {
      IAtsVersion item = atsClient.getVersionFactory().createVersion(title, guid, uuid, changes, atsClient);
      cache.cacheAtsObject(item);
      return item;
   }

   @Override
   public IAtsVersion createVersion(String name, IAtsChangeSet changes) throws OseeCoreException {
      IAtsVersion item = atsClient.getVersionFactory().createVersion(name, changes, atsClient);
      cache.cacheAtsObject(item);
      return item;
   }

   @Override
   public Collection<IAtsVersion> getVersions(IAtsTeamDefinition teamDef) {
      return atsClient.getTeamDefinitionService().getVersions(teamDef);
   }
}
