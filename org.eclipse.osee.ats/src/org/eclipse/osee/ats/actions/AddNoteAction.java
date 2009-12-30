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

package org.eclipse.osee.ats.actions;

import java.util.ArrayList;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.actions.wizard.NewNoteWizard;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.ats.NoteType;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;
import org.eclipse.ui.PlatformUI;

/**
 * @author Donald G. Dunne
 */
public class AddNoteAction extends Action {

   Action action;
   private final StateMachineArtifact sma;

   public AddNoteAction(StateMachineArtifact sma) {
      this.sma = sma;
      setText("Add Note");
      setToolTipText(getText());
   }

   @Override
   public void run() {
      performAddNote();
   }

   private void performAddNote() {
      try {
         ArrayList<String> artifactNames = new ArrayList<String>();
         artifactNames.add("Whole \"" + sma.getArtifactTypeName() + "\"");
         for (WorkPageDefinition workPageDefinition : sma.getWorkFlowDefinition().getPagesOrdered())
            artifactNames.add("\"" + workPageDefinition.getPageName() + "\" State");
         NewNoteWizard noteWizard = new NewNoteWizard(artifactNames);
         WizardDialog dialog =
               new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), noteWizard);
         dialog.create();
         if (dialog.open() == 0) {
            String selected = noteWizard.mainPage.artifactList.getSelected().iterator().next().getName();
            String state = "";
            if (!selected.startsWith(sma.getName() + " - ")) state = selected;
            sma.getNotes().addNote(
                  NoteType.getType(noteWizard.mainPage.typeList.getSelected().iterator().next().getName()), state,
                  noteWizard.mainPage.noteText.get(), UserManager.getUser());
            sma.getEditor().onDirtied();
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(AtsImage.NEW_NOTE);
   }

}
