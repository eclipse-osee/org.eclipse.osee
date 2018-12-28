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
package org.eclipse.osee.ats.editor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * Common location for event handling for SMAEditors in order to keep number of registrations and processing to a
 * minimum.
 *
 * @author Donald G. Dunne
 */
public class WfeBranchEventManager implements IBranchEventListener {

   List<IWfeEventHandler> handlers = new CopyOnWriteArrayList<>();
   static WfeBranchEventManager instance = new WfeBranchEventManager();

   private WfeBranchEventManager() {
      OseeEventManager.addListener(this);
   }

   public static void add(IWfeEventHandler iWorldEventHandler) {
      OseeEventManager.addListener(instance);
      if (instance != null) {
         instance.handlers.add(iWorldEventHandler);
      }
   }

   public static void remove(IWfeEventHandler iWorldEventHandler) {
      if (instance != null) {
         instance.handlers.remove(iWorldEventHandler);
      }
   }

   @Override
   public List<? extends IEventFilter> getEventFilters() {
      return null;
   }

   @Override
   public void handleBranchEvent(Sender sender, BranchEvent branchEvent) {
      for (IWfeEventHandler handler : new ArrayList<>(handlers)) {
         if (handler.isDisposed()) {
            handlers.remove(handler);
         }
      }
      for (final IWfeEventHandler handler : handlers) {
         try {
            safelyProcessHandler(branchEvent.getEventType(), branchEvent.getSourceBranch());
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, "Error processing event handler - " + handler, ex);
         }
      }
   }

   private void safelyProcessHandler(BranchEventType branchEventType, BranchId branch) {
      for (final IWfeEventHandler handler : handlers) {
         if (handler.isDisposed()) {
            OseeLog.log(Activator.class, Level.SEVERE, "Unexpected handler disposed but not unregistered.");
         }
         final AbstractWorkflowArtifact awa = handler.getWorkflowEditor().getAwa();
         try {
            if (!awa.isTeamWorkflow()) {
               return;
            }
            switch (branchEventType) {
               case Added:
               case Deleting:
               case Deleted:
               case Purging:
               case Purged:
               case Committing:
               case Committed:
                  if (awa instanceof TeamWorkFlowArtifact) {
                     TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) awa;
                     BranchId assocBranch = AtsClientService.get().getBranchService().getWorkingBranch(teamArt);
                     if (branch.equals(assocBranch)) {
                        Displays.ensureInDisplayThread(new Runnable() {
                           @Override
                           public void run() {
                              if (handler.isDisposed()) {
                                 return;
                              }
                              try {
                                 handler.getWorkflowEditor().refreshPages();
                                 handler.getWorkflowEditor().onDirtied();
                              } catch (Exception ex) {
                                 OseeLog.log(Activator.class, Level.SEVERE, ex);
                              }
                           }
                        });
                     }
                  }
               default:
                  break;
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
   }
}
