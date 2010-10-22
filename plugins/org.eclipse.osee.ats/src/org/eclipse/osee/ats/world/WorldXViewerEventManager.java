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
package org.eclipse.osee.ats.world;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.EventModType;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * Common location for event handling for task and world composites in order to keep number of registrations and
 * processing to a minimum.
 * 
 * @author Donald G. Dunne
 */
public class WorldXViewerEventManager {

   private static final NotificationHandler notificationHandler = new NotificationHandler();

   private WorldXViewerEventManager() {
      // Static API Class
   }

   public static void add(IWorldViewerEventHandler iWorldEventHandler) {
      notificationHandler.add(iWorldEventHandler);
   }

   public static void remove(IWorldViewerEventHandler iWorldEventHandler) {
      notificationHandler.remove(iWorldEventHandler);
   }

   private static final class NotificationHandler implements IArtifactEventListener {
      private final Collection<IWorldViewerEventHandler> handlers =
         new CopyOnWriteArrayList<IWorldViewerEventHandler>();

      public NotificationHandler() {
         OseeEventManager.addListener(this);
      }

      public void add(IWorldViewerEventHandler iWorldEventHandler) {
         handlers.add(iWorldEventHandler);
      }

      public void remove(IWorldViewerEventHandler iWorldEventHandler) {
         handlers.remove(iWorldEventHandler);
      }

      @Override
      public void handleArtifactEvent(final ArtifactEvent artifactEvent, Sender sender) {
         for (IWorldViewerEventHandler handler : handlers) {
            if (handler.isDisposed()) {
               handlers.remove(handler);
            }
         }
         try {
            if (artifactEvent.isForBranch(AtsUtil.getAtsBranch())) {
               Runnable runnable = createDisplayRunnable(artifactEvent, handlers);
               Displays.ensureInDisplayThread(runnable);
            }
         } catch (OseeCoreException ex) {
            // Do Nothing;
         }
      }

      @Override
      public List<? extends IEventFilter> getEventFilters() {
         return AtsUtil.getAtsObjectEventFilters();
      }

      private Runnable createDisplayRunnable(ArtifactEvent artifactEvent, Collection<IWorldViewerEventHandler> handlers) {
         Collection<Artifact> modifiedArts =
            artifactEvent.getCacheArtifacts(EventModType.Modified, EventModType.Reloaded);
         Collection<Artifact> relModifiedArts = artifactEvent.getRelCacheArtifacts();
         Collection<EventBasicGuidArtifact> deletedPurgedArts =
            artifactEvent.get(EventModType.Deleted, EventModType.Purged);
         return new DisplayRunnable(modifiedArts, relModifiedArts, deletedPurgedArts, handlers);
      }
   }

   private static final class DisplayRunnable implements Runnable {
      private final Collection<Artifact> modifiedArts;
      private final Collection<Artifact> relModifiedArts;
      private final Collection<EventBasicGuidArtifact> deletedPurgedArts;
      private final Collection<IWorldViewerEventHandler> handlers;

      public DisplayRunnable(Collection<Artifact> modifiedArts, Collection<Artifact> relModifiedArts, Collection<EventBasicGuidArtifact> deletedPurgedArts, Collection<IWorldViewerEventHandler> handlers) {
         super();
         this.modifiedArts = modifiedArts;
         this.relModifiedArts = relModifiedArts;
         this.deletedPurgedArts = deletedPurgedArts;
         this.handlers = handlers;
      }

      private void processArtifact(WorldXViewer worldViewer, Artifact artifact) {
         try {
            // Don't refresh deleted artifacts
            if (!artifact.isDeleted() && artifact instanceof IWorldViewArtifact) {
               worldViewer.refresh(artifact);
               // If parent is loaded and child changed, refresh parent
               if (artifact instanceof AbstractWorkflowArtifact) {
                  AbstractWorkflowArtifact smaArt = (AbstractWorkflowArtifact) artifact;
                  Artifact smaParent = smaArt.getParentAtsArtifact();
                  if (smaParent instanceof IWorldViewArtifact) {
                     worldViewer.refresh(smaParent);
                  }
               }
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         }
      }

      private void processPurged(WorldXViewer worldViewer, IWorldViewerEventHandler handler) {
         if (!deletedPurgedArts.isEmpty()) {
            try {
               // allow handler to remove from model
               handler.removeItems(deletedPurgedArts);
               IContentProvider contentProvider = worldViewer.getContentProvider();
               // remove from UI
               if (contentProvider instanceof WorldContentProvider) {
                  for (EventBasicGuidArtifact guidArt : deletedPurgedArts) {
                     worldViewer.remove(guidArt);
                  }
               }
            } catch (Exception ex) {
               OseeLog.log(AtsPlugin.class, Level.SEVERE, ex, "Error processing event handler for deleted - %s",
                  handler);
            }
         }
      }

      @Override
      public void run() {
         for (IWorldViewerEventHandler handler : handlers) {
            try {
               if (!handler.isDisposed()) {
                  WorldXViewer worldViewer = handler.getWorldXViewer();
                  processPurged(worldViewer, handler);
                  for (Artifact artifact : modifiedArts) {
                     processArtifact(worldViewer, artifact);
                  }
                  for (Artifact artifact : relModifiedArts) {
                     processArtifact(worldViewer, artifact);
                  }
                  handler.relationsModifed(relModifiedArts);
               }
            } catch (Exception ex) {
               OseeLog.log(AtsPlugin.class, Level.SEVERE, ex, "Error processing event handler for - %s", handler);
            }
         }
      }
   }
}
