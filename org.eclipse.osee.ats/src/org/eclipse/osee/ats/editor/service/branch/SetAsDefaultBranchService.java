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

import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.editor.SMAWorkFlowSection;
import org.eclipse.osee.ats.editor.service.WorkPageService;
import org.eclipse.osee.ats.util.AtsBranchManager;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.skynet.core.event.LocalBranchEvent;
import org.eclipse.osee.framework.skynet.core.event.LocalBranchToArtifactCacheUpdateEvent;
import org.eclipse.osee.framework.skynet.core.event.LocalTransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.RemoteBranchEvent;
import org.eclipse.osee.framework.skynet.core.event.RemoteTransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.ui.plugin.event.Event;
import org.eclipse.osee.framework.ui.plugin.event.IEventReceiver;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author Donald G. Dunne
 */
public class SetAsDefaultBranchService extends WorkPageService implements IEventReceiver {

   private Hyperlink link;

   // Since this service is only going to be added for the Implement state, Location.AllState will
   // work
   public SetAsDefaultBranchService(SMAManager smaMgr) {
      super(smaMgr);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.service.WorkPageService#isShowSidebarService(org.eclipse.osee.ats.workflow.AtsWorkPage)
    */
   @Override
   public boolean isShowSidebarService(AtsWorkPage page) {
      return isCurrentState(page);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.service.WorkPageService#createSidebarService(org.eclipse.swt.widgets.Group, org.eclipse.osee.ats.workflow.AtsWorkPage, org.eclipse.osee.framework.ui.skynet.XFormToolkit, org.eclipse.osee.ats.editor.SMAWorkFlowSection)
    */
   @Override
   public void createSidebarService(Group workGroup, AtsWorkPage page, XFormToolkit toolkit, SMAWorkFlowSection section) {
      link = toolkit.createHyperlink(workGroup, getName(), SWT.NONE);
      link.addHyperlinkListener(new IHyperlinkListener() {

         public void linkEntered(HyperlinkEvent e) {
         }

         public void linkExited(HyperlinkEvent e) {
         }

         public void linkActivated(HyperlinkEvent e) {
            performService();
         }
      });
      SkynetEventManager.getInstance().register(LocalTransactionEvent.class, this);
      SkynetEventManager.getInstance().register(RemoteTransactionEvent.class, this);
      SkynetEventManager.getInstance().register(LocalBranchEvent.class, this);
      SkynetEventManager.getInstance().register(RemoteBranchEvent.class, this);
      SkynetEventManager.getInstance().register(LocalBranchToArtifactCacheUpdateEvent.class, this);
      refresh();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.service.WorkPageService#getName()
    */
   @Override
   public String getName() {
      return "Set As Default Branch";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.service.WorkPageService#getSidebarCategory()
    */
   @Override
   public String getSidebarCategory() {
      return AtsBranchManager.BRANCH_CATEGORY;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.editor.statistic.WorkPageStatistic#refresh()
    */
   @Override
   public void refresh() {
      if (link != null && !link.isDisposed()) {
         boolean enabled = isEnabled();
         link.setEnabled(enabled);
         link.setUnderlined(enabled);
      }
   }

   private boolean isEnabled() {
      boolean enabled = false;
      try {
         if (smaMgr.getBranchMgr().isWorkingBranch()) {
            Pair<TransactionId, TransactionId> transactionToFrom =
                  TransactionIdManager.getInstance().getStartEndPoint(smaMgr.getBranchMgr().getWorkingBranch());
            enabled = !transactionToFrom.getKey().equals(transactionToFrom.getValue());
         } else {
            enabled = smaMgr.getBranchMgr().getTransactionId() != null;
         }
      } catch (Exception ex) {
         // do nothing
      }
      return enabled;
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
      smaMgr.getBranchMgr().setAsDefaultBranch();
   }

}
