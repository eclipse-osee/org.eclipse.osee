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
package org.eclipse.osee.framework.skynet.core;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchCreator;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.factory.ArtifactFactoryCache;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.remoteEvent.RemoteEventManager;
import org.eclipse.osee.framework.skynet.core.revision.RevisionManager;
import org.eclipse.osee.framework.skynet.core.tagging.TagManager;
import org.eclipse.osee.framework.skynet.core.usage.LoginEntry;
import org.eclipse.osee.framework.skynet.core.usage.UsageLog;

/**
 * @author Ryan D. Brooks
 */
public final class PersistenceManagerInit {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(PersistenceManagerInit.class);
   private static boolean isFirstRun = true;
   private static boolean inInit = false;
   private static Set<PersistenceManager> initializedManagers = new HashSet<PersistenceManager>();

   public static synchronized void initManagerWeb(PersistenceManager managerCandidate) {
      if (managerCandidate == null) {
         Exception ex = new IllegalStateException("Initialization cycle detected.  Manager instance is null.");
         // Make sure that this gets logged out 
         logger.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
      }

      if (inInit) {
         /* This condition stops recursion by the thread performing manager initialization
          * since other threads are pended on this synchronized method.
          */
         return;
      }

      if (isFirstRun) {
         LoginEntry logEntry = new LoginEntry(); // do very early to get a time closer to the actual login time
         inInit = true;
         isFirstRun = false;
         for (PersistenceManager manager : new PersistenceManager[] {ArtifactFactoryCache.getInstance(),
               SkynetAuthentication.getInstance(), AccessControlManager.getInstance(),
               ConfigurationPersistenceManager.getInstance(), RelationPersistenceManager.getInstance(),
               ArtifactPersistenceManager.getInstance(), RemoteEventManager.getInstance(),
               BranchPersistenceManager.getInstance(), TagManager.getInstance(), BranchCreator.getInstance(),
               RevisionManager.getInstance()}) {
            initializedManagers.add(manager);
         }

         for (PersistenceManager manager : initializedManagers) {
            try {
               manager.onManagerWebInit();
            } catch (Exception ex) {
               throw new IllegalStateException("Exception while initializing persistence managers.", ex);
            }
         }
         inInit = false;

         // this is performed last since the UsageLog depeneds on at least one manager in the web
         UsageLog.getInstance().addEntry(logEntry);
      }

      if (!initializedManagers.contains(managerCandidate)) {
         throw new IllegalStateException("The manager " + managerCandidate + " is not in this web of managers");
      }
   }
}
