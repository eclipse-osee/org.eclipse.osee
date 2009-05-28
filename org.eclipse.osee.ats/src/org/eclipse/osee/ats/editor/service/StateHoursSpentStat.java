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
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author Donald G. Dunne
 */
public class StateHoursSpentStat {

   private Hyperlink link;
   private Label label;
   private AtsWorkPage page;
   private final SMAManager smaMgr;

   public StateHoursSpentStat(SMAManager smaMgr) {
      this.smaMgr = smaMgr;
   }

   public void createSidebarService(Composite workGroup, AtsWorkPage page, XFormToolkit toolkit, final SMAStateMetricsHeader header) throws OseeCoreException {
      this.page = page;
      if (!page.isCompleteCancelledState() && smaMgr.isCurrentState(page.getName())) {
         link = toolkit.createHyperlink(workGroup, "", SWT.NONE);
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
         link.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      } else {
         label = toolkit.createLabel(workGroup, "", SWT.NONE);
         label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      }
      refresh();
   }

   public String getName() {
      return "Hours Spent";
   }

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
            setString(String.format("Total State Hours: %5.2f", smaMgr.getSma().getHoursSpentSMAStateTotal(
                  page.getName())));
            if (link != null && !link.isDisposed()) {
               link.setToolTipText(sb.toString());
            }
            if (label != null && !label.isDisposed()) {
               label.setToolTipText(sb.toString());
            }
         } else {
            setString(String.format("State Hours Spent: %5.2f", smaMgr.getStateMgr().getHoursSpent(page.getName())));
         }

      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   private void setString(String str) {
      if (page != null && link != null && !link.isDisposed()) {
         link.setText(str);
         link.update();
      } else if (page != null && label != null && !label.isDisposed()) {
         label.setText(str);
         label.update();
      } else if (label != null && !label.isDisposed()) {
         label.setText("State Hours Spent Error: page == null");
         label.update();
      } else if (link != null && !link.isDisposed()) link.setText("State Hours Spent Error: page == null");
   }
}
