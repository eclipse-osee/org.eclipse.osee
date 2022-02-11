/*********************************************************************
 * Copyright (c) 2012 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.util.internal;

import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.ActionableItem;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.TeamDefinition;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.Version;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.AtsUtilClient;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactTopicEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactTopicEvent;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidRelation;
import org.eclipse.osee.framework.skynet.core.event.model.EventModType;
import org.eclipse.osee.framework.skynet.core.event.model.EventTopicArtifactTransfer;
import org.eclipse.osee.framework.skynet.core.event.model.EventTopicRelationTransfer;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.skynet.core.topic.event.filter.ITopicEventFilter;
import org.eclipse.osee.framework.skynet.core.utility.DbUtil;

/**
 * Updates ATS Cache based on Artifact Events. Registers for accessControlService via ats.cache.update.listener osgi
 * registration.
 *
 * @author Donald G. Dunne
 */
public class AtsCacheManagerUpdateListener implements IArtifactEventListener, IArtifactTopicEventListener {

   private static List<Long> configReloadRelationTypeGuids = Arrays.asList(
      AtsRelationTypes.ActionableItemLead_Lead.getGuid(), AtsRelationTypes.TeamDefinitionToVersion_Version.getGuid(),
      AtsRelationTypes.TeamActionableItem_TeamDefinition.getGuid(), AtsRelationTypes.TeamLead_Team.getGuid(),
      AtsRelationTypes.ParallelVersion_Child.getGuid(), AtsRelationTypes.ParallelVersion_Parent.getGuid());
   private Boolean singleServerDeployment;
   private OrcsTokenService tokenService;

   public void setOrcsTokenService(OrcsTokenService tokenService) {
      this.tokenService = tokenService;
   }

   @Override
   public List<? extends IEventFilter> getEventFilters() {
      return Arrays.asList(AtsUtilClient.getAtsBranchFilter());
   }

   @Override
   public List<? extends ITopicEventFilter> getTopicEventFilters() {
      return Arrays.asList(AtsUtilClient.getAtsTopicBranchFilter());
   }

   @Override
   public void handleArtifactEvent(ArtifactEvent artifactEvent, Sender sender) {
      if (!DbUtil.isDbInit() && !AtsUtil.isInTest() && isSingleServerDeployment()) {
         boolean handledConfigReload = processArtifacts(artifactEvent, sender);
         if (!handledConfigReload) {
            processRelations(artifactEvent, handledConfigReload);
         }
      }
   }

   @Override
   public void handleArtifactTopicEvent(ArtifactTopicEvent artifactTopicEvent, Sender sender) {
      if (!DbUtil.isDbInit() && !AtsUtil.isInTest() && isSingleServerDeployment()) {
         boolean handledConfigReload = processArtifacts(artifactTopicEvent, sender);
         if (!handledConfigReload) {
            processRelations(artifactTopicEvent, handledConfigReload);
         }
      }
   }

   private boolean isSingleServerDeployment() {
      if (singleServerDeployment == null) {
         singleServerDeployment = AtsApiService.get().isSingleServerDeployment();
      }
      return singleServerDeployment;
   }

   private void processRelations(ArtifactEvent artifactEvent, boolean handledConfigReload) {
      if (!handledConfigReload) {
         // update cache
         for (EventBasicGuidRelation guidRel : artifactEvent.getRelations()) {
            try {
               RelationTypeToken typeByGuid = tokenService.getRelationType(guidRel.getRelTypeGuid());
               if (configReloadRelationTypeGuids.contains(typeByGuid.getId())) {
                  AtsApiService.get().reloadServerAndClientCaches();
                  break;
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      }
   }

   private void processRelations(ArtifactTopicEvent artifactTopicEvent, boolean handledConfigReload) {
      if (!handledConfigReload) {
         // update cache
         for (EventTopicRelationTransfer guidRel : artifactTopicEvent.getRelations()) {
            try {
               RelationTypeToken typeByGuid = tokenService.getRelationType(guidRel.getRelTypeId());
               if (configReloadRelationTypeGuids.contains(typeByGuid.getId())) {
                  AtsApiService.get().reloadServerAndClientCaches();
                  break;
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      }
   }

   private boolean processArtifacts(ArtifactEvent artifactEvent, Sender sender) {
      boolean reload = false;
      for (EventBasicGuidArtifact guidArt : artifactEvent.getArtifacts()) {
         if (guidArt.getArtifactType().matches(Version, TeamDefinition, ActionableItem)) {
            reload = true;
            break;
         }
      }

      if (reload) {
         try {
            AtsApiService.get().reloadServerAndClientCaches();
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }

      for (EventBasicGuidArtifact guidArt : artifactEvent.getArtifacts()) {
         try {
            if (guidArt.is(EventModType.Added, EventModType.Modified)) {
               if (sender.isRemote()) {
                  handleCachesForAddedModified(guidArt);
               }
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      return reload;
   }

   private boolean processArtifacts(ArtifactTopicEvent artifactTopicEvent, Sender sender) {
      boolean reload = false;
      for (EventTopicArtifactTransfer guidArt : artifactTopicEvent.getTransferArtifacts()) {
         if (guidArt.getArtifactTypeId().matches(Version, TeamDefinition, ActionableItem)) {
            reload = true;
            break;
         }
      }

      if (reload) {
         try {
            AtsApiService.get().reloadServerAndClientCaches();
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }

      for (EventBasicGuidArtifact guidArt : artifactTopicEvent.getLegacyArtifacts()) {
         try {
            if (guidArt.is(EventModType.Added, EventModType.Modified)) {
               if (sender.isRemote()) {
                  handleCachesForAddedModified(guidArt);
               }
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      return reload;
   }

   private void handleCachesForAddedModified(EventBasicGuidArtifact guidArt) {
      // Only process if in cache
      Artifact artifact = ArtifactCache.getActive(guidArt);
      if (artifact instanceof AbstractWorkflowArtifact) {
         AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) artifact;
         awa.clearCaches();
      }
   }

}