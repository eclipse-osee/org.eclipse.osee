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
package org.eclipse.osee.ats.editor;

import java.util.logging.Level;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.editor.service.StateEstimatedHoursStat;
import org.eclipse.osee.ats.editor.service.StateHoursSpentStat;
import org.eclipse.osee.ats.editor.service.StatePercentCompleteStat;
import org.eclipse.osee.ats.editor.stateItem.AtsDebugWorkPage;
import org.eclipse.osee.ats.editor.stateItem.AtsLogWorkPage;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.event.FrameworkTransactionData;
import org.eclipse.osee.framework.skynet.core.event.IFrameworkTransactionEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Donald G. Dunne
 */
public class SMAStateMetricsHeader extends Composite implements IFrameworkTransactionEventListener {

   private final SMAManager smaMgr;
   private StateEstimatedHoursStat estHoursStat;
   private StatePercentCompleteStat percentComp;
   private StateHoursSpentStat hoursSpent;

   public SMAStateMetricsHeader(Composite parent, XFormToolkit toolkit, final SMAManager smaMgr, final AtsWorkPage page) throws OseeCoreException {
      super(parent, SWT.NONE);
      this.smaMgr = smaMgr;
      setLayout(ALayout.getZeroMarginLayout(3, false));
      setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      try {

         if (page.isCurrentNonCompleteCancelledState(smaMgr)) {
            percentComp = new StatePercentCompleteStat(smaMgr);
            percentComp.createSidebarService(this, page, toolkit, this);
         }
         if (!page.getId().equals(AtsLogWorkPage.PAGE_ID) && !page.getId().equals(AtsDebugWorkPage.PAGE_ID) && !page.isCompleteCancelledState()) {
            estHoursStat = new StateEstimatedHoursStat(smaMgr);
            estHoursStat.createSidebarService(this, page, toolkit, this);
         }
         if (!page.getId().equals(AtsLogWorkPage.PAGE_ID) && !page.getId().equals(AtsDebugWorkPage.PAGE_ID) && !page.isCompleteCancelledState()) {
            hoursSpent = new StateHoursSpentStat(smaMgr);
            hoursSpent.createSidebarService(this, page, toolkit, this);
         }

         SMAEditor.setLabelFonts(this, SMAEditor.getBoldLabelFont());
         refresh();

         OseeEventManager.addListener(this);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   public void refresh() throws OseeCoreException {
      if (isDisposed()) return;
      if (percentComp != null) percentComp.refresh();
      if (estHoursStat != null) estHoursStat.refresh();
      if (hoursSpent != null) hoursSpent.refresh();
      smaMgr.getEditor().onDirtied();
      layout();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.event.IFrameworkTransactionEventListener#handleFrameworkTransactionEvent(org.eclipse.osee.framework.skynet.core.event.Sender, org.eclipse.osee.framework.skynet.core.event.FrameworkTransactionData)
    */
   @Override
   public void handleFrameworkTransactionEvent(Sender sender, FrameworkTransactionData transData) throws OseeCoreException {
      if (smaMgr.isInTransition()) return;
      if (transData.branchId != AtsPlugin.getAtsBranch().getBranchId()) return;
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            try {
               refresh();
            } catch (OseeCoreException ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });
   }

   /* (non-Javadoc)
    * @see org.eclipse.swt.widgets.Widget#dispose()
    */
   @Override
   public void dispose() {
      OseeEventManager.removeListener(this);
      super.dispose();
   }

}
