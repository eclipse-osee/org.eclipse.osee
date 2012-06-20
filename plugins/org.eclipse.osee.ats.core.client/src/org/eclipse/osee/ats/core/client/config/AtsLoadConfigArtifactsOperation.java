/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.config;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.ats.core.client.internal.AtsCacheManagerUpdateListener;
import org.eclipse.osee.ats.core.client.util.AtsUsersClient;
import org.eclipse.osee.ats.core.client.util.AtsUtilCore;
import org.eclipse.osee.ats.core.config.AtsConfigCache;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Donald G. Dunne
 */
public class AtsLoadConfigArtifactsOperation extends AbstractOperation {
   private static boolean loaded = false;

   public AtsLoadConfigArtifactsOperation() {
      this(false);
   }

   public AtsLoadConfigArtifactsOperation(boolean reload) {
      super("ATS Loading Configuration", Activator.PLUGIN_ID);
      if (reload) {
         loaded = false;
      }
   }

   public void forceReload() throws OseeCoreException {
      loaded = false;
      ensureLoaded();
   }

   public synchronized void ensureLoaded() throws OseeCoreException {
      if (!loaded) {
         //         ElapsedTime loadConfigTime = new ElapsedTime(getName());
         loaded = true;
         OseeLog.log(Activator.class, Level.INFO, "Loading ATS Configuration");
         //         ElapsedTime time = new ElapsedTime("  - QueryListFromType");
         AtsUsersClient.start();
         AtsConfigCache.clearCaches();
         List<Artifact> artifactListFromType =
            ArtifactQuery.getArtifactListFromType(Arrays.asList(AtsArtifactTypes.TeamDefinition,
               AtsArtifactTypes.ActionableItem, AtsArtifactTypes.Version, AtsArtifactTypes.WorkDefinition),
               AtsUtilCore.getAtsBranchToken(), DeletionFlag.EXCLUDE_DELETED);
         //         time.end();
         //         time = new ElapsedTime("  - CacheStaticIds");

         for (Artifact artifact : artifactListFromType) {
            AtsConfigManagerClient.cacheConfigArtifact(artifact);
         }

         AtsCacheManagerUpdateListener.start();

         //         for (IAtsActionableItem aia : AtsConfigCache.get(IAtsActionableItem.class)) {
         //            System.out.println("AI: " + aia + " - " + aia.getGuid());
         //            for (IAtsActionableItem child : aia.getChildrenActionableItems()) {
         //               System.out.println("--- " + child + " - " + child.getGuid());
         //            }
         //         }
         //
         //         for (IAtsTeamDefinition teamDef : AtsConfigCache.get(IAtsTeamDefinition.class)) {
         //            System.out.println("Teams: " + teamDef + " - " + teamDef.getGuid());
         //            for (IAtsTeamDefinition child : teamDef.getChildrenTeamDefinitions()) {
         //               System.out.println("--- " + child + " - " + child.getGuid());
         //            }
         //         }

         //         time.end();
         loaded = true;
         //         loadConfigTime.end();
      }
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      ensureLoaded();
   }

   public static boolean isLoaded() {
      return loaded;
   }

}
