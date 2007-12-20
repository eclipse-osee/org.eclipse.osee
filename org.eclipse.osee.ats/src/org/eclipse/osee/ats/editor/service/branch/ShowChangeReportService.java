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

import java.sql.SQLException;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.editor.SMAWorkFlowSection;
import org.eclipse.osee.ats.editor.service.WorkPageService;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.skynet.core.event.LocalTransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.RemoteTransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.skynet.core.event.TransactionEvent;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.ui.plugin.event.Event;
import org.eclipse.osee.framework.ui.plugin.event.IEventReceiver;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author Donald G. Dunne
 */
public class ShowChangeReportService extends WorkPageService implements IEventReceiver {
   private static final TransactionIdManager transactionIdManager = TransactionIdManager.getInstance();

   private Hyperlink link;

   // Since this service is only going to be added for the Implement state, Location.AllState will
   // work
   public ShowChangeReportService(SMAManager smaMgr, AtsWorkPage page, XFormToolkit toolkit, SMAWorkFlowSection section) {
      super("", smaMgr, page, toolkit, section, CreateWorkingBranchService.BRANCH_CATEGORY, Location.AllState);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.editor.statistic.WorkPageStatistic#create()
    */
   @Override
   public void create(Group workComp) {
      link = toolkit.createHyperlink(workComp, "Show Change Report", SWT.NONE);
      link.addHyperlinkListener(new IHyperlinkListener() {

         public void linkEntered(HyperlinkEvent e) {
         }

         public void linkExited(HyperlinkEvent e) {
         }

         public void linkActivated(HyperlinkEvent e) {
            smaMgr.getBranchMgr().showChangeReport();
         }
      });
      SkynetEventManager.getInstance().register(LocalTransactionEvent.class, this);
      SkynetEventManager.getInstance().register(RemoteTransactionEvent.class, this);
      refresh();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.editor.statistic.WorkPageStatistic#refresh()
    */
   @Override
   public void refresh() {
      if (link != null && !link.isDisposed()) {
         boolean enabled = false;
         try {
            if (smaMgr.getBranchMgr().isWorkingBranch()) {
               try {
                  Pair<TransactionId, TransactionId> transactionToFrom =
                        transactionIdManager.getStartEndPoint(smaMgr.getBranchMgr().getBranch());
                  enabled = !transactionToFrom.getKey().equals(transactionToFrom.getValue());
               } catch (SQLException ex) {
                  OSEELog.logException(getClass(), ex, false);
                  enabled = false;
               }
            } else {
               enabled =
                     ((smaMgr.getBranchMgr().getTransactionIdInt() != null) && (smaMgr.getBranchMgr().getTransactionIdInt() > 0));
            }
         } catch (Exception ex) {
            OSEELog.logException(AtsPlugin.class, ex, false);
         }

         link.setText(enabled ? "Show Change Report" : "Show Change Report\n(no changes)");
         link.setEnabled(enabled);
         link.setUnderlined(enabled);
      }
   }

   public void onEvent(Event event) {
      if (event instanceof TransactionEvent) refresh();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.jdk.core.event.IEventReceiver#runOnEventInDisplayThread()
    */
   public boolean runOnEventInDisplayThread() {
      return true;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.editor.service.WorkPageService#dispose()
    */
   @Override
   public void dispose() {
      SkynetEventManager.getInstance().unRegisterAll(this);
   }
}
