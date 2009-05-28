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

import java.util.Arrays;
import java.util.logging.Level;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.editor.SMAPromptChangeStatus;
import org.eclipse.osee.ats.editor.SMAStateMetricsHeader;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author Donald G. Dunne
 */
public class StatePercentCompleteStat {

   private Hyperlink link;
   private AtsWorkPage page;
   private final SMAManager smaMgr;

   public StatePercentCompleteStat(SMAManager smaMgr) {
      this.smaMgr = smaMgr;
   }

   public void createSidebarService(Composite composite, AtsWorkPage page, XFormToolkit toolkit, final SMAStateMetricsHeader header) throws OseeCoreException {
      this.page = page;
      link = toolkit.createHyperlink(composite, "", SWT.NONE);
      if (smaMgr.getSma().isReadOnly())
         link.addHyperlinkListener(new ReadOnlyHyperlinkListener(smaMgr));
      else
         link.addHyperlinkListener(new IHyperlinkListener() {

            public void linkEntered(HyperlinkEvent e) {
            }

            public void linkExited(HyperlinkEvent e) {
            }

            public void linkActivated(HyperlinkEvent e) {
               try {
                  SMAPromptChangeStatus.promptChangeStatus(Arrays.asList(smaMgr.getSma()), false);
                  header.refresh();
               } catch (Exception ex) {
                  OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         });
      link.setToolTipText(TOOLTIP);
      link.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      refresh();
   }
   public static String TOOLTIP = "Calculation: \n     State Percent: amount entered by user\n" +
   //
   "     Task Percent: total percent of all tasks related to state / number of tasks related to state\n" +
   //
   "     Review Percent: total percent of all reviews related to state / number of reviews related to state\n" +
   //
   "Total State Percent: state percent + all task percents + all review percents / 1 + num tasks + num reviews";

   public void refresh() {
      if (page == null && link != null && !link.isDisposed()) {
         link.setText("State Percent Error: page == null");
         return;
      } else if (page == null) return;

      try {
         StringBuffer sb =
               new StringBuffer(String.format("        State Percent: %d", smaMgr.getStateMgr().getPercentComplete(
                     page.getName())));
         boolean breakoutNeeded = false;
         if (smaMgr.getTaskMgr().hasTaskArtifacts()) {
            sb.append(String.format("\n        Task  Percent: %d", smaMgr.getTaskMgr().getPercentComplete(
                  page.getName())));
            breakoutNeeded = true;
         }
         if (smaMgr.getReviewManager().hasReviews()) {
            sb.append(String.format("\n     Review Percent: %d", smaMgr.getReviewManager().getPercentComplete(
                  page.getName())));
            breakoutNeeded = true;
         }
         if (breakoutNeeded) {
            setString(String.format("Total State Percent: %d", smaMgr.getSma().getPercentCompleteSMAStateTotal(
                  page.getName())));
            if (link != null && !link.isDisposed()) {
               link.setToolTipText(sb.toString() + "\n" + TOOLTIP);
            }
         } else {
            setString(String.format("State Percent Complete: %d", smaMgr.getStateMgr().getPercentComplete(
                  page.getName())));
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   private void setString(String str) {
      if (page != null && link != null && !link.isDisposed()) {
         link.setText(str);
         link.update();
      } else if (link != null && !link.isDisposed()) link.setText("State Hours Spent Error: page == null");
   }

   public String getName() {
      return "Percent Complete";
   }

}
