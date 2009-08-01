/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.editor.service.branch;

import org.eclipse.jface.action.Action;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.editor.service.WorkPageService;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class ShowMergeManagerService extends WorkPageService implements IBranchEventListener {

   // Since this service is only going to be added for the Implement state, Location.AllState will
   // work
   public ShowMergeManagerService(SMAManager smaMgr) {
      super(smaMgr);
   }

   private Action toolBarAction;

   @Override
   public Action createToolbarService() {
      toolBarAction = new Action(getName(), Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            smaMgr.getBranchMgr().showMergeManager();
         }
      };
      toolBarAction.setToolTipText(getName());
      toolBarAction.setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.OUTGOING_MERGED));

      OseeEventManager.addListener(this);
      refresh();
      return toolBarAction;
   }

   @Override
   public String getName() {
      return "Show Merge Manager";
   }

   @Override
   public void refresh() {
      if (toolBarAction != null) {
         toolBarAction.setEnabled(isEnabled());
      }
   }

   private boolean isEnabled() {
      boolean enabled = false;
      try {
         enabled = smaMgr.getBranchMgr().isWorkingBranchInWork() || smaMgr.getBranchMgr().isCommittedBranchExists();
      } catch (Exception ex) {
         // do nothing
      }
      return enabled;
   }

   @Override
   public void dispose() {
      OseeEventManager.removeListener(this);
   }

   @Override
   public void handleBranchEvent(Sender sender, BranchEventType branchModType, int branchId) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            refresh();
         }
      });

   }

   @Override
   public void handleLocalBranchToArtifactCacheUpdateEvent(Sender sender) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            refresh();
         }
      });

   }

}
