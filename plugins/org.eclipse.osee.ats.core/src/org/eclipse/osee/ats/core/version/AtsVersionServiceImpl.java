/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.version;

import java.rmi.activation.Activator;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.AtsTopicEvent;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.IExecuteListener;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.version.IAtsVersionService;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.Identity;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

/**
 * @author Donald G Dunne
 */
public class AtsVersionServiceImpl implements IAtsVersionService {

   private final IAtsServices services;
   private final EventAdmin eventAdmin;

   public AtsVersionServiceImpl(IAtsServices services, EventAdmin eventAdmin) {
      super();
      this.services = services;
      this.eventAdmin = eventAdmin;
   }

   @Override
   public IAtsVersion getTargetedVersion(IAtsWorkItem workItem)  {
      IAtsVersion version = null;
      IAtsTeamWorkflow team = workItem.getParentTeamWorkflow();
      if (team != null) {
         version = getTargetedVersionByTeamWf(team);
      }
      return version;
   }

   @Override
   public IAtsVersion getTargetedVersionByTeamWf(IAtsTeamWorkflow team)  {
      if (team == null) {
         throw new OseeArgumentException("Team Workflow can not be null %s", team);
      }
      Collection<ArtifactToken> versions = services.getRelationResolver().getRelated(team.getStoreObject(),
         AtsRelationTypes.TeamWorkflowTargetedForVersion_Version);
      IAtsVersion version = null;
      if (!versions.isEmpty()) {
         if (versions.size() > 1) {
            OseeLog.log(Activator.class, Level.SEVERE,
               "Multiple targeted versions for artifact " + team.toStringWithId());
         } else {
            version = services.getConfigItemFactory().getVersion(versions.iterator().next());
         }
      }
      return version;
   }

   @Override
   public void removeTargetedVersion(IAtsTeamWorkflow teamWf, IAtsChangeSet changes)  {
      changes.unrelateAll(teamWf, AtsRelationTypes.TeamWorkflowTargetedForVersion_Version);
   }

   @Override
   public IAtsVersion setTargetedVersion(IAtsTeamWorkflow teamWf, IAtsVersion version, IAtsChangeSet changes) {
      Collection<ArtifactToken> previousVersions =
         services.getRelationResolver().getRelated(teamWf, AtsRelationTypes.TeamWorkflowTargetedForVersion_Version);

      ArtifactId previousVersion = ArtifactId.SENTINEL;
      if (!previousVersions.isEmpty()) {
         previousVersion = ArtifactId.valueOf(previousVersions.iterator().next().getId());
      }
      changes.setRelation(teamWf, AtsRelationTypes.TeamWorkflowTargetedForVersion_Version, version);
      changes.addExecuteListener(getPostPersistExecutionListener(teamWf, version, previousVersion));
      return version;
   }

   protected IExecuteListener getPostPersistExecutionListener(IAtsTeamWorkflow teamWf, IAtsVersion newVersion, ArtifactId previousVersion) {
      return new IExecuteListener() {

         @Override
         public void changesStored(IAtsChangeSet changes) {
            try {
               HashMap<String, Object> properties = new HashMap<String, Object>();
               properties.put(AtsTopicEvent.WORK_ITEM_UUIDS_KEY, teamWf.getIdString());
               properties.put(AtsTopicEvent.NEW_ATS_VERSION_ID, newVersion.getIdString());
               properties.put(AtsTopicEvent.PREVIOUS_ATS_VERSION_ID, previousVersion.getIdString());

               Event event = new Event(AtsTopicEvent.TARGETED_VERSION_MODIFIED, properties);

               eventAdmin.postEvent(event);

            } catch (OseeCoreException ex) {
               OseeLog.log(getClass(), Level.SEVERE, ex);
            }
         }
      };
   }

   /**
    * @return true if this is a TeamWorkflow and the version it's been targeted for has been released
    */
   @Override
   public boolean isReleased(IAtsTeamWorkflow teamWf)  {
      boolean released = false;
      IAtsVersion verArt = getTargetedVersion(teamWf);
      if (verArt != null) {
         released = verArt.isReleased();
      }
      return released;
   }

