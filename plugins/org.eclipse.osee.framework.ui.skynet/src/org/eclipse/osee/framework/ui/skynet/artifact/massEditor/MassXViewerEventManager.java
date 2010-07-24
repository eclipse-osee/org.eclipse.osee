/*
 * Created on Jun 30, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.artifact.massEditor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.event2.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event2.artifact.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event2.artifact.EventModType;
import org.eclipse.osee.framework.skynet.core.event2.artifact.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event2.filter.IEventFilter;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * Common location for event handling for mass editor in order to keep number of registrations and processing to a
 * minimum.
 * 
 * @author Donald G. Dunne
 */
public class MassXViewerEventManager implements IArtifactEventListener {

   List<IMassViewerEventHandler> handlers = new ArrayList<IMassViewerEventHandler>();
   static MassXViewerEventManager instance;

   public static void add(IMassViewerEventHandler iWorldEventHandler) {
      if (instance == null) {
         instance = new MassXViewerEventManager();
         OseeEventManager.addListener(instance);
      }
      instance.handlers.add(iWorldEventHandler);
   }

   public static void remove(IMassViewerEventHandler iWorldEventHandler) {
      if (instance != null) {
         instance.handlers.remove(iWorldEventHandler);
      }
   }

   @Override
   public void handleArtifactEvent(final ArtifactEvent artifactEvent, Sender sender) {
      for (IMassViewerEventHandler handler : new CopyOnWriteArrayList<IMassViewerEventHandler>(handlers)) {
         if (handler.isDisposed()) {
            handlers.remove(handler);
         }
      }

      final Collection<Artifact> modifiedArts =
         artifactEvent.getCacheArtifacts(EventModType.Modified, EventModType.Reloaded);
      final Collection<Artifact> relModifiedArts = artifactEvent.getRelCacheArtifacts();
      final Collection<EventBasicGuidArtifact> deletedPurgedArts =
         artifactEvent.get(EventModType.Deleted, EventModType.Purged);

      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            if (!deletedPurgedArts.isEmpty()) {
               for (IMassViewerEventHandler handler : handlers) {
                  if (!handler.isDisposed()) {
                     IContentProvider contentProvider = handler.getMassXViewer().getContentProvider();
                     // remove from UI
                     if (contentProvider instanceof MassContentProvider) {
                        ((MassContentProvider) contentProvider).removeAll(deletedPurgedArts);
                     }
                  }
               }
            }
            for (IMassViewerEventHandler handler : handlers) {
               if (!handler.isDisposed()) {
                  IContentProvider contentProvider = handler.getMassXViewer().getContentProvider();
                  // remove from UI
                  if (contentProvider instanceof MassContentProvider) {
                     ((MassContentProvider) contentProvider).updateAll(modifiedArts);
                  }
               }

               for (Artifact art : relModifiedArts) {
                  // Don't refresh deleted artifacts
                  if (art.isDeleted()) {
                     continue;
                  }
                  handler.getMassXViewer().refresh(art);
               }
            }
         }
      });
   }

   @Override
   public List<? extends IEventFilter> getEventFilters() {
      return null;
   }

}
