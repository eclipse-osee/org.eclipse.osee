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
package org.eclipse.osee.ats.core.client.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.core.client.artifact.GoalArtifact;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.filter.ArtifactEventFilter;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;

/**
 * @author John Misinco
 */
public class GoalArtifactMembersCache {

   private static Map<String, List<Artifact>> cache;
   private static Set<String> registered;
   private static volatile boolean initialized = false;

   private static void initializeStructures() {
      if (!initialized) {
         initialized = true;
         cache = new HashMap<String, List<Artifact>>();
         registered = new HashSet<String>();
      }
   }

   private static void registerForEvents(final GoalArtifact artifact) {
      if (!registered.contains(artifact.getGuid())) {
         IArtifactEventListener eventListener = new IArtifactEventListener() {

            @Override
            public List<? extends IEventFilter> getEventFilters() {
               return Arrays.asList(new ArtifactEventFilter(artifact));
            }

            @Override
            public void handleArtifactEvent(ArtifactEvent artifactEvent, Sender sender) {
               synchronized (cache) {
                  cache.remove(artifact.getGuid());
               }
            }
         };
         OseeEventManager.addListener(eventListener);
         synchronized (registered) {
            registered.add(artifact.getGuid());
         }
      }
   }

   public static List<Artifact> getMembers(GoalArtifact artifact) throws OseeCoreException {
      initializeStructures();
      registerForEvents(artifact);
      List<Artifact> toReturn = cache.get(artifact.getGuid());
      if (toReturn == null) {
         toReturn = artifact.getRelatedArtifacts(AtsRelationTypes.Goal_Member, DeletionFlag.EXCLUDE_DELETED);
         synchronized (cache) {
            cache.put(artifact.getGuid(), toReturn);
         }
      }
      return new LinkedList<Artifact>(toReturn);
   }

}
