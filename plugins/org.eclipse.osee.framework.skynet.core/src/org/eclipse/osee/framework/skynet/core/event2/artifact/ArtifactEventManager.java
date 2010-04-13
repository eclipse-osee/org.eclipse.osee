/*
 * Created on Mar 24, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.event2.artifact;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.osee.framework.core.data.DefaultBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.IEventListener;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.event.msgs.BasicModifiedGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.msgs.TransactionEvent;
import org.eclipse.osee.framework.skynet.core.event2.filter.FilteredEventListener;
import org.eclipse.osee.framework.skynet.core.event2.filter.IEventFilter;

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

   public static void processArtifactChanges(Sender sender, TransactionEvent transEvent) {
      List<EventBasicGuidArtifact> artifactChanges = new ArrayList<EventBasicGuidArtifact>();
      for (DefaultBasicGuidArtifact guidArt : transEvent.getAdded()) {
         artifactChanges.add(new EventBasicGuidArtifact(EventModType.Added, guidArt));
      }
      for (DefaultBasicGuidArtifact guidArt : transEvent.getDeleted()) {
         artifactChanges.add(new EventBasicGuidArtifact(EventModType.Deleted, guidArt));
      }
      for (BasicModifiedGuidArtifact guidArt : transEvent.getModified()) {
         artifactChanges.add(new EventModifiedBasicGuidArtifact(guidArt.getBranchGuid(), guidArt.getArtTypeGuid(),
               guidArt.getArtGuid(), guidArt.getAttributes()));
      }
      processArtifactChanges(sender, artifactChanges);
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
