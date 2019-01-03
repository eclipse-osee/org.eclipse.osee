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

package org.eclipse.osee.ats.ide.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workflow.note.NoteType;
import org.eclipse.osee.ats.ide.AtsImage;
import org.eclipse.osee.ats.ide.actions.wizard.NewNoteWizard;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.ui.PlatformUI;

/**
 * @author Donald G. Dunne
 */
public class AddNoteAction extends AbstractAtsAction {

   private final AbstractWorkflowArtifact sma;
   private final IDirtiableEditor dirtiable;
   private boolean emulate = false;
   private String selectedState;
   private NoteType noteType;
   private String noteText;

   public AddNoteAction(AbstractWorkflowArtifact sma, IDirtiableEditor dirtiable) {
      super();
      this.sma = sma;
      this.dirtiable = dirtiable;
      setText("Add Note");
      setToolTipText(getText());
   }

   @Override
   public void runWithException() {
      ArrayList<String> artifactNames = new ArrayList<>();
      Map<String, String> selectedToStateName = new HashMap<>();
      artifactNames.add("Whole \"" + sma.getArtifactTypeName() + "\"");
      for (IAtsStateDefinition stateDefinition : AtsClientService.get().getWorkDefinitionService().getStatesOrderedByOrdinal(
         sma.getWorkDefinition())) {
         String displayName = "\"" + stateDefinition.getName() + "\" State";
         artifactNames.add(displayName);
         selectedToStateName.put(displayName, stateDefinition.getName());
      }

      if (emulate) {
         boolean result = performEmulate();
         if (!result) {
            return;
         }
      } else {
         boolean result = performUi(artifactNames);
         if (!result) {
            return;
         }
      }
      String state = "";
      if (!selectedState.startsWith(sma.getName() + " - ")) {
         state = selectedToStateName.get(selectedState);
      }
      AtsClientService.get().getWorkItemService().getNotes(sma).addNote(noteType, state, noteText,
         AtsClientService.get().getUserService().getCurrentUser());
      dirtiable.onDirtied();
   }

   private boolean performEmulate() {
      this.selectedState = "Endorse";
      this.noteType = NoteType.Comment;
      this.noteText = "this is the comment";
      return true;
   }

   private boolean performUi(ArrayList<String> artifactNames) {
      NewNoteWizard noteWizard = new NewNoteWizard(artifactNames);
      WizardDialog dialog =
         new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), noteWizard);
      dialog.create();
      if (dialog.open() == 0) {
         selectedState = noteWizard.mainPage.artifactList.getSelected().iterator().next().getName();
         noteType = NoteType.getType(noteWizard.mainPage.typeList.getSelected().iterator().next().getName());
         noteText = noteWizard.mainPage.noteText.get();
         return true;
      }
      return false;
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(AtsImage.NEW_NOTE);
   }

   public void setEmulateUi(boolean emulate) {
      this.emulate = emulate;
   }

}
