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
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osee.ats.actions.wizard.NewNoteWizard;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.editor.SMAWorkFlowSection;
import org.eclipse.osee.ats.util.DefaultTeamState;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.skynet.ats.NoteType;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author Donald G. Dunne
 */
public class AddNoteOperation extends WorkPageService {

   private final SMAManager smaMgr;
   private Hyperlink link;

   public AddNoteOperation(SMAManager smaMgr, AtsWorkPage page, XFormToolkit toolkit, SMAWorkFlowSection section) {
      super("Add Note", smaMgr, page, toolkit, section, ServicesArea.OPERATION_CATEGORY, Location.Global);
      this.smaMgr = smaMgr;
   }

   @Override
   public void create(Group workComp) {
      link = toolkit.createHyperlink(workComp, name, SWT.NONE);
      if (smaMgr.getSma().isReadOnly())
         link.addHyperlinkListener(readOnlyHyperlinkListener);
      else
         link.addHyperlinkListener(new IHyperlinkListener() {

            public void linkEntered(HyperlinkEvent e) {
            }

            public void linkExited(HyperlinkEvent e) {
            }

            public void linkActivated(HyperlinkEvent e) {
               ArrayList<String> artifactNames = new ArrayList<String>();
               artifactNames.add(smaMgr.getSma().getDescriptiveName() + " - " + smaMgr.getSma().getDescriptiveName());
               for (WorkPage page : smaMgr.getWorkFlow().getPages())
                  if (!page.equals(DefaultTeamState.Cancelled.name()) && !page.equals(DefaultTeamState.Completed.name())) artifactNames.add(page.getName());
               NewNoteWizard noteWizard = new NewNoteWizard(artifactNames);
               WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                     noteWizard);
               dialog.create();
               if (dialog.open() == 0) {
                  String selected = noteWizard.mainPage.artifactList.getSelected().iterator().next().getName();
                  String state = "";
                  if (!selected.startsWith(smaMgr.getSma().getDescriptiveName() + " - ")) state = selected;
                  smaMgr.getSma().getNotes().addNote(
                        NoteType.getType(noteWizard.mainPage.typeList.getSelected().iterator().next().getName()),
                        state, noteWizard.mainPage.noteText.get(),
                        SkynetAuthentication.getInstance().getAuthenticatedUser());
                  smaMgr.getEditor().onDirtied();
               }
            }
         });
      refresh();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.editor.operation.WorkPageService#refresh()
    */
   @Override
   public void refresh() {
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.editor.service.WorkPageService#dispose()
    */
   @Override
   public void dispose() {
   }

}
