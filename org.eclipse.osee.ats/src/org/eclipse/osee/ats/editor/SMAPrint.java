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

import java.util.Arrays;
import org.eclipse.jface.action.Action;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.NoteItem;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.task.TaskComposite;
import org.eclipse.osee.ats.world.IWorldViewArtifact;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.results.html.XResultPage.Manipulations;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;

/**
 * @author Donald G. Dunne
 */
public class SMAPrint extends Action {

   private final StateMachineArtifact sma;
   private final TaskComposite taskComposite;
   private final SMAWorkFlowTab workFlowTab;

   public SMAPrint(StateMachineArtifact sma, SMAWorkFlowTab workFlowTab, TaskComposite taskComposite) {
      super();
      this.sma = sma;
      this.workFlowTab = workFlowTab;
      this.taskComposite = taskComposite;
   }

   @Override
   public void run() {
      try {
         XResultData xResultData = getResultData();
         xResultData.report(sma.getName(), Manipulations.RAW_HTML);
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }

   }

   public XResultData getResultData() throws OseeCoreException {
      XResultData resultData = new XResultData();
      resultData.addRaw(AHTML.beginMultiColumnTable(100));
      resultData.addRaw(AHTML.addRowMultiColumnTable(new String[] {AHTML.getLabelValueStr(AHTML.LABEL_FONT, "Title: ",
            sma.getName())}));
      resultData.addRaw(AHTML.endMultiColumnTable());
      resultData.addRaw(AHTML.beginMultiColumnTable(100));
      resultData.addRaw(AHTML.addRowMultiColumnTable(new String[] {
      //
            AHTML.getLabelValueStr(AHTML.LABEL_FONT, "Current State: ",
                  ((IWorldViewArtifact) sma).getWorldViewState()),
            //
            AHTML.getLabelValueStr(AHTML.LABEL_FONT, "Team: ",
                  ((IWorldViewArtifact) sma).getWorldViewTeam()),
            //
            AHTML.getLabelValueStr(AHTML.LABEL_FONT, "Assignees: ",
                  ((IWorldViewArtifact) sma).getWorldViewActivePoc()),
            //
            AHTML.getLabelValueStr(AHTML.LABEL_FONT, "Originator: ",
                  ((IWorldViewArtifact) sma).getWorldViewOriginator()),
            //
            AHTML.getLabelValueStr(AHTML.LABEL_FONT, "Created: ", XDate.getDateStr(sma.getLog().getCreationDate(),
                  XDate.MMDDYYHHMM))

      }));
      resultData.addRaw(AHTML.endMultiColumnTable());
      resultData.addRaw(AHTML.beginMultiColumnTable(100));
      resultData.addRaw(AHTML.addRowMultiColumnTable(new String[] {
            //
            AHTML.getLabelValueStr(AHTML.LABEL_FONT, "Workflow: ", sma.getArtifactTypeName()),
            AHTML.getLabelValueStr(AHTML.LABEL_FONT, "HRID: ", sma.getHumanReadableId()),
            (sma.getParentActionArtifact() == null ? "" : AHTML.getLabelValueStr(AHTML.LABEL_FONT,
                  "Action HRID: ", sma.getParentActionArtifact().getHumanReadableId()))}));
      resultData.addRaw(AHTML.endMultiColumnTable());
      for (NoteItem note : sma.getNotes().getNoteItems()) {
         if (note.getState().equals("")) {
            resultData.addRaw(note.toHTML() + AHTML.newline());
         }
      }
      if (workFlowTab != null) resultData.addRaw(workFlowTab.getHtml());
      if (taskComposite != null) resultData.addRaw(taskComposite.toHTML(AHTML.LABEL_FONT));
      resultData.addRaw(AHTML.newline());
      resultData.addRaw(sma.getLog().getHtml());

      XResultData rd = new XResultData();
      rd.addRaw(AHTML.beginMultiColumnTable(100, 1));
      rd.addRaw(AHTML.addRowMultiColumnTable(new String[] {resultData.getReport("").getManipulatedHtml(
            Arrays.asList(Manipulations.NONE))}));
      rd.addRaw(AHTML.endMultiColumnTable());

      return rd;
   }

}
