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

package org.eclipse.osee.ats.editor.service;

import java.util.logging.Level;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.editor.SMAWorkFlowSection;
import org.eclipse.osee.ats.editor.stateItem.AtsDebugWorkPage;
import org.eclipse.osee.ats.editor.stateItem.AtsLogWorkPage;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author Donald G. Dunne
 */
public class StateHoursSpentStat extends WorkPageService {

   private Hyperlink link;
   private Label label;
   private AtsWorkPage page;

   public StateHoursSpentStat(SMAManager smaMgr) {
      super(smaMgr);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.service.WorkPageService#isShowSidebarService(org.eclipse.osee.ats.workflow.AtsWorkPage)
    */
   @Override
   public boolean isShowSidebarService(AtsWorkPage page) throws OseeCoreException {
      return !page.getId().equals(AtsLogWorkPage.PAGE_ID) && !page.getId().equals(AtsDebugWorkPage.PAGE_ID) && !isCompleteCancelledState(page);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.service.WorkPageService#createSidebarService(org.eclipse.swt.widgets.Group, org.eclipse.osee.ats.workflow.AtsWorkPage, org.eclipse.osee.framework.ui.skynet.XFormToolkit, org.eclipse.osee.ats.editor.SMAWorkFlowSection)
    */
   @Override
   public void createSidebarService(Group workGroup, AtsWorkPage page, XFormToolkit toolkit, final SMAWorkFlowSection section) throws OseeCoreException {
      this.page = page;
      if (!isCompleteCancelledState(page) && smaMgr.isCurrentState(page.getName())) {
         link = toolkit.createHyperlink(workGroup, "", SWT.NONE);
         if (smaMgr.getSma().isReadOnly())
            link.addHyperlinkListener(readOnlyHyperlinkListener);
         else
            link.addHyperlinkListener(new IHyperlinkListener() {

               public void linkEntered(HyperlinkEvent e) {
               }

               public void linkExited(HyperlinkEvent e) {
               }

               public void linkActivated(HyperlinkEvent e) {
                  try {
                     if (smaMgr.promptChangeStatus(false)) section.refreshStateServices();
                  } catch (Exception ex) {
                     OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
                  }
               }
            });
      } else
         label = toolkit.createLabel(workGroup, "", SWT.NONE);
      refresh();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.service.WorkPageService#getName()
    */
   @Override
   public String getName() {
      return "Hours Spent";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.service.WorkPageService#getSidebarCategory()
    */
   @Override
   public String getSidebarCategory() {
      return ServicesArea.STATISTIC_CATEGORY;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.editor.statistic.WorkPageStatistic#refresh()
    */
   @Override
   public void refresh() {
      if (page == null && link != null && !link.isDisposed()) {
         link.setText("State Hours Error: page == null");
         return;
      } else if (page == null && label != null && !label.isDisposed()) {
         label.setText("State Hours Error: page == null");
         return;
      } else if (page == null) return;
      try {
         StringBuffer sb =
               new StringBuffer(String.format("        State Hours: %5.2f", smaMgr.getStateMgr().getHoursSpent(
                     page.getName())));
         boolean breakoutNeeded = false;
         if (smaMgr.getTaskMgr().hasTaskArtifacts()) {
            sb.append(String.format("\n        Task  Hours: %5.2f", smaMgr.getTaskMgr().getHoursSpent(page.getName())));
            breakoutNeeded = true;
         }
         if (smaMgr.getReviewManager().hasReviews()) {
            sb.append(String.format("\n     Review Hours: %5.2f", smaMgr.getReviewManager().getHoursSpent(
                  page.getName())));
            breakoutNeeded = true;
         }
         if (breakoutNeeded) {
            sb.append(String.format("\nTotal State Hours: %5.2f", smaMgr.getSma().getHoursSpentSMAStateTotal(
                  page.getName())));
            setString(sb.toString());
         } else {
            setString(String.format("State Hours Spent: %5.2f", smaMgr.getStateMgr().getHoursSpent(page.getName())));
         }

      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   private void setString(String str) {
      if (page != null && link != null && !link.isDisposed())
         link.setText(str);
      else if (page != null && label != null && !label.isDisposed())
         label.setText(str);
      else if (label != null && !label.isDisposed())
         label.setText("State Hours Spent Error: page == null");
      else if (link != null && !link.isDisposed()) link.setText("State Hours Spent Error: page == null");
   }
}
