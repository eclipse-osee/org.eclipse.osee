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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.core.client.artifact.CollectorArtifact;
import org.eclipse.osee.ats.core.client.util.IArtifactMembersCache;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.jdk.core.type.DoubleKeyHashMap;
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
public class ArtifactCollectorsCache<T extends CollectorArtifact> implements IArtifactMembersCache<T> {

   private static Map<Integer, List<Artifact>> cache;
   private static DoubleKeyHashMap<Integer, Integer, String> goalMemberOrderMap;
   private static Set<Integer> registered;
   private static volatile boolean initialized = false;

   private void initializeStructures() {
      if (!initialized) {
         initialized = true;
         cache = new HashMap<Integer, List<Artifact>>();
         registered = new HashSet<Integer>();
         goalMemberOrderMap = new DoubleKeyHashMap<Integer, Integer, String>();
      }
   }

   private void registerForEvents(final T collectorArt) {
      if (!registered.contains(collectorArt.getArtId())) {
         IArtifactEventListener eventListener = new IArtifactEventListener() {

            @Override
            public List<? extends IEventFilter> getEventFilters() {
               return Arrays.asList(new ArtifactEventFilter(collectorArt));
            }

            @Override
            public void handleArtifactEvent(ArtifactEvent artifactEvent, Sender sender) {
               synchronized (cache) {
                  cache.remove(collectorArt.getArtId());
               }
            }
         };
         OseeEventManager.addListener(eventListener);
         synchronized (registered) {
            registered.add(collectorArt.getArtId());
         }
      }
   }

   @Override
   public List<Artifact> getMembers(T collector) throws OseeCoreException {
      initializeStructures();
      registerForEvents(collector);
      List<Artifact> members = cache.get(collector.getArtId());
      if (members == null) {
         members = collector.getRelatedArtifacts(AtsRelationTypes.Goal_Member, DeletionFlag.EXCLUDE_DELETED);
         synchronized (cache) {
            cache.put(collector.getArtId(), members);
            fillOrderCache(collector, members);
         }
      }
      LinkedList<Artifact> linkedList = new LinkedList<Artifact>(members);
      return linkedList;
   }

   @Override
   public void decache(T collectorArt) {
      if (initialized) {
         synchronized (cache) {
            cache.remove(collectorArt.getArtId());
         }
         synchronized (goalMemberOrderMap) {
            clearOrderCache(collectorArt);
         }
      }
   }

   @Override
   public void invalidate() {
      if (initialized) {
         synchronized (cache) {
            cache.clear();
         }
         synchronized (goalMemberOrderMap) {
            goalMemberOrderMap.clear();
         }
      }
   }

   @Override
   public String getMemberOrder(T collectorArt, Artifact member) throws OseeCoreException {
      initializeStructures();
      if (goalMemberOrderMap.getSubHash(collectorArt.getArtId()) == null) {
         fillOrderCache(collectorArt, getMembers(collectorArt));
      }
      String order = goalMemberOrderMap.get(collectorArt.getArtId(), member.getArtId());
      return order == null ? "" : order;
   }

   private void fillOrderCache(T collectorArt, List<Artifact> members) {
      initializeStructures();
      synchronized (goalMemberOrderMap) {
         clearOrderCache(collectorArt);
         int x = 1;
         for (Artifact artifact : members) {
            goalMemberOrderMap.put(collectorArt.getArtId(), artifact.getArtId(), String.valueOf(x++));
         }
      }
   }

   private void clearOrderCache(T collectorArt) {
      if (initialized) {
         List<Integer> memberIds = new ArrayList<Integer>();
         Map<Integer, String> subHash = goalMemberOrderMap.getSubHash(collectorArt.getArtId());
         if (subHash != null) {
            memberIds.addAll(subHash.keySet());
            for (Integer memberId : memberIds) {
               goalMemberOrderMap.remove(collectorArt.getArtId(), memberId);
            }
         }
      }
   }
}
