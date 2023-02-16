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

package org.eclipse.osee.ats.ide.editor.tab.workflow.util;

import java.util.Arrays;
import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.osee.ats.api.column.AtsColumnTokens;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.note.AtsStateNote;
import org.eclipse.osee.ats.core.column.ChangeTypeColumn;
import org.eclipse.osee.ats.core.workflow.log.AtsLogUtility;
import org.eclipse.osee.ats.ide.column.DeadlineColumn;
import org.eclipse.osee.ats.ide.editor.tab.workflow.widget.ReviewInfoXWidget;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.Overview;
import org.eclipse.osee.ats.ide.workdef.StateXWidgetPage;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.WorkflowManager;
import org.eclipse.osee.ats.ide.workflow.task.TaskArtifact;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.jdk.core.result.Manipulations;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;

/**
 * @author Donald G. Dunne
 */
public class WfePrint extends Action {

   private final AbstractWorkflowArtifact sma;
   boolean includeTaskList = true;
   private final String normalColor = "#FFFFFF";
   private final String activeColor = "#EEEEEE";

   public WfePrint(AbstractWorkflowArtifact sma) {
      super();
      this.sma = sma;
   }

   @Override
   public void run() {
      try {
         XResultData xResultData = getResultData();
         XResultDataUI.report(xResultData, "Print Preview of " + sma.getName(), Manipulations.RAW_HTML);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

   }

   public XResultData getResultData() {
      XResultData resultData = new XResultData();
      resultData.addRaw(AHTML.beginMultiColumnTable(100));
      resultData.addRaw(AHTML.addRowMultiColumnTable(
         new String[] {AHTML.getLabelValueStr(AHTML.LABEL_FONT, "Title: ", sma.getName())}));
      resultData.addRaw(AHTML.endMultiColumnTable());
      resultData.addRaw(AHTML.beginMultiColumnTable(100));
      resultData.addRaw(AHTML.addRowMultiColumnTable(new String[] {
         //
         AHTML.getLabelValueStr(AHTML.LABEL_FONT, "Current State: ", sma.getCurrentStateName()),
         //
         AHTML.getLabelValueStr(AHTML.LABEL_FONT, "Team: ",
            AtsApiService.get().getColumnService().getColumn(AtsColumnTokens.TeamColumn).getColumnText(sma)),
         //
         AHTML.getLabelValueStr(AHTML.LABEL_FONT, "Assignees: ",
            AtsApiService.get().getColumnService().getColumnText(AtsColumnTokens.AssigneeColumn, sma)),
         //
         AHTML.getLabelValueStr(AHTML.LABEL_FONT, "Originator: ", sma.getCreatedBy().getName()),
         //
         AHTML.getLabelValueStr(AHTML.LABEL_FONT, "Created: ", DateUtil.getMMDDYYHHMM(sma.getCreatedDate()))

      }));
      resultData.addRaw(AHTML.endMultiColumnTable());
      resultData.addRaw(AHTML.beginMultiColumnTable(100));
      resultData.addRaw(AHTML.addRowMultiColumnTable(new String[] {
         //
         AHTML.getLabelValueStr(AHTML.LABEL_FONT, "Change Type: ",
            ChangeTypeColumn.getChangeTypeStr(sma, AtsApiService.get())),
         AHTML.getLabelValueStr(AHTML.LABEL_FONT, "Priority: ",
            sma.getSoleAttributeValue(AtsAttributeTypes.Priority, "")),
         AHTML.getLabelValueStr(AHTML.LABEL_FONT, "Need By: ", DeadlineColumn.getDateStr(sma))}));

      String computedId = AtsApiService.get().getWorkItemService().getCombinedPcrId(sma);
      resultData.addRaw(AHTML.addRowMultiColumnTable(new String[] {
         //
         AHTML.getLabelValueStr(AHTML.LABEL_FONT, "Workflow: ", sma.getArtifactTypeName()),
         AHTML.getLabelValueStr(AHTML.LABEL_FONT, "ID: ", computedId)}));
      resultData.addRaw(AHTML.endMultiColumnTable());
      for (AtsStateNote note : AtsApiService.get().getWorkItemService().getStateNoteService().getNotes(sma)) {
         if (note.getState().equals("")) {
            resultData.addRaw(note.toHTML() + AHTML.newline());
         }
      }
      getWorkFlowHtml(resultData);
      if (includeTaskList) {
         getTaskHtml(resultData);
      }
      resultData.addRaw(AHTML.newline());
      resultData.addRaw(AtsLogUtility.getHtml(sma.getLog(),
         AtsApiService.get().getLogFactory().getLogProvider(sma, AtsApiService.get().getAttributeResolver()),
         AtsApiService.get().getUserService()));

      XResultData rd = new XResultData();
      rd.addRaw(AHTML.beginMultiColumnTable(100, 1));
      rd.addRaw(AHTML.addRowMultiColumnTable(
         new String[] {XResultDataUI.getReport(resultData, "").getManipulatedHtml(Arrays.asList(Manipulations.NONE))}));
      rd.addRaw(AHTML.endMultiColumnTable());

      return rd;
   }

   private void getTaskHtml(XResultData rd) {
      if (!(sma instanceof TeamWorkFlowArtifact)) {
         return;
      }
      try {
         rd.addRaw(AHTML.addSpace(1) + AHTML.getLabelStr(AHTML.LABEL_FONT, "Tasks"));
         rd.addRaw(AHTML.startBorderTable(100, Overview.normalColor, ""));
         rd.addRaw(
            AHTML.addHeaderRowMultiColumnTable(new String[] {"Title", "State", "POC", "%", "Hrs", "Resolution", "ID"}));
         for (IAtsTask task : AtsApiService.get().getTaskService().getTasks((TeamWorkFlowArtifact) sma)) {
            TaskArtifact art = (TaskArtifact) task;
            rd.addRaw(AHTML.addRowMultiColumnTable(new String[] {
               art.getName(),
               art.getCurrentStateName().replaceAll("(Task|State)", ""),
               AtsApiService.get().getColumnService().getColumnText(AtsColumnTokens.AssigneeColumn, art),
               AtsApiService.get().getWorkItemMetricsService().getPercentCompleteTotal(art) + "",
               AtsApiService.get().getWorkItemMetricsService().getHoursSpentTotal(art) + "",
               art.getSoleAttributeValue(AtsAttributeTypes.Resolution, ""),
               art.getAtsId()}));
         }
         rd.addRaw(AHTML.endBorderTable());
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         rd.addRaw("Task Exception - " + ex.getLocalizedMessage());
      }
   }

   private void getWorkFlowHtml(XResultData rd) {
      // Only display current or past states
      for (StateXWidgetPage statePage : WorkflowManager.getStatePagesOrderedByOrdinal(sma)) {
         if (sma.isInState(statePage) || sma.getStateMgr().isStateVisited(statePage)) {
            // Don't show completed or cancelled state if not currently those state
            if (statePage.getStateType().isCompletedState() && !sma.isCompleted()) {
               continue;
            }
            if (statePage.getStateType().isCancelledState() && !sma.isCancelled()) {
               continue;
            }
            StringBuffer notesSb = new StringBuffer();
            for (AtsStateNote note : AtsApiService.get().getWorkItemService().getStateNoteService().getNotes(sma)) {
               if (note.getState().equals(statePage.getName())) {
                  notesSb.append(note.toHTML());
                  notesSb.append(AHTML.newline());
               }
            }
            if (sma.isInState(statePage) || sma.getStateMgr().isStateVisited(statePage)) {
               statePage.generateLayoutDatas();
               rd.addRaw(statePage.getHtml(sma.isInState(statePage) ? activeColor : normalColor, notesSb.toString(),
                  getReviewData(sma, statePage)));
               rd.addRaw(AHTML.newline());
            }
         }
      }
   }

   private String getReviewData(AbstractWorkflowArtifact sma, StateXWidgetPage page) {
      if (sma.isOfType(AtsArtifactTypes.TeamWorkflow)) {
         return ReviewInfoXWidget.toHTML((TeamWorkFlowArtifact) sma, page);
      }
      return "";
   }

   public boolean isIncludeTaskList() {
      return includeTaskList;
   }

   public void setIncludeTaskList(boolean includeTaskList) {
      this.includeTaskList = includeTaskList;
   }

}
