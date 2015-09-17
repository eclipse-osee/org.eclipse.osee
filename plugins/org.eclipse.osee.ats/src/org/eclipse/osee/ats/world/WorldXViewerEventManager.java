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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.core.client.util.AtsUtilClient;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.model.event.DefaultBasicUuidRelationReorder;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
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
            if (artifactEvent.isForBranch(AtsUtilCore.getAtsBranch())) {
               Runnable runnable = createDisplayRunnable(artifactEvent, handlers);
               Displays.ensureInDisplayThread(runnable);
            }
         } catch (OseeCoreException ex) {
            // Do Nothing;
         }
      }

      @Override
      public List<? extends IEventFilter> getEventFilters() {
         return AtsUtilClient.getAtsObjectEventFilters();
      }

      private Runnable createDisplayRunnable(ArtifactEvent artifactEvent, Collection<IWorldViewerEventHandler> handlers) {
         Collection<Artifact> modifiedArts =
            artifactEvent.getCacheArtifacts(EventModType.Modified, EventModType.Reloaded);
         Collection<Artifact> relModifiedArts = artifactEvent.getRelCacheArtifacts();
         Collection<EventBasicGuidArtifact> deletedPurgedArts =
            artifactEvent.get(EventModType.Deleted, EventModType.Purged);
         Collection<Artifact> goalMemberReordered = new HashSet<Artifact>();
         for (DefaultBasicUuidRelationReorder reorder : artifactEvent.getRelationOrderRecords()) {
            if (reorder.is(AtsRelationTypes.Goal_Member)) {
               Artifact cachedArt = ArtifactCache.getActive(reorder.getParentArt());
               if (cachedArt != null && cachedArt.isOfType(AtsArtifactTypes.Goal)) {
                  goalMemberReordered.add(cachedArt);
               }
            }
         }
         Collection<Artifact> sprintMemberReordered = new HashSet<Artifact>();
         for (DefaultBasicUuidRelationReorder reorder : artifactEvent.getRelationOrderRecords()) {
            if (reorder.is(AtsRelationTypes.AgileSprintToItem_AtsItem)) {
               Artifact cachedArt = ArtifactCache.getActive(reorder.getParentArt());
               if (cachedArt != null && cachedArt.isOfType(AtsArtifactTypes.Goal)) {
                  sprintMemberReordered.add(cachedArt);
               }
            }
         }
         return new DisplayRunnable(modifiedArts, relModifiedArts, deletedPurgedArts, goalMemberReordered,
            sprintMemberReordered, handlers);
      }
   }

   private static final class DisplayRunnable implements Runnable {
      private final Collection<Artifact> modifiedArts;
      private final Collection<Artifact> relModifiedArts;
      private final Collection<EventBasicGuidArtifact> deletedPurgedArts;
      private final Collection<IWorldViewerEventHandler> handlers;
      private final Collection<Artifact> goalMemberReordered;
      private final Collection<Artifact> sprintMemberReordered;

      public DisplayRunnable(Collection<Artifact> modifiedArts, Collection<Artifact> relModifiedArts, Collection<EventBasicGuidArtifact> deletedPurgedArts, Collection<Artifact> goalMemberReordered, Collection<Artifact> sprintMemberReordered, Collection<IWorldViewerEventHandler> handlers) {
         super();
         this.modifiedArts = modifiedArts;
         this.relModifiedArts = relModifiedArts;
         this.deletedPurgedArts = deletedPurgedArts;
         this.goalMemberReordered = goalMemberReordered;
         this.sprintMemberReordered = sprintMemberReordered;
         this.handlers = handlers;
      }

      private void processArtifact(WorldXViewer worldViewer, Artifact artifact, Set<Long> processed) {
         try {
            // Don't refresh deleted artifacts
            if (!artifact.isDeleted() && AtsUtil.isAtsArtifact(artifact)) {
               worldViewer.refresh(artifact);
               // If parent is loaded and child changed, refresh parent
               if (artifact instanceof AbstractWorkflowArtifact) {
                  AbstractWorkflowArtifact smaArt = (AbstractWorkflowArtifact) artifact;
                  Artifact smaParent = smaArt.getParentAtsArtifact();
                  /**
                   * Only process parent artifacts once to reduce the amount of refresh. This is especially important
                   * for tasks and reviews where the parents of 2 or more can be the same.
                   */
                  if (smaParent != null && !processed.contains(smaParent.getUuid())) {
                     worldViewer.refresh(smaParent);
                     processed.add(smaParent.getUuid());
                  }
               }
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
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
                  handler.getWorldXViewer().remove(deletedPurgedArts.toArray(new Object[deletedPurgedArts.size()]));
               }
            } catch (Exception ex) {
               OseeLog.logf(Activator.class, Level.SEVERE, ex, "Error processing event handler for deleted - %s",
                  handler);
            }
         }
      }

      @Override
      public void run() {
         Set<Long> processed = new HashSet<>();
         for (IWorldViewerEventHandler handler : handlers) {
            try {
               if (!handler.isDisposed()) {
                  WorldXViewer worldViewer = handler.getWorldXViewer();
                  if (worldViewer != null && !worldViewer.isDisposed()) {
                     processPurged(worldViewer, handler);
                     for (Artifact artifact : modifiedArts) {
                        processArtifact(worldViewer, artifact, processed);
                     }
                     for (Artifact artifact : relModifiedArts) {
                        processArtifact(worldViewer, artifact, processed);
                     }
                     handler.relationsModifed(relModifiedArts, goalMemberReordered, sprintMemberReordered);
                  }
               }
            } catch (Exception ex) {
               OseeLog.logf(Activator.class, Level.SEVERE, ex, "Error processing event handler for - %s", handler);
            }
         }
      }
   }
}
