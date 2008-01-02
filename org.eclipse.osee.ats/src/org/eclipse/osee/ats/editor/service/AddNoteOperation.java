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

package org.eclipse.osee.ats.editor.service;

import java.util.ArrayList;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.actions.wizard.NewNoteWizard;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.util.DefaultTeamState;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.ui.skynet.ats.NoteType;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPage;
import org.eclipse.ui.PlatformUI;

/**
 * @author Donald G. Dunne
 */
public class AddNoteOperation extends WorkPageService {

   Action action;

   public AddNoteOperation(SMAManager smaMgr) {
      super(smaMgr);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.service.WorkPageService#createToolbarService()
    */
   @Override
   public Action createToolbarService() {
      action = new Action(getName(), Action.AS_PUSH_BUTTON) {
         public void run() {
            performAddNote();
         }
      };
      action.setToolTipText(getName());
      action.setImageDescriptor(AtsPlugin.getInstance().getImageDescriptor("newNote.gif"));
      return action;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.service.WorkPageService#getName()
    */
   @Override
   public String getName() {
      return "Add Note";
   }

   private void performAddNote() {
      try {
         ArrayList<String> artifactNames = new ArrayList<String>();
         artifactNames.add(smaMgr.getSma().getDescriptiveName() + " - " + smaMgr.getSma().getDescriptiveName());
         for (WorkPage page : smaMgr.getWorkFlow().getPages())
            if (!page.equals(DefaultTeamState.Cancelled.name()) && !page.equals(DefaultTeamState.Completed.name())) artifactNames.add(page.getName());
         NewNoteWizard noteWizard = new NewNoteWizard(artifactNames);
         WizardDialog dialog =
               new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), noteWizard);
         dialog.create();
         if (dialog.open() == 0) {
            String selected = noteWizard.mainPage.artifactList.getSelected().iterator().next().getName();
            String state = "";
            if (!selected.startsWith(smaMgr.getSma().getDescriptiveName() + " - ")) state = selected;
            smaMgr.getSma().getNotes().addNote(
                  NoteType.getType(noteWizard.mainPage.typeList.getSelected().iterator().next().getName()), state,
                  noteWizard.mainPage.noteText.get(), SkynetAuthentication.getInstance().getAuthenticatedUser());
            smaMgr.getEditor().onDirtied();
         }
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.service.WorkPageService#refresh()
    */
   @Override
   public void refresh() {
      if (action != null) action.setEnabled(smaMgr.getSma().isReadOnly());
   }
}
