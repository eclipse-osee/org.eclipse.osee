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
package org.eclipse.osee.framework.ui.skynet.explorer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.event.EventUtil;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.AccessTopicEvent;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.EventModType;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.ui.skynet.IArtifactExplorerEventHandler;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

/**
 * Common location for event handling for ArtifactExplorers in order to keep number of registrations and processing to a
 * minimum.
 *
 * @author Donald G. Dunne
 */
public class ArtifactExplorerEventManager implements IArtifactEventListener, EventHandler {

   static List<IArtifactExplorerEventHandler> handlers = new CopyOnWriteArrayList<>();
   static ArtifactExplorerEventManager instance;

   static {
      instance = new ArtifactExplorerEventManager();
      OseeEventManager.addListener(instance);
   }

   public static synchronized void add(IArtifactExplorerEventHandler iWorldEventHandler) {
      ArtifactExplorerEventManager.handlers.add(iWorldEventHandler);
   }

   public static void remove(IArtifactExplorerEventHandler iWorldEventHandler) {
      if (instance != null) {
         ArtifactExplorerEventManager.handlers.remove(iWorldEventHandler);
      }
   }

   @Override
   public List<? extends IEventFilter> getEventFilters() {
      // Can't filter cause this class handles all artifact explorers which can care about different branches
      return null;
   }

   /**
    * @return true if branch is not null, matches the branch for the event and is not deleted or purged
    */
   private boolean isArtifactExplorerValidForEvents(ArtifactExplorer artifactExplorer, BranchId brancFromEvent) {
      if (artifactExplorer != null) {
         BranchId branch = artifactExplorer.getBranch();
         if (branch.isValid()) {
            BranchState state = BranchManager.getState(branch);
            return brancFromEvent.equals(branch) && !state.isDeleted() && !state.isPurged();
         }
      }
      return false;
   }

   @Override
   public void handleArtifactEvent(final ArtifactEvent artifactEvent, Sender sender) {
      IWorkbench workbench = PlatformUI.getWorkbench();
      if (workbench == null || workbench.isClosing() || workbench.isStarting()) {
         return;
      }

      // Do not process event if branch is null, deleted or purged.  But, don't want to remove as handler cause another branch may be selected
      final List<IArtifactExplorerEventHandler> handlersToProcess = new ArrayList<>();
      for (IArtifactExplorerEventHandler handler : handlers) {
         if (handler.isDisposed()) {
            handlers.remove(handler);
         } else if (isArtifactExplorerValidForEvents(handler.getArtifactExplorer(), artifactEvent.getBranch())) {
            handlersToProcess.add(handler);
         }
      }

      EventUtil.eventLog("ArtifacExplorer: handleArtifactEvent called [" + artifactEvent + "] - sender " + sender + "");
      final Collection<Artifact> modifiedArts =
         artifactEvent.getCacheArtifacts(EventModType.Modified, EventModType.Reloaded);
      Collection<Artifact> relCacheArtifacts = artifactEvent.getRelCacheArtifacts();
      final Collection<EventBasicGuidArtifact> deletedPurgedArts =
         artifactEvent.get(EventModType.Deleted, EventModType.Purged);

      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            if (!deletedPurgedArts.isEmpty()) {
               for (IArtifactExplorerEventHandler handler : handlersToProcess) {
                  try {
                     if (!handler.isDisposed()) {
                        handler.getArtifactExplorer().getTreeViewer().remove(
                           deletedPurgedArts.toArray(new Object[deletedPurgedArts.size()]));
                     }
                  } catch (Exception ex) {
                     OseeLog.log(Activator.class, Level.SEVERE,
                        "Error processing event handler for deleted - " + handler, ex);
                  }
               }
            }
            for (IArtifactExplorerEventHandler handler : handlersToProcess) {
               try {
                  if (!handler.isDisposed()) {
                     for (Artifact artifact : modifiedArts) {
                        // Don't refresh deleted artifacts
                        if (artifact.isDeleted()) {
                           continue;
                        }
                        handler.getArtifactExplorer().getTreeViewer().update(artifact, null);
                     }

                     // We do not need to refresh each artifact for each handler, just the handler itself
                     handler.getArtifactExplorer().getTreeViewer().refresh();

                     for (Artifact artifact : artifactEvent.getRelationOrderArtifacts()) {
                        try {
                           handler.getArtifactExplorer().getTreeViewer().refresh(artifact);
                        } catch (Exception ex) {
                           OseeLog.log(Activator.class, Level.SEVERE, ex);
                        }
                     }
                  }
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, "Error processing event handler for modified - " + handler,
                     ex);
               }
            }
         }
      });
   }

   @Override
   public void handleEvent(Event event) {
      try {
         if (AccessTopicEvent.ACCESS_ARTIFACT_LOCK_MODIFIED.matches(event)) {
            for (final IArtifactExplorerEventHandler handler : handlers) {
               if (!handler.isDisposed()) {
                  Displays.ensureInDisplayThread(new Runnable() {

                     @Override
                     public void run() {
                        handler.getArtifactExplorer().getTreeViewer().refresh();
                        handler.getArtifactExplorer().refreshBranchWarning();
                     }
                  });
               }
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

}
