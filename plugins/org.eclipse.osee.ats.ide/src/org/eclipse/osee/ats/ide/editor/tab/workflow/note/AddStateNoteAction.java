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

package org.eclipse.osee.ats.ide.editor.tab.workflow.note;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workflow.note.AtsStateNoteType;
import org.eclipse.osee.ats.ide.actions.AbstractAtsAction;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.ui.PlatformUI;

/**
 * @author Donald G. Dunne
 */
public class AddStateNoteAction extends AbstractAtsAction {

   private final AbstractWorkflowArtifact workItem;
   private boolean emulate = false;
   private String selectedState;
   private AtsStateNoteType noteType;
   private String noteText;

   public AddStateNoteAction(AbstractWorkflowArtifact workItem) {
      super();
      this.workItem = workItem;
      setText("Add State Note");
      setToolTipText(getText());
   }

   @Override
   public void runWithException() {
      ArrayList<String> stateNames = new ArrayList<>();
      Map<String, String> selectedToStateName = new HashMap<>();
      for (StateDefinition stateDefinition : AtsApiService.get().getWorkDefinitionService().getStatesOrderedByOrdinal(
         workItem.getWorkDefinition())) {
         String displayName = "\"" + stateDefinition.getName() + "\" State";
         stateNames.add(displayName);
         selectedToStateName.put(displayName, stateDefinition.getName());
      }

      if (emulate) {
         boolean result = performEmulate();
         if (!result) {
            return;
         }
      } else {
         boolean result = performUi(stateNames);
         if (!result) {
            return;
         }
      }
      String state = "";
      if (!selectedState.startsWith(workItem.getName() + " - ")) {
         state = selectedToStateName.get(selectedState);
      }
      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getText());
      AtsApiService.get().getWorkItemService().getStateNoteService().addNote(workItem, noteType, state, noteText,
         changes);
      changes.execute();
   }

   private boolean performEmulate() {
      this.selectedState = "Endorse";
      this.noteType = AtsStateNoteType.Info;
      this.noteText = "this is the comment";
      return true;
   }

   private boolean performUi(ArrayList<String> stateNames) {
      NewStateNoteWizard noteWizard = new NewStateNoteWizard(stateNames);
      WizardDialog dialog =
         new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), noteWizard);
      dialog.create();
      if (dialog.open() == Window.OK) {
         selectedState = noteWizard.mainPage.stateList.getSelected().iterator().next().getName();
         noteType = AtsStateNoteType.valueOf(noteWizard.mainPage.typeList.getSelected().iterator().next().getName());
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