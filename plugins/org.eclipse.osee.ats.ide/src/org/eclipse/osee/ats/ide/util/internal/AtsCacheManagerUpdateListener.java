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
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.util.AtsUtilClient;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidRelation;
import org.eclipse.osee.framework.skynet.core.event.model.EventModType;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.skynet.core.utility.DbUtil;

/**
 * Updates ATS Cache based on Artifact Events. Registers for accessControlService via ats.cache.update.listener osgi registration.
 *
 * @author Donald G. Dunne
 */
public class AtsCacheManagerUpdateListener implements IArtifactEventListener {

   private static List<Long> configReloadRelationTypeGuids = Arrays.asList(
      AtsRelationTypes.ActionableItemLead_Lead.getGuid(), AtsRelationTypes.TeamDefinitionToVersion_Version.getGuid(),
      AtsRelationTypes.TeamActionableItem_TeamDefinition.getGuid(), AtsRelationTypes.TeamLead_Team.getGuid(),
      AtsRelationTypes.ParallelVersion_Child.getGuid(), AtsRelationTypes.ParallelVersion_Parent.getGuid());
   private Boolean singleServerDeployment;

   @Override
   public List<? extends IEventFilter> getEventFilters() {
      return Arrays.asList(AtsUtilClient.getAtsBranchFilter());
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

   private boolean isSingleServerDeployment() {
      if (singleServerDeployment == null) {
         singleServerDeployment = AtsClientService.get().isSingleServerDeployment();
      }
      return singleServerDeployment;
   }

   private void processRelations(ArtifactEvent artifactEvent, boolean handledConfigReload) {
      if (!handledConfigReload) {
         // update cache
         for (EventBasicGuidRelation guidRel : artifactEvent.getRelations()) {
            try {
               RelationType typeByGuid = RelationTypeManager.getTypeByGuid(guidRel.getRelTypeGuid());
               if (configReloadRelationTypeGuids.contains(typeByGuid.getId())) {
                  AtsClientService.get().reloadServerAndClientCaches();
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
            AtsClientService.get().reloadServerAndClientCaches();
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

   private void handleCachesForAddedModified(EventBasicGuidArtifact guidArt) {
      // Only process if in cache
      Artifact artifact = ArtifactCache.getActive(guidArt);
      if (artifact instanceof AbstractWorkflowArtifact) {
         AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) artifact;
         awa.clearCaches();
      }
   }

}