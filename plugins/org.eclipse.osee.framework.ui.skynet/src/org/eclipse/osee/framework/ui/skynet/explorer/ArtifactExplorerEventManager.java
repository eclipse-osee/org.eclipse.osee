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
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.event.DefaultBasicGuidArtifact;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.event.EventUtil;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.EventModType;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.ui.skynet.IArtifactExplorerEventHandler;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

/**
 * Common location for event handling for ArtifactExplorers in order to keep number of registrations and processing to a
 * minimum.
 * 
 * @author Donald G. Dunne
 */
public class ArtifactExplorerEventManager implements IArtifactEventListener {

   List<IArtifactExplorerEventHandler> handlers = new CopyOnWriteArrayList<>();
   static ArtifactExplorerEventManager instance;

   public static void add(IArtifactExplorerEventHandler iWorldEventHandler) {
      if (instance == null) {
         instance = new ArtifactExplorerEventManager();
         OseeEventManager.addListener(instance);
      }
      instance.handlers.add(iWorldEventHandler);
   }

   public static void remove(IArtifactExplorerEventHandler iWorldEventHandler) {
      if (instance != null) {
         instance.handlers.remove(iWorldEventHandler);
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
   private boolean isArtifactExplorerValidForEvents(ArtifactExplorer artifactExplorer, Long branchUuidFromEvent) {
      boolean toReturn = false;
      if (artifactExplorer != null) {
         Branch branch = artifactExplorer.getBranch();
         toReturn =
            branch != null && branchUuidFromEvent.equals(branch.getUuid()) && !branch.isDeleted() && !branch.isPurged();
      }
      return toReturn;
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
         } else if (isArtifactExplorerValidForEvents(handler.getArtifactExplorer(), artifactEvent.getBranchUuid())) {
            handlersToProcess.add(handler);
         }
      }

      EventUtil.eventLog("ArtifacExplorer: handleArtifactEvent called [" + artifactEvent + "] - sender " + sender + "");
      final Collection<Artifact> modifiedArts =
         artifactEvent.getCacheArtifacts(EventModType.Modified, EventModType.Reloaded);
      final Collection<Artifact> relModifiedArts = artifactEvent.getRelCacheArtifacts();
      final Collection<EventBasicGuidArtifact> deletedPurgedArts =
         artifactEvent.get(EventModType.Deleted, EventModType.Purged);
      final Collection<DefaultBasicGuidArtifact> relOrderChangedArtifacts = artifactEvent.getRelOrderChangedArtifacts();

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

                     for (DefaultBasicGuidArtifact guidArt : relOrderChangedArtifacts) {
                        try {
                           Artifact artifact = ArtifactCache.getActive(guidArt);
                           if (artifact != null) {
                              handler.getArtifactExplorer().getTreeViewer().refresh(artifact);
                           }
                        } catch (Exception ex) {
                           OseeLog.log(Activator.class, Level.SEVERE, ex);
                        }
                     }
                  }
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, Level.SEVERE,
                     "Error processing event handler for modified - " + handler, ex);
               }
            }
         }
      });
   }
}
