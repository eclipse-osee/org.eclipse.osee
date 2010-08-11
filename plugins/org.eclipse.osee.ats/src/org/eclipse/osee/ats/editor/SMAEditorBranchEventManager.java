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
import java.util.logging.Level;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.event2.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event2.filter.IEventFilter;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * Common location for event handling for SMAEditors in order to keep number of registrations and processing to a
 * minimum.
 * 
 * @author Donald G. Dunne
 */
public class SMAEditorBranchEventManager implements IBranchEventListener {

   List<ISMAEditorEventHandler> handlers = new ArrayList<ISMAEditorEventHandler>();
   static SMAEditorBranchEventManager instance;

   public static void add(ISMAEditorEventHandler iWorldEventHandler) {
      if (instance == null) {
         instance = new SMAEditorBranchEventManager();
         OseeEventManager.addListener(instance);
      }
      instance.handlers.add(iWorldEventHandler);
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
   public void handleBranchEventREM1(Sender sender, BranchEventType branchModType, int branchId) {
      // do nothing - <REM2> shouldn't be called unless new event system available
   }

   @Override
   public void handleBranchEvent(Sender sender, BranchEvent branchEvent) {
      try {
         handleBranchEvent(branchEvent.getEventType(), BranchManager.getBranchByGuid(branchEvent.getBranchGuid()));
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   private void handleBranchEvent(BranchEventType branchModType, Branch branch) {
      for (final ISMAEditorEventHandler handler : handlers) {
         if (handler.isDisposed()) {
            System.out.println("Unexpected handler disposed but not unregistered.");
         }
         final StateMachineArtifact sma = handler.getSMAEditor().getSma();
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

   @Override
   public void handleLocalBranchToArtifactCacheUpdateEvent(Sender sender) {
      // do nothing
   }
}
