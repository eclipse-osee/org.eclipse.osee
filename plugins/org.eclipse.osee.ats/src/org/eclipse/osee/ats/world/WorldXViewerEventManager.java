/*
 * Created on Jun 30, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
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
 * Common location for event handling for task and world composites in order to keep number of registrations and
 * processing to a minimum.
 * 
 * @author Donald G. Dunne
 */
public class WorldXViewerEventManager implements IArtifactEventListener {

   List<IWorldViewerEventHandler> handlers = new ArrayList<IWorldViewerEventHandler>();
   static WorldXViewerEventManager instance;

   public static void add(IWorldViewerEventHandler iWorldEventHandler) {
      if (instance == null) {
         instance = new WorldXViewerEventManager();
         OseeEventManager.addListener(instance);
      }
      instance.handlers.add(iWorldEventHandler);
   }

   public static void remove(IWorldViewerEventHandler iWorldEventHandler) {
      if (instance != null) {
         instance.handlers.remove(iWorldEventHandler);
      }
   }

   @Override
   public void handleArtifactEvent(final ArtifactEvent artifactEvent, Sender sender) {
      for (IWorldViewerEventHandler handler : new CopyOnWriteArrayList<IWorldViewerEventHandler>(handlers)) {
         if (handler.isDisposed()) {
            handlers.remove(handler);
         }
      }
      try {
         if (artifactEvent.getBranchGuid() == null || !artifactEvent.getBranchGuid().equals(
            AtsUtil.getAtsBranch().getGuid())) {
            return;
         }
      } catch (OseeCoreException ex) {
         return;
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
               for (IWorldViewerEventHandler handler : handlers) {
                  if (!handler.isDisposed()) {
                     // allow handler to remove from model
                     handler.removeItems(deletedPurgedArts);
                     IContentProvider contentProvider = handler.getWorldXViewer().getContentProvider();
                     // remove from UI
                     if (contentProvider instanceof WorldContentProvider) {
                        handler.getWorldXViewer().remove(
                           deletedPurgedArts.toArray(new Object[deletedPurgedArts.size()]));
                     }
                  }
               }
            }
            for (IWorldViewerEventHandler handler : handlers) {
               if (!handler.isDisposed()) {
                  for (Artifact artifact : modifiedArts) {
                     try {
                        // Don't refresh deleted artifacts
                        if (artifact.isDeleted()) {
                           continue;
                        }
                        if (artifact instanceof IWorldViewArtifact) {
                           handler.getWorldXViewer().refresh(artifact);
                           // If parent is loaded and child changed, refresh parent
                           if (artifact instanceof StateMachineArtifact && ((StateMachineArtifact) artifact).getParentAtsArtifact() instanceof IWorldViewArtifact) {
                              handler.getWorldXViewer().refresh(
                                 ((StateMachineArtifact) artifact).getParentAtsArtifact());
                           }
                        }
                     } catch (OseeCoreException ex) {
                        OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
                     }
                  }

                  for (Artifact art : relModifiedArts) {
                     // Don't refresh deleted artifacts
                     if (art.isDeleted()) {
                        continue;
                     }
                     if (art instanceof IWorldViewArtifact) {
                        handler.getWorldXViewer().refresh(art);
                        // If parent is loaded and child changed, refresh parent
                        try {
                           if (art instanceof StateMachineArtifact && ((StateMachineArtifact) art).getParentAtsArtifact() instanceof IWorldViewArtifact) {
                              handler.getWorldXViewer().refresh(((StateMachineArtifact) art).getParentAtsArtifact());
                           }
                        } catch (OseeCoreException ex) {
                           OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
                        }
                     }
                  }
               }
            }
         }
      });
   }

   @Override
   public List<? extends IEventFilter> getEventFilters() {
      return AtsUtil.getAtsObjectEventFilters();
   }

}