   @Override
   public boolean isVersionLocked(IAtsTeamWorkflow teamWf)  {
      boolean locked = false;
      IAtsVersion verArt = getTargetedVersion(teamWf);
      if (verArt != null) {
         locked = verArt.isVersionLocked();
      }
      return locked;
   }

   @Override
   public boolean hasTargetedVersion(IAtsWorkItem workItem)  {
      return getTargetedVersion(workItem) != null;
   }

   @Override
   public void setTeamDefinition(IAtsVersion version, IAtsTeamDefinition teamDef, IAtsChangeSet changes)  {
      Object verArt = services.getArtifact(version);
      if (verArt == null) {
         throw new OseeStateException("Version [%s] does not exist.", version);
      }
      Object teamDefArt = services.getArtifact(teamDef);
      if (teamDefArt == null) {
         throw new OseeStateException("Team Definition [%s] does not exist.", teamDef);
      }
      if (!services.getRelationResolver().areRelated(version, AtsRelationTypes.TeamDefinitionToVersion_TeamDefinition,
         teamDef)) {
         changes.relate(version, AtsRelationTypes.TeamDefinitionToVersion_TeamDefinition, teamDefArt);
      }
   }

   @Override
   public IAtsTeamDefinition getTeamDefinition(IAtsVersion version)  {
      return services.getRelationResolver().getRelatedOrNull(version,
         AtsRelationTypes.TeamDefinitionToVersion_TeamDefinition, IAtsTeamDefinition.class);
   }

   @Override
   public IAtsVersion getById(Identity<String> id)  {
      IAtsVersion version = null;
      Object verArt = null;
      if (id instanceof ArtifactId) {
         verArt = services.getArtifact(((ArtifactId) id).getUuid());
      } else {
         verArt = services.getArtifactById(id.getGuid());
      }
      if (verArt != null) {
         version = services.getConfigItemFactory().getVersion((ArtifactId) verArt);
      }
      return version;
   }

   @Override
   public Collection<IAtsTeamWorkflow> getTargetedForTeamWorkflows(IAtsVersion verArt)  {
      List<IAtsTeamWorkflow> teamWorkflows = new LinkedList<>();
      for (IAtsTeamWorkflow teamWf : services.getRelationResolver().getRelated(verArt,
         AtsRelationTypes.TeamWorkflowTargetedForVersion_Workflow, IAtsTeamWorkflow.class)) {
         teamWorkflows.add(teamWf);
      }
      return teamWorkflows;
   }

   @Override
   public BranchId getBranch(IAtsVersion version) {
      String branchId =
         services.getAttributeResolver().getSoleAttributeValue(version, AtsAttributeTypes.BaselineBranchUuid, "");
      if (branchId == null || branchId.isEmpty()) {
         return BranchId.SENTINEL;
      }
      return BranchId.valueOf(branchId);
   }

   @Override
   public IAtsVersion createVersion(IAtsProgram program, String versionName, IAtsChangeSet changes) {
      IAtsVersion version = null;
      version = services.getProgramService().getVersion(program, versionName);
      if (version == null) {
         version =
            services.getConfigItemFactory().getVersion(changes.createArtifact(AtsArtifactTypes.Version, versionName));
      }
      return version;
   }

   @Override
   public IAtsVersion getVersion(IAtsProgram program, String versionName, IAtsChangeSet changes) {
      return services.getProgramService().getVersion(program, versionName);
   }

   @Override
   public IAtsVersion createVersion(String title, long uuid, IAtsChangeSet changes)  {
      return services.getVersionFactory().createVersion(title, uuid, changes, services);
   }

   @Override
   public IAtsVersion createVersion(String name, IAtsChangeSet changes)  {
      return services.getVersionFactory().createVersion(name, changes, services);
   }

   @Override
   public Collection<IAtsVersion> getVersions(IAtsTeamDefinition teamDef) {
      return services.getTeamDefinitionService().getVersions(teamDef);
   }

}
