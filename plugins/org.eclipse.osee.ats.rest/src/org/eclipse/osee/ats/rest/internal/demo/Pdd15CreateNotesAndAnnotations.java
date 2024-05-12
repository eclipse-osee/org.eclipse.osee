/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.ats.rest.internal.demo;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.INewActionListener;
import org.eclipse.osee.ats.api.workflow.note.AtsStateNoteType;
import org.eclipse.osee.ats.api.workflow.note.IAtsStateNoteService;
import org.eclipse.osee.ats.core.demo.DemoUtil;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.framework.core.data.ArtifactAnnotation;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public class Pdd15CreateNotesAndAnnotations extends AbstractPopulateDemoDatabase {

   public Pdd15CreateNotesAndAnnotations(XResultData rd, AtsApi atsApi) {
      super(rd, atsApi);
   }

   @Override
   public void run() {
      rd.logf("\n\nRunning [%s]...\n", getClass().getSimpleName());

      IAtsChangeSet changes = atsApi.createChangeSet(getClass().getSimpleName());

      Collection<IAtsActionableItem> aias = DemoUtil.getActionableItems(atsApi, DemoArtifactToken.SAW_Code_AI);
      Date createdDate = new Date();
      AtsUser createdBy = atsApi.getUserService().getCurrentUser();

      ActionResult actionResult =
         atsApi.getActionService().createAction(null, DemoArtifactToken.SAW_NotesAnnotations_Code_TeamWf.getName(),
            "Problem with the Diagram View", ChangeTypes.Problem, "1", false, null, aias, createdDate, createdBy,
            Arrays.asList(new ArtifactTokenActionListener()), changes);
      changes.execute();

      if (actionResult.getResults().isErrors()) {
         rd.errorf("Error creating Action in %s: %s", getClass().getSimpleName(), actionResult.getResults().toString());
         return;
      }

      IAtsTeamWorkflow teamWf = actionResult.getFirstTeam();

      changes = atsApi.createChangeSet(getClass().getSimpleName());
      // create state notes
      IAtsStateNoteService noteService = atsApi.getWorkItemService().getStateNoteService();
      noteService.addNote(teamWf, AtsStateNoteType.Info, TeamState.Analyze.getName(), "This is my informational note.",
         changes);
      noteService.addNote(teamWf, AtsStateNoteType.Problem, TeamState.Implement.getName(), "This is my problem note.",
         changes);
      noteService.addNote(teamWf, AtsStateNoteType.Warning, TeamState.Implement.getName(), "This is my warning note.",
         changes);
      noteService.addNote(teamWf, AtsStateNoteType.Other, TeamState.Analyze.getName(), "Other note.", changes);

      // create workflow notes
      changes.addAttribute(teamWf, AtsAttributeTypes.WorkflowNotes, "Now is the time\n\nfor all good men.");
      changes.execute();

      // create annotations
      changes = atsApi.createChangeSet("Create Annotations");
      changes.addAnnotation(teamWf.getStoreObject(),
         ArtifactAnnotation.getError("my.annotation", "This is error annotation"));
      changes.addAnnotation(teamWf.getStoreObject(),
         ArtifactAnnotation.getWarning("my.annotation", "This is warning annotation"));
      changes.addAnnotation(teamWf.getStoreObject(),
         ArtifactAnnotation.getInfo("my.annotation", "This is info annotation"));

      changes.execute();
   }

   private class ArtifactTokenActionListener implements INewActionListener {
      @Override
      public ArtifactToken getArtifactToken(List<IAtsActionableItem> applicableAis) {
         return DemoArtifactToken.SAW_NotesAnnotations_Code_TeamWf;
      }
   }

}
