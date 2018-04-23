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
import org.eclipse.nebula.widgets.xviewer.IXViewerPreComputedColumn;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.AtsUtilClient;
import org.eclipse.osee.ats.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
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
      // Utility Class
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
            if (artifactEvent.isOnBranch(AtsClientService.get().getAtsBranch())) {
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

         // create list of items to updatePreComputedColumnValues; includes arts and parent arts
         Set<Artifact> allModAndParents = new HashSet<>((modifiedArts.size() * 2) + relModifiedArts.size());
         for (Artifact art : modifiedArts) {
            allModAndParents.add(art);
            if (art instanceof IAtsWorkItem) {
               IAtsTeamWorkflow teamWf = ((IAtsWorkItem) art).getParentTeamWorkflow();
               if (teamWf != null) {
                  allModAndParents.add((Artifact) teamWf.getStoreObject());
               }
            }
         }
         for (Artifact art : relModifiedArts) {
            allModAndParents.add(art);
            if (art instanceof IAtsWorkItem) {
               IAtsTeamWorkflow teamWf = ((IAtsWorkItem) art).getParentTeamWorkflow();
               if (teamWf != null) {
                  allModAndParents.add((Artifact) teamWf.getStoreObject());
               }
            }
         }
         Collection<Artifact> goalMemberReordered =
            artifactEvent.getRelationOrderArtifacts(AtsRelationTypes.Goal_Member, AtsArtifactTypes.Goal);
         Collection<Artifact> sprintMemberReordered = artifactEvent.getRelationOrderArtifacts(
            AtsRelationTypes.AgileSprintToItem_AtsItem, AtsArtifactTypes.AgileSprint);

         return new DisplayRunnable(modifiedArts, allModAndParents, relModifiedArts, deletedPurgedArts,
            goalMemberReordered, sprintMemberReordered, handlers);
      }
   }

   private static final class DisplayRunnable implements Runnable {
      private final Collection<Artifact> modifiedArts;
      private final Collection<Artifact> relModifiedArts;
      private final Collection<EventBasicGuidArtifact> deletedPurgedArts;
      private final Collection<IWorldViewerEventHandler> handlers;
      private final Collection<Artifact> goalMemberReordered;
      private final Collection<Artifact> sprintMemberReordered;
      private final Collection<Artifact> allModAndParents;

      public DisplayRunnable(Collection<Artifact> modifiedArts, Collection<Artifact> allModAndParents, Collection<Artifact> relModifiedArts, Collection<EventBasicGuidArtifact> deletedPurgedArts, Collection<Artifact> goalMemberReordered, Collection<Artifact> sprintMemberReordered, Collection<IWorldViewerEventHandler> handlers) {
         super();
         this.modifiedArts = modifiedArts;
         this.allModAndParents = allModAndParents;
         this.relModifiedArts = relModifiedArts;
         this.deletedPurgedArts = deletedPurgedArts;
         this.goalMemberReordered = goalMemberReordered;
         this.sprintMemberReordered = sprintMemberReordered;
         this.handlers = handlers;
      }

      private void processArtifact(WorldXViewer worldViewer, Artifact artifact, Set<Long> processed) {
         try {
            // Don't refresh deleted artifacts
            if (!artifact.isDeleted() && AtsObjects.isAtsWorkItemOrAction(artifact)) {
               worldViewer.refresh(artifact);
               // If parent is loaded and child changed, refresh parent
               if (artifact instanceof AbstractWorkflowArtifact) {
                  AbstractWorkflowArtifact smaArt = (AbstractWorkflowArtifact) artifact;
                  Artifact smaParent = smaArt.getParentAtsArtifact();
                  /**
                   * Only process parent artifacts once to reduce the amount of refresh. This is especially important
                   * for tasks and reviews where the parents of 2 or more can be the same.
                   */
                  if (smaParent != null && !processed.contains(smaParent.getId())) {
                     worldViewer.refresh(smaParent);
                     processed.add(smaParent.getId());
                  }
               }
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }

      private void updatePreComputedColumnValue(Collection<Artifact> allModAndParents, WorldXViewer worldViewer) {
         try {
            for (XViewerColumn column : worldViewer.getCustomizeMgr().getCurrentVisibleTableColumns()) {
               if (column instanceof IXViewerPreComputedColumn) {
                  ((IXViewerPreComputedColumn) column).populateCachedValues(allModAndParents,
                     column.getPreComputedValueMap());
               }
            }
         } catch (Exception ex) {
            // do nothing
         }

      }

      private void processPurged(WorldXViewer worldViewer, IWorldViewerEventHandler handler) {
         if (!deletedPurgedArts.isEmpty()) {
            try {
               worldViewer.refresh();
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
                     // update precomputed values in bulk before handling each artifact separately
                     updatePreComputedColumnValue(allModAndParents, worldViewer);

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
