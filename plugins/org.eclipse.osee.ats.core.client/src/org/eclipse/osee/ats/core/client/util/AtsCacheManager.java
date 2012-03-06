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
package org.eclipse.osee.ats.core.client.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.osee.ats.core.client.config.AtsBulkLoad;
import org.eclipse.osee.ats.core.client.config.TeamDefinitionArtifact;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.ats.core.client.task.AbstractTaskableArtifact;
import org.eclipse.osee.ats.core.client.task.TaskArtifact;
import org.eclipse.osee.ats.core.client.type.AtsArtifactTypes;
import org.eclipse.osee.ats.core.client.type.AtsRelationTypes;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * Common cache storage for ATS configuration artifacts:<br>
 * TeamDefinitionArtifact<br>
 * VersionArtifact<br>
 * ActionableItemArtifact<br>
 * All other artifact types will silently not cached<br>
 *
 * @author Donald G. Dunne
 */
public class AtsCacheManager {

   private static Map<AbstractTaskableArtifact, Collection<TaskArtifact>> teamTasksCache =
      new ConcurrentHashMap<AbstractTaskableArtifact, Collection<TaskArtifact>>();

   private AtsCacheManager() {
      // Utility class
   }

   public static void decache(Artifact sma) {
      if (sma != null) {
         teamTasksCache.remove(sma);
      }
   }

   public static void decache(AbstractTaskableArtifact sma) {
      if (sma != null) {
         teamTasksCache.remove(sma);
      }
   }

   public static Collection<TaskArtifact> getTaskArtifacts(AbstractTaskableArtifact sma) throws OseeCoreException {
      Collection<TaskArtifact> tasks = teamTasksCache.get(sma);
      if (tasks == null || containsDeleted(tasks)) {
         // Get and cache tasks
         tasks = sma.getRelatedArtifacts(AtsRelationTypes.SmaToTask_Task, TaskArtifact.class);
         if (tasks.isEmpty()) {
            return tasks;
         }
         teamTasksCache.put(sma, tasks);
      }
      return tasks;
   }

   private static boolean containsDeleted(Collection<TaskArtifact> tasks) {
      boolean result = false;
      for (TaskArtifact task : tasks) {
         if (task.isDeleted()) {
            result = true;
            break;
         }
      }
      return result;
   }

   public static List<Artifact> getArtifactsByName(IArtifactType artifactType, String name) {
      AtsBulkLoad.loadConfig(true);
      return ArtifactCache.getArtifactsByName(artifactType, name);
   }

   public static TeamDefinitionArtifact getTeamDefinitionArtifact(String guid) throws OseeCoreException {
      AtsBulkLoad.loadConfig(true);
      //AtsBulkLoad should load the artifact into the cache and ArtifactQuery should return the cached artifact
      return (TeamDefinitionArtifact) ArtifactQuery.getArtifactFromId(guid, AtsUtilCore.getAtsBranch());
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

}
