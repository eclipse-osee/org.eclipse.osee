/*********************************************************************
 * Copyright (c) 2010 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.editor.event;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
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

   List<WorkflowEditor> editors = new CopyOnWriteArrayList<>();
   static WfeBranchEventManager instance = new WfeBranchEventManager();

   private WfeBranchEventManager() {
      OseeEventManager.addListener(this);
   }

   public static void add(WorkflowEditor editor) {
      OseeEventManager.addListener(instance);
      if (instance != null && !instance.editors.contains(editor)) {
         instance.editors.add(editor);
      }
   }

   public static void remove(WorkflowEditor editor) {
      if (instance != null) {
         instance.editors.remove(editor);
      }
   }

   @Override
   public List<? extends IEventFilter> getEventFilters() {
      return null;
   }

   @Override
   public void handleBranchEvent(Sender sender, BranchEvent branchEvent) {
      for (WorkflowEditor editor : new ArrayList<>(editors)) {
         if (editor.isDisposed()) {
            editors.remove(editor);
         }
      }
      for (final WorkflowEditor handler : editors) {
         try {
            safelyProcessHandler(branchEvent.getEventType(), branchEvent.getSourceBranch());
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, "Error processing event handler - " + handler, ex);
         }
      }
   }

   private void safelyProcessHandler(BranchEventType branchEventType, BranchId branch) {
      for (final WorkflowEditor editor : editors) {
         if (editor.isDisposed()) {
            OseeLog.log(Activator.class, Level.SEVERE, "Unexpected handler disposed but not unregistered.");
         }
         final AbstractWorkflowArtifact awa = editor.getWorkItem();
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
                     BranchId assocBranch = AtsApiService.get().getBranchService().getWorkingBranch(teamArt);
                     if (branch.equals(assocBranch)) {
                        Displays.ensureInDisplayThread(new Runnable() {
                           @Override
                           public void run() {
                              if (editor.isDisposed()) {
                                 return;
                              }
                              try {
                                 editor.refresh();
                                 editor.onDirtied();
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
