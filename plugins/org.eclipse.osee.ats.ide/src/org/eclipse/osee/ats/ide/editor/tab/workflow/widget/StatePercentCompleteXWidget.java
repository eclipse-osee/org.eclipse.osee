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
package org.eclipse.osee.ats.ide.editor.tab.workflow.widget;

import java.util.Collections;
import java.util.logging.Level;
import org.eclipse.osee.ats.core.util.PercentCompleteTotalUtil;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.editor.tab.workflow.util.WfePromptChangeStatus;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
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
         StringBuffer sb = new StringBuffer(
            String.format("        State Percent: %d", sma.getStateMgr().getPercentComplete(page.getName())));
         boolean breakoutNeeded = false;
         if (sma instanceof TeamWorkFlowArtifact && AtsClientService.get().getTaskService().hasTasks(
            (TeamWorkFlowArtifact) sma)) {
            sb.append(String.format("\n        Task  Percent: %d",
               AtsClientService.get().getEarnedValueService().getPercentCompleteFromTasks(sma, page)));
            breakoutNeeded = true;
         }
         if (sma.isTeamWorkflow() && AtsClientService.get().getReviewService().hasReviews((TeamWorkFlowArtifact) sma)) {
            sb.append(String.format("\n     Review Percent: %d",
               AtsClientService.get().getEarnedValueService().getPercentCompleteFromReviews(sma, page)));
            breakoutNeeded = true;
         }
         if (breakoutNeeded) {
            if (!getControl().isDisposed()) {
               setToolTip(sb.toString() + "\n" + TOOLTIP);
            }
            return String.valueOf(PercentCompleteTotalUtil.getPercentCompleteSMAStateTotal(sma, page,
               AtsClientService.get().getServices()));
         } else {
            return String.valueOf(sma.getStateMgr().getPercentComplete(page.getName()));
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         return ex.getLocalizedMessage();
      }
   }

}
