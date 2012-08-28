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
   private static String NAME = "Loading ATS Configuration";

   public AtsLoadConfigArtifactsOperation() {
      this(false);
   }

   public AtsLoadConfigArtifactsOperation(boolean reload) {
      super(NAME, Activator.PLUGIN_ID);
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
         //         ElapsedTime time = new ElapsedTime(NAME);
         loaded = true;
         OseeLog.log(Activator.class, Level.INFO, NAME);
         AtsUsersClient.start();
         loadAtsConfig();
         loaded = true;
         //         time.end();
      }
   }

   private void loadAtsConfig() throws OseeCoreException {
      //      ElapsedTime time = new ElapsedTime("Loading ATS Teams, AIs and Versions");
      AtsConfigCache newInstance = new AtsConfigCache();
      AtsConfigCacheLoaderClient loader = new AtsConfigCacheLoaderClient(newInstance);
      List<Artifact> artifactListFromType =
         ArtifactQuery.getArtifactListFromType(
            Arrays.asList(AtsArtifactTypes.TeamDefinition, AtsArtifactTypes.ActionableItem, AtsArtifactTypes.Version),
            AtsUtilCore.getAtsBranchToken(), DeletionFlag.EXCLUDE_DELETED);

      for (Artifact artifact : artifactListFromType) {
         loader.cacheConfigArtifact(artifact);
      }
      AtsConfigCache.setCurrent(newInstance);
      //      time.end();
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      ensureLoaded();
   }

   public static boolean isLoaded() {
      return loaded;
   }

}
