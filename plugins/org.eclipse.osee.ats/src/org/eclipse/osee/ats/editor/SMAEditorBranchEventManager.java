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
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.framework.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
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
public class SMAEditorBranchEventManager implements IBranchEventListener {

   List<ISMAEditorEventHandler> handlers = new ArrayList<ISMAEditorEventHandler>();
   static SMAEditorBranchEventManager instance = new SMAEditorBranchEventManager();

   private SMAEditorBranchEventManager() {
      OseeEventManager.addListener(this);
   }

   public static void add(ISMAEditorEventHandler iWorldEventHandler) {
      OseeEventManager.addListener(instance);
   }

   public static void remove(ISMAEditorEventHandler iWorldEventHandler) {
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
      for (ISMAEditorEventHandler handler : new CopyOnWriteArrayList<ISMAEditorEventHandler>(handlers)) {
         if (handler.isDisposed()) {
            handlers.remove(handler);
         }
      }
      for (final ISMAEditorEventHandler handler : handlers) {
         try {
            safelyProcessHandler(branchEvent.getEventType(), BranchManager.getBranchByGuid(branchEvent.getBranchGuid()));
         } catch (BranchDoesNotExist ex) {
            // if branch doesn't exist (purged), don't need this event
         } catch (Exception ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, "Error processing event handler - " + handler, ex);
         }
      }
   }

   private void safelyProcessHandler(BranchEventType branchModType, Branch branch) {
      for (final ISMAEditorEventHandler handler : handlers) {
         if (handler.isDisposed()) {
            System.out.println("Unexpected handler disposed but not unregistered.");
         }
         final AbstractWorkflowArtifact sma = handler.getSMAEditor().getSma();
         try {
            if (!sma.isTeamWorkflow()) {
               return;
            }
            if (sma.isInTransition()) {
               return;
            }
            if (branchModType == BranchEventType.Added || branchModType == BranchEventType.Deleted || branchModType == BranchEventType.Purged || branchModType == BranchEventType.Committed) {
               if (branch.getAssociatedArtifactId() != sma.getArtId()) {
                  return;
               }
               Displays.ensureInDisplayThread(new Runnable() {
                  @Override
                  public void run() {
                     if (handler.isDisposed()) {
                        return;
                     }
                     try {
                        handler.getSMAEditor().refreshPages();
                        handler.getSMAEditor().onDirtied();
                     } catch (Exception ex) {
                        OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
                     }
                  }
               });
            }
         } catch (Exception ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         }
      }
   }

}
