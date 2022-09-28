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

package org.eclipse.osee.ats.ide.integration.tests.ats.demo;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
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
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.ide.demo.DemoUtil;
import org.eclipse.osee.ats.ide.demo.config.DemoDbUtil;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.util.AtsApiIde;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.artifact.annotation.ArtifactAnnotation;
import org.eclipse.osee.framework.ui.skynet.artifact.annotation.ArtifactAnnotationManager;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class Pdd15CreateNotesAndAnnotationsTest implements IPopulateDemoDatabaseTest {

   @Test
   public void testAction() {
      DemoUtil.checkDbInitAndPopulateSuccess();
      DemoUtil.setPopulateDbSuccessful(false);

      AtsApiIde atsApi = AtsApiService.get();
      IAtsChangeSet changes = atsApi.createChangeSet(getClass().getSimpleName());

      Collection<IAtsActionableItem> aias = DemoDbUtil.getActionableItems(DemoArtifactToken.SAW_Code_AI);
      Date createdDate = new Date();
      AtsUser createdBy = atsApi.getUserService().getCurrentUser();

      ActionResult actionResult =
         atsApi.getActionService().createAction(null, DemoArtifactToken.SAW_NotesAnnotations_Code_TeamWf.getName(),
            "Problem with the Diagram View", ChangeTypes.Problem, "1", false, null, aias, createdDate, createdBy,
            Arrays.asList(new ArtifactTokenActionListener()), changes);
      changes.execute();

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
      Artifact teamWfArt = (Artifact) teamWf;
      ArtifactAnnotationManager.addAnnotation(teamWfArt,
         ArtifactAnnotation.getError("my.annotation", "This is error annotation"));
      ArtifactAnnotationManager.addAnnotation(teamWfArt,
         ArtifactAnnotation.getWarning("my.annotation", "This is warning annotation"));
      ArtifactAnnotationManager.addAnnotation(teamWfArt,
         ArtifactAnnotation.getInfo("my.annotation", "This is info annotation"));
      ((Artifact) teamWf.getStoreObject()).persist("Add Annotations");

      DemoUtil.setPopulateDbSuccessful(true);
   }

   private class ArtifactTokenActionListener implements INewActionListener {
      @Override
      public ArtifactToken getArtifactToken(List<IAtsActionableItem> applicableAis) {
         return DemoArtifactToken.SAW_NotesAnnotations_Code_TeamWf;
      }
   }

}
