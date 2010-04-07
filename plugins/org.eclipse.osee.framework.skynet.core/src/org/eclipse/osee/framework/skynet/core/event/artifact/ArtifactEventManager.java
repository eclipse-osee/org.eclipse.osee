/*
 * Created on Mar 24, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.event.artifact;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.osee.framework.skynet.core.event.IEventListener;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.event.filter.FilteredEventListener;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;

/**
 * @author Donald G. Dunne
 */
public class ArtifactEventManager {
   private static final List<IEventListener> listeners = new CopyOnWriteArrayList<IEventListener>();

   public static void addListener(IEventListener listener) {
      if (listener == null) {
         throw new IllegalArgumentException("listener can not be null");
      }
      if (!listeners.contains(listener)) {
         listeners.add(listener);
      }
   }

   public static void removeListener(IEventListener listener) {
      listeners.remove(listener);
   }

   public static void removeAllListeners() {
      listeners.clear();
   }

   public static int getNumberOfListeners() {
      return listeners.size();
   }

   public static boolean isHandledBy(IEventListener event) {
      return event instanceof IArtifactListener || (event instanceof FilteredEventListener && ((FilteredEventListener) event).isOfType(IArtifactListener.class));
   }

   public static void processArtifactChanges(Sender sender, Collection<EventBasicGuidArtifact> artifactChanges) {
      for (IEventListener listener : listeners) {
         IArtifactListener artifactListener = null;
         Collection<IEventFilter> eventFilters = null;
         if (listener instanceof IArtifactListener) {
            artifactListener = (IArtifactListener) listener;
            eventFilters = Collections.emptyList();
         } else if (listener instanceof FilteredEventListener) {
            artifactListener = (IArtifactListener) ((FilteredEventListener) listener).getEventListener();
            eventFilters = ((FilteredEventListener) listener).getEventFilters();
         }
         if (artifactListener != null) {
            // TODO handle filters first
            artifactListener.handleArtifactModified(artifactChanges, sender);

            // TODO handle artifact change type??
         }
      }
   }
}
