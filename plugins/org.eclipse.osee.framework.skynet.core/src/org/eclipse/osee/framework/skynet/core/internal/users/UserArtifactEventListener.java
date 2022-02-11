/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.framework.skynet.core.internal.users;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.cache.admin.Cache;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.type.LazyObject;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.filter.ArtifactTypeEventFilter;
import org.eclipse.osee.framework.skynet.core.event.filter.BranchIdEventFilter;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactTopicEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactTopicEvent;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.EventModType;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.topic.event.filter.ArtifactTopicTypeEventFilter;
import org.eclipse.osee.framework.skynet.core.topic.event.filter.BranchIdTopicEventFilter;
import org.eclipse.osee.framework.skynet.core.topic.event.filter.ITopicEventFilter;

/**
 * @author Roberto E. Escobar
 */
public class UserArtifactEventListener implements IArtifactEventListener, IArtifactTopicEventListener {

   private final List<? extends IEventFilter> eventFilters;
   private final List<? extends ITopicEventFilter> topicEventFilters;
   private final LazyObject<Cache<String, User>> cacheProvider;
   private final LazyObject<Iterable<? extends String>> keysProvider;

   public UserArtifactEventListener(LazyObject<Cache<String, User>> cacheProvider, LazyObject<Iterable<? extends String>> keysProvider) {
      super();
      this.eventFilters = Arrays.asList(new ArtifactTypeEventFilter(CoreArtifactTypes.User),
         new BranchIdEventFilter(CoreBranches.COMMON));
      topicEventFilters = Arrays.asList(new ArtifactTopicTypeEventFilter(CoreArtifactTypes.User),
         new BranchIdTopicEventFilter(CoreBranches.COMMON));
      this.cacheProvider = cacheProvider;
      this.keysProvider = keysProvider;
   }

   @Override
   public List<? extends IEventFilter> getEventFilters() {
      return eventFilters;
   }

   @Override
   public List<? extends ITopicEventFilter> getTopicEventFilters() {
      return topicEventFilters;
   }

   @Override
   public void handleArtifactEvent(ArtifactEvent artifactEvent, Sender sender) {
      if (areUsers(artifactEvent, EventModType.Added, EventModType.Purged, EventModType.Deleted)) {
         keysProvider.invalidate();
      }

      Collection<Artifact> cacheArtifacts = artifactEvent.getCacheArtifacts(EventModType.Purged, EventModType.Deleted);
      if (!cacheArtifacts.isEmpty()) {
         Set<String> keys = getKeysToInvalidate(cacheArtifacts);
         if (!keys.isEmpty()) {
            try {
               cacheProvider.get().invalidate(keys);
            } catch (OseeCoreException ex) {
               OseeLog.logf(Activator.class, Level.SEVERE, ex, "Error updating users");
            }
         }
      }
   }

   @Override
   public void handleArtifactTopicEvent(ArtifactTopicEvent artifactTopicEvent, Sender sender) {
      if (areUsers(artifactTopicEvent, EventModType.Added, EventModType.Purged, EventModType.Deleted)) {
         keysProvider.invalidate();
      }

      Collection<Artifact> cacheArtifacts =
         artifactTopicEvent.getCacheArtifacts(EventModType.Purged, EventModType.Deleted);
      if (!cacheArtifacts.isEmpty()) {
         Set<String> keys = getKeysToInvalidate(cacheArtifacts);
         if (!keys.isEmpty()) {
            try {
               cacheProvider.get().invalidate(keys);
            } catch (OseeCoreException ex) {
               OseeLog.logf(Activator.class, Level.SEVERE, ex, "Error updating users");
            }
         }
      }
   }

   private boolean areUsers(ArtifactEvent artifactEvent, EventModType... eventType) {
      for (EventBasicGuidArtifact artifact : artifactEvent.get(eventType)) {
         if (artifact.isTypeEqual(CoreArtifactTypes.User)) {
            return true;
         }
      }
      return false;
   }

   private boolean areUsers(ArtifactTopicEvent artifactTopicEvent, EventModType... eventType) {
      for (EventBasicGuidArtifact artifact : artifactTopicEvent.get(eventType)) {
         if (artifact.isTypeEqual(CoreArtifactTypes.User)) {
            return true;
         }
      }
      return false;
   }

   private Set<String> getKeysToInvalidate(Collection<Artifact> cacheArtifacts) {
      Set<String> keys = new HashSet<>();
      for (Artifact artifact : cacheArtifacts) {
         if (artifact instanceof User) {
            User user = (User) artifact;
            try {
               keys.add(user.getUserId());
            } catch (OseeCoreException ex) {
               OseeLog.logf(Activator.class, Level.SEVERE, ex, "Error updating user [%s]", user);
            }
         }
      }
      return keys;
   }

}
