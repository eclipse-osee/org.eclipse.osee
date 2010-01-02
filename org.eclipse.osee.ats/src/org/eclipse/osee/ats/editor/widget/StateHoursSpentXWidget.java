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
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TaskableStateMachineArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.editor.SMAPromptChangeStatus;
import org.eclipse.osee.ats.util.widgets.ReviewManager;
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
public class StateHoursSpentXWidget extends XHyperlinkLabelValueSelection {

   private final StateMachineArtifact sma;
   private final AtsWorkPage page;

   public StateHoursSpentXWidget(IManagedForm managedForm, AtsWorkPage page, final StateMachineArtifact sma, Composite composite, int horizontalSpan, XModifiedListener xModListener) {
      super("\"" + page.getName() + "\"" + " State Hours Spent");
      this.page = page;
      this.sma = sma;
      if (xModListener != null) {
         addXModifiedListener(xModListener);
      }
      setEditable(!sma.isReadOnly());
      setFillHorizontally(true);
      setToolTip(TOOLTIP);
      super.createWidgets(managedForm, composite, horizontalSpan);
   }

   @Override
   public boolean handleSelection() {
      try {
         SMAPromptChangeStatus.promptChangeStatus(Collections.singleton(sma), false);
         sma.getEditor().onDirtied();
         return true;
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   public static String TOOLTIP = "Calculation: \n     State Hours Spent: amount entered by user\n" +
   //
   "     Task Hours Spent: total hours spent of all tasks related to state\n" +
   //
   "     Review Hours Spent: total hours spent of all reviews related to state\n" +
   //
   "Total State Hours Spent: state hours + all task hours + all review hours";

   @Override
   public String getCurrentValue() {
      if (page == null) {
         return "page == null";
      }
      try {
         StringBuffer sb =
               new StringBuffer(String.format("        State Hours: %5.2f", sma.getStateMgr().getHoursSpent(
                     page.getName())));
         boolean breakoutNeeded = false;
         if (sma instanceof TaskableStateMachineArtifact) {
            if (((TaskableStateMachineArtifact) sma).hasTaskArtifacts()) {
               sb.append(String.format("\n        Task  Hours: %5.2f",
                     ((TaskableStateMachineArtifact) sma).getHoursSpentFromTasks(page.getName())));
               breakoutNeeded = true;
            }
         }
         if (sma.isTeamWorkflow() && ReviewManager.hasReviews((TeamWorkFlowArtifact) sma)) {
            sb.append(String.format("\n     Review Hours: %5.2f", ReviewManager.getHoursSpent(
                  (TeamWorkFlowArtifact) sma, page.getName())));
            breakoutNeeded = true;
         }
         if (breakoutNeeded) {
            setToolTip(sb.toString());
            return String.format("%5.2f", sma.getHoursSpentSMAStateTotal(page.getName()));
         } else {
            return String.format("%5.2f", sma.getStateMgr().getHoursSpent(page.getName()));
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         return ex.getLocalizedMessage();
      }
   }
}
