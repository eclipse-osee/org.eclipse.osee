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

package org.eclipse.osee.ats.ide.editor.tab.workflow.note;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.ide.actions.AbstractAtsAction;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.AttributeTypeString;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class AddWorkflowNotesAction extends AbstractAtsAction {

   private static final AttributeTypeString notesAttrType = AtsAttributeTypes.WorkflowNotes;
   private final IAtsWorkItem workItem;
   private boolean emulate = false;

   public AddWorkflowNotesAction(IAtsWorkItem workItem) {
      super();
      this.workItem = workItem;
      setText("Add/Update Workflow Notes");
      setToolTipText(getText());
   }

   @Override
   public void runWithException() {
      String noteText = "";
      if (emulate) {
         noteText = performEmulate();
      } else {
         noteText = performUi();
      }
      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getText());
      if (Strings.isValid(noteText)) {
         changes.setSoleAttributeValue(workItem, notesAttrType, noteText);
      } else {
         changes.deleteAttributes(workItem, notesAttrType);
      }
      changes.execute();
   }

   private String getNoteText() {
      return AtsApiService.get().getAttributeResolver().getSoleAttributeValue(workItem, notesAttrType, "");
   }

   private String performEmulate() {
      return "this is the note";
   }

   private String performUi() {
      EntryDialog ed = new EntryDialog("Add/Update Workflow Notes", "Enter Workflow Notes");
      ed.setFillVertically(true);
      ed.setEntry(getNoteText());
      String noteTxt = "";
      if (ed.open() == Window.OK) {
         noteTxt = ed.getEntry();
      }
      return noteTxt;
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(AtsImage.NOTE);
   }

   public void setEmulateUi(boolean emulate) {
      this.emulate = emulate;
   }
}