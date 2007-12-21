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
import org.eclipse.jface.action.Action;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.editor.SMAWorkFlowSection;
import org.eclipse.osee.ats.editor.service.WorkPageService;
import org.eclipse.osee.ats.editor.toolbar.IAtsEditorToolBarService;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.skynet.core.event.LocalTransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.RemoteTransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.ui.plugin.event.Event;
import org.eclipse.osee.framework.ui.plugin.event.IEventReceiver;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
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
public class ShowChangeReportService extends WorkPageService implements IEventReceiver, IAtsEditorToolBarService {
   private static final TransactionIdManager transactionIdManager = TransactionIdManager.getInstance();

   private Hyperlink link;
   private Action action;

   // Since this service is only going to be added for the Implement state, Location.AllState will
   // work
   public ShowChangeReportService(SMAManager smaMgr, AtsWorkPage page, XFormToolkit toolkit, SMAWorkFlowSection section) {
      super("Show Change Report", smaMgr, page, toolkit, section, CreateWorkingBranchService.BRANCH_CATEGORY,
            Location.AllState);
   }

   /*
    * This constructor is used for the toolbar service extension
    */
   public ShowChangeReportService(SMAManager smaMgr) {
      super("Show Change Report", smaMgr, null, null, null, null, null);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.editor.statistic.WorkPageStatistic#create()
    */
   @Override
   public void create(Group workComp) {
      link = toolkit.createHyperlink(workComp, getName(), SWT.NONE);
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
         boolean enabled = isEnabled();
         link.setText(enabled ? "Show Change Report" : "Show Change Report\n(no changes)");
         link.setEnabled(enabled);
         link.setUnderlined(enabled);
      }
   }

   public boolean isEnabled() {
      boolean enabled = false;
      try {
         if (smaMgr.getBranchMgr().isWorkingBranch()) {
            Pair<TransactionId, TransactionId> transactionToFrom =
                  transactionIdManager.getStartEndPoint(smaMgr.getBranchMgr().getWorkingBranch());
            enabled = !transactionToFrom.getKey().equals(transactionToFrom.getValue());
         } else {
            enabled = smaMgr.getBranchMgr().getTransactionId() != null;
         }
      } catch (SQLException ex) {
         OSEELog.logException(AtsPlugin.class, ex, false);
      }
      return enabled;
   }

   public void onEvent(Event event) {
      refreshToolbarAction();
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

   private void performService() {
      smaMgr.getBranchMgr().showChangeReport();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.toolbar.IAtsEditorToolBarService#getToolbarAction(org.eclipse.osee.ats.editor.SMAManager)
    */
   public Action getToolbarAction(SMAManager smaMgr) {
      action = new Action(getName(), Action.AS_PUSH_BUTTON) {
         public void run() {
            performService();
         }
      };
      action.setToolTipText(getName());
      action.setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("branch_change.gif"));
      return action;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.toolbar.IAtsEditorToolBarService#refreshToolbarAction()
    */
   public void refreshToolbarAction() {
      if (action != null) action.setEnabled(isEnabled());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.toolbar.IAtsEditorToolBarService#showInToolbar(org.eclipse.osee.ats.editor.SMAManager)
    */
   public boolean showInToolbar(SMAManager smaMgr) {
      return isEnabled();
   }

}
