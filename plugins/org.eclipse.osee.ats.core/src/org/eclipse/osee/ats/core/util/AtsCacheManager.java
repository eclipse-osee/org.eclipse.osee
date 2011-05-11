/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.ats.core.config.ActionableItemArtifact;
import org.eclipse.osee.ats.core.config.AtsBulkLoad;
import org.eclipse.osee.ats.core.config.TeamDefinitionArtifact;
import org.eclipse.osee.ats.core.internal.Activator;
import org.eclipse.osee.ats.core.task.AbstractTaskableArtifact;
import org.eclipse.osee.ats.core.task.TaskArtifact;
import org.eclipse.osee.ats.core.type.AtsArtifactTypes;
import org.eclipse.osee.ats.core.type.AtsRelationTypes;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidRelation;
import org.eclipse.osee.framework.skynet.core.event.model.EventModType;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.skynet.core.utility.DbUtil;

/**
 * Common cache storage for ATS configuration artifacts:<br>
 * TeamDefinitionArtifact<br>
 * VersionArtifact<br>
 * ActionableItemArtifact<br>
 * All other artifact types will silently not cached<br>
 * 
 * @author Donald G. Dunne
 */
public class AtsCacheManager implements IArtifactEventListener {

   private static Map<AbstractTaskableArtifact, Collection<TaskArtifact>> teamTasksCache =
      new HashMap<AbstractTaskableArtifact, Collection<TaskArtifact>>();

   public static void start() {
      new AtsCacheManager();
   }

   private AtsCacheManager() {
      OseeEventManager.addPriorityListener(this);
   }

   public static synchronized void decacheTaskArtifacts(AbstractTaskableArtifact sma) {
      teamTasksCache.remove(sma);
   }

   public static synchronized Collection<TaskArtifact> getTaskArtifacts(AbstractTaskableArtifact sma) throws OseeCoreException {
      if (!teamTasksCache.containsKey(sma)) {
         Collection<TaskArtifact> taskArtifacts =
            sma.getRelatedArtifacts(AtsRelationTypes.SmaToTask_Task, TaskArtifact.class);
         if (taskArtifacts.isEmpty()) {
            return taskArtifacts;
         }
         teamTasksCache.put(sma, taskArtifacts);
      }
      return teamTasksCache.get(sma);
   }

   public static List<Artifact> getArtifactsByName(IArtifactType artifactType, String name) {
      AtsBulkLoad.loadConfig(true);
      return ArtifactCache.getArtifactsByName(artifactType, name);
   }

   public static ActionableItemArtifact getActionableItemByGuid(String guid) throws OseeCoreException {
      AtsBulkLoad.loadConfig(true);
      return (ActionableItemArtifact) ArtifactCache.getActive(guid, AtsUtilCore.getAtsBranch().getId());
   }

   public static TeamDefinitionArtifact getTeamDefinitionArtifact(String guid) throws OseeCoreException {
      AtsBulkLoad.loadConfig(true);
      return (TeamDefinitionArtifact) ArtifactCache.getActive(guid, AtsUtilCore.getAtsBranch().getId());
   }

   public static List<Artifact> getArtifactsByActive(IArtifactType artifactType, Active active) throws OseeCoreException {
      AtsBulkLoad.loadConfig(true);
      return AtsUtilCore.getActive(ArtifactCache.getArtifactsByType(artifactType), active, null);
   }

   public static Artifact getSoleArtifactByName(IArtifactType artifactType, String name) {
      AtsBulkLoad.loadConfig(true);
      List<Artifact> arts = ArtifactCache.getArtifactsByName(artifactType, name);
      if (arts.size() == 1) {
         return arts.iterator().next();
      }
      return null;
   }

   @Override
   public void handleArtifactEvent(ArtifactEvent artifactEvent, Sender sender) {
      if (DbUtil.isDbInit()) {
         OseeEventManager.removeListener(this);
         return;
      }
      processArtifacts(artifactEvent);
      processRelations(artifactEvent);
   }

   private void processRelations(ArtifactEvent artifactEvent) {
      for (EventBasicGuidRelation guidRel : artifactEvent.getRelations()) {
         try {
            if (guidRel.is(AtsRelationTypes.SmaToTask_Task)) {
               for (TaskArtifact taskArt : ArtifactCache.getActive(guidRel, TaskArtifact.class)) {
                  teamTasksCache.remove(taskArt.getParent());
               }
               for (Artifact artifact : ArtifactCache.getActive(guidRel)) {
                  if (artifact instanceof AbstractTaskableArtifact) {
                     teamTasksCache.remove(artifact);
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
         try {
            if (guidArt.is(EventModType.Deleted, EventModType.Purged)) {
               if (guidArt.is(AtsArtifactTypes.Task) && guidArt.is(EventModType.Deleted)) {
                  Artifact artifact = ArtifactCache.getActive(guidArt);
                  if (artifact != null) {
                     teamTasksCache.remove(artifact.getParent());
                  }
               }
               Artifact artifact = ArtifactCache.getActive(guidArt);
               if (artifact instanceof AbstractTaskableArtifact) {
                  teamTasksCache.remove(artifact);
               }
            }
            if (guidArt.is(EventModType.Added, EventModType.Modified)) {
               // Only process if in cache
               Artifact artifact = ArtifactCache.getActive(guidArt);
               if (artifact != null && guidArt.is(EventModType.Added)) {
                  if (artifact.isOfType(AtsArtifactTypes.Task)) {
                     teamTasksCache.remove(artifact.getParent());
                  }
                  if (artifact instanceof AbstractTaskableArtifact) {
                     teamTasksCache.remove(artifact);
                  }
               }
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
   }

   @Override
   public List<? extends IEventFilter> getEventFilters() {
      return Arrays.asList(OseeEventManager.getCommonBranchFilter());
   }
}
