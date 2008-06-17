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
import org.eclipse.osee.framework.skynet.core.event.LocalBranchEvent;
import org.eclipse.osee.framework.skynet.core.event.LocalBranchToArtifactCacheUpdateEvent;
import org.eclipse.osee.framework.skynet.core.event.RemoteBranchEvent;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.ui.plugin.event.Event;
import org.eclipse.osee.framework.ui.plugin.event.IEventReceiver;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;

/**
 * @author Donald G. Dunne
 */
public class ShowChangeReportToolbarServiceOld extends WorkPageService implements IEventReceiver {

   // Since this service is only going to be added for the Implement state, Location.AllState will
   // work
   public ShowChangeReportToolbarServiceOld(SMAManager smaMgr) {
      super(smaMgr);
   }

   Action toolBarAction;

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.service.WorkPageService#createToolbarService()
    */
   @Override
   public Action createToolbarService() {
      toolBarAction = new Action(getName(), Action.AS_PUSH_BUTTON) {
         public void run() {
            performService();
         }
      };
      toolBarAction.setToolTipText(getName());
      toolBarAction.setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("branch_change.gif"));
      SkynetEventManager.getInstance().register(LocalBranchEvent.class, this);
      SkynetEventManager.getInstance().register(RemoteBranchEvent.class, this);
      SkynetEventManager.getInstance().register(LocalBranchToArtifactCacheUpdateEvent.class, this);
      refresh();
      return toolBarAction;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.service.WorkPageService#getName()
    */
   @Override
   public String getName() {
      return "Show OLD Change Report";
   }

   private boolean isEnabled() {
      boolean enabled = false;
      try {
         enabled = smaMgr.getBranchMgr().isCommittedBranch() || smaMgr.getBranchMgr().isWorkingBranch();
      } catch (Exception ex) {
         // do nothing
      }
      return enabled;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.service.WorkPageService#refresh()
    */
   @Override
   public void refresh() {
      super.refresh();
      if (toolBarAction != null) {
         toolBarAction.setEnabled(isEnabled());
      }
   }

   public void onEvent(Event event) {
      refresh();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.jdk.core.event.IEventReceiver#runOnEventInDisplayThread()
    */
   public boolean runOnEventInDisplayThread() {
      return true;
   }

   private void performService() {
      smaMgr.getBranchMgr().showChangeReportOld();
   }
}