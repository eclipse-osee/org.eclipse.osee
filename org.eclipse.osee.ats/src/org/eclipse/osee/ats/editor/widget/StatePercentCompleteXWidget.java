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
package org.eclipse.osee.ats.editor.widget;

import java.util.Collections;
import java.util.logging.Level;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.editor.SMAPromptChangeStatus;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkLabelValueSelection;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;

/**
 * @author Donald G. Dunne
 */
public class StatePercentCompleteXWidget extends XHyperlinkLabelValueSelection {

   private final SMAManager smaMgr;
   private final AtsWorkPage page;

   public StatePercentCompleteXWidget(IManagedForm managedForm, AtsWorkPage page, final SMAManager smaMgr, Composite composite, int horizontalSpan, XModifiedListener xModListener) {
      super("\"" + page.getName() + "\"" + " State Percent Complete");
      this.page = page;
      this.smaMgr = smaMgr;
      if (xModListener != null) {
         addXModifiedListener(xModListener);
      }
      try {
         setEditable(!smaMgr.getSma().isReadOnly());
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE, ex);
         setEditable(false);
      }
      setFillHorizontally(true);
      setToolTip(TOOLTIP);
      super.createWidgets(managedForm, composite, horizontalSpan);
   }

   @Override
   public boolean handleSelection() {
      try {
         SMAPromptChangeStatus.promptChangeStatus(Collections.singleton(smaMgr.getSma()), false);
         smaMgr.getEditor().onDirtied();
         return true;
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   public static String TOOLTIP = "Calculation: \n     State Percent: amount entered by user\n" +
   //
   "     Task Percent: total percent of all tasks related to state / number of tasks related to state\n" +
   //
   "     Review Percent: total percent of all reviews related to state / number of reviews related to state\n" +
   //
   "Total State Percent: state percent + all task percents + all review percents / 1 + num tasks + num reviews";

   @Override
   public String getCurrentValue() {
      if (page == null) {
         return "page == null";
      }
      try {
         setEditable(!smaMgr.getSma().isReadOnly());
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
            if (!getControl().isDisposed()) {
               setToolTip(sb.toString() + "\n" + TOOLTIP);
            }
            return String.valueOf(smaMgr.getSma().getPercentCompleteSMAStateTotal(page.getName()));
         } else {
            return String.valueOf(smaMgr.getStateMgr().getPercentComplete(page.getName()));
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         return ex.getLocalizedMessage();
      }
   }

}
