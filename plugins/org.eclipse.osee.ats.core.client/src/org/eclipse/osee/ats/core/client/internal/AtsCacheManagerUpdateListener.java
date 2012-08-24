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
package org.eclipse.osee.ats.core.client.internal;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.core.client.config.AtsBulkLoad;
import org.eclipse.osee.ats.core.client.task.AbstractTaskableArtifact;
import org.eclipse.osee.ats.core.client.task.TaskArtifact;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.util.AtsTaskCache;
import org.eclipse.osee.ats.core.config.AtsConfigCache;
import org.eclipse.osee.ats.core.config.AtsVersionService;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.event.EventUtil;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidRelation;
import org.eclipse.osee.framework.skynet.core.event.model.EventModType;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.skynet.core.utility.DbUtil;

/**
 * Updates ATS Cache based on Artifact Events. Registers for service via ats.cache.update.listener osgi registration.
 * 
 * @author Donald G. Dunne
 */
public class AtsCacheManagerUpdateListener implements IArtifactEventListener {

   @Override
   public List<? extends IEventFilter> getEventFilters() {
      return Arrays.asList(EventUtil.getCommonBranchFilter());
   }

   @Override
   public void handleArtifactEvent(ArtifactEvent artifactEvent, Sender sender) {
      if (!DbUtil.isDbInit()) {
         processArtifacts(artifactEvent);
         processRelations(artifactEvent);
      }
   }

   private void processRelations(ArtifactEvent artifactEvent) {
      // TODO AtsBulkLoad.reloadConfig(false) if config relation modified
      for (EventBasicGuidRelation guidRel : artifactEvent.getRelations()) {
         try {
            if (guidRel.is(AtsRelationTypes.TeamWorkflowTargetedForVersion_Workflow)) {
               for (TeamWorkFlowArtifact teamArt : ArtifactCache.getActive(guidRel, TeamWorkFlowArtifact.class)) {
                  Artifact verArt = null;
                  try {
                     verArt = teamArt.getRelatedArtifact(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version);
                  } catch (ArtifactDoesNotExist ex) {
                     // do nothing
                  }
                  if (verArt == null) {
                     AtsVersionService.get().removeTargetedVersion(teamArt);
                  } else {
                     IAtsVersion version = AtsConfigCache.getSoleByGuid(verArt.getGuid(), IAtsVersion.class);
                     AtsVersionService.get().setTargetedVersion(teamArt, version);
                  }
               }
            }
            if (guidRel.is(AtsRelationTypes.SmaToTask_Task)) {
               for (TaskArtifact taskArt : ArtifactCache.getActive(guidRel, TaskArtifact.class)) {
                  AtsTaskCache.decache(taskArt.getParent());
               }
               for (Artifact artifact : ArtifactCache.getActive(guidRel)) {
                  if (artifact instanceof AbstractTaskableArtifact) {
                     AtsTaskCache.decache(artifact);
                  }
               }
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
   }

   private void processArtifacts(ArtifactEvent artifactEvent) {
      for (EventBasicGuidArtifact guidArt : artifactEvent.getArtifacts()) {
         if (isConfigArtifactType(guidArt)) {
            AtsBulkLoad.reloadConfig(false);
            break;
         }
      }
      for (EventBasicGuidArtifact guidArt : artifactEvent.getArtifacts()) {
         try {
            if (guidArt.is(EventModType.Deleted, EventModType.Purged)) {
               handleTaskCacheForDeletedPurged(guidArt);
            }
            if (guidArt.is(EventModType.Added, EventModType.Modified)) {
               handleTaskCacheForAddedModified(guidArt);
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
   }

   private boolean isConfigArtifactType(EventBasicGuidArtifact guidArt) {
      return guidArt.getArtTypeGuid().equals(AtsArtifactTypes.Version.getGuid()) || guidArt.getArtTypeGuid().equals(
         AtsArtifactTypes.TeamDefinition.getGuid()) || guidArt.getArtTypeGuid().equals(
         AtsArtifactTypes.ActionableItem.getGuid());
   }

   private void handleTaskCacheForAddedModified(EventBasicGuidArtifact guidArt) throws OseeCoreException {
      // Only process if in cache
      Artifact artifact = ArtifactCache.getActive(guidArt);
      if (artifact != null && guidArt.is(EventModType.Added)) {
         if (artifact.isOfType(AtsArtifactTypes.Task)) {
            AtsTaskCache.decache(artifact.getParent());
         }
         if (artifact instanceof AbstractTaskableArtifact) {
            AtsTaskCache.decache(artifact);
         }
      }
   }

   private void handleTaskCacheForDeletedPurged(EventBasicGuidArtifact guidArt) throws OseeCoreException {
      if (guidArt.is(AtsArtifactTypes.Task) && guidArt.is(EventModType.Deleted)) {
         Artifact artifact = ArtifactCache.getActive(guidArt);
         if (artifact != null) {
            AtsTaskCache.decache(artifact.getParent());
         }
      }
      Artifact artifact = ArtifactCache.getActive(guidArt);
      if (artifact instanceof AbstractTaskableArtifact) {
         AtsTaskCache.decache(artifact);
      }
   }

}
