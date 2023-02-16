/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.editor.tab.workflow.widget;

import java.util.Collections;
import java.util.logging.Level;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.editor.tab.workflow.util.WfePromptChangeStatus;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workdef.StateXWidgetPage;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
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

   private final AbstractWorkflowArtifact sma;
   private final StateXWidgetPage page;
   private final boolean isCurrentState;
   private final WorkflowEditor editor;

   public StatePercentCompleteXWidget(IManagedForm managedForm, StateXWidgetPage page, final AbstractWorkflowArtifact sma, Composite composite, int horizontalSpan, XModifiedListener xModListener, boolean isCurrentState, WorkflowEditor editor) {
      super("\"" + page.getName() + "\"" + " State Percent Complete");
      this.page = page;
      this.sma = sma;
      this.isCurrentState = isCurrentState;
      this.editor = editor;
      if (xModListener != null) {
         addXModifiedListener(xModListener);
      }
      setEditable(isCurrentState && !sma.isReadOnly());
      setFillHorizontally(true);
      setToolTip(TOOLTIP);
      super.createWidgets(managedForm, composite, horizontalSpan);
   }

   @Override
   public boolean handleSelection() {
      try {
         WfePromptChangeStatus.promptChangeStatus(Collections.singleton(sma), false);
         editor.onDirtied();
         return true;
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   public final static String TOOLTIP = "Calculation: \n     State Percent: amount entered by user\n" +
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
         setEditable(isCurrentState && !sma.isReadOnly());
         StringBuffer sb = new StringBuffer();
         boolean breakoutNeeded = false;
         if (sma instanceof TeamWorkFlowArtifact && AtsApiService.get().getTaskService().hasTasks(
            (TeamWorkFlowArtifact) sma)) {
            sb.append(String.format("\n        Task  Percent: %d",
               AtsApiService.get().getEarnedValueService().getPercentCompleteFromTasks(sma, page)));
            breakoutNeeded = true;
         }
         if (sma.isTeamWorkflow() && AtsApiService.get().getReviewService().hasReviews((TeamWorkFlowArtifact) sma)) {
            sb.append(String.format("\n     Review Percent: %d",
               AtsApiService.get().getEarnedValueService().getPercentCompleteFromReviews(sma, page)));
            breakoutNeeded = true;
         }
         if (breakoutNeeded) {
            if (!getControl().isDisposed()) {
               setToolTip(sb.toString() + "\n" + TOOLTIP);
            }
            return String.valueOf(AtsApiService.get().getWorkItemMetricsService().getPercentCompleteSMAStateTotal(sma,
               page, AtsApiService.get()));
         } else {
            return String.valueOf(AtsApiService.get().getWorkItemMetricsService().getPercentComplete(sma));
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         return ex.getLocalizedMessage();
      }
   }

}
