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

package org.eclipse.osee.ats.ide.editor.tab.workflow.header;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workflow.note.AtsStateNote;
import org.eclipse.osee.ats.api.workflow.note.AtsStateNoteType;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkLabelValueSelection;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Donald G. Dunne
 */
public class WfeStateNotesHeader extends Composite {

   private final IAtsWorkItem workItem;
   private final WorkflowEditor editor;
   private final List<Composite> nComps = new ArrayList<>();
   private final String forStateName;

   public WfeStateNotesHeader(Composite parent, int style, final IAtsWorkItem workItem, String forStateName, final WorkflowEditor editor) {
      super(parent, style);
      this.workItem = workItem;
      this.forStateName = forStateName;
      this.editor = editor;
      setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
      setLayout(ALayout.getZeroMarginLayout(1, false));
      editor.getToolkit().adapt(this);

      setBackground(Displays.getSystemColor(SWT.COLOR_WHITE));

      createNotes(workItem, forStateName, editor);
   }

   private void createNotes(final IAtsWorkItem workItem, String forStateName, final WorkflowEditor editor) {
      try {
         // Display State Notes
         List<AtsStateNote> notes = AtsApiService.get().getWorkItemService().getStateNoteService().getNotes(workItem);
         for (AtsStateNote note : notes) {
            if (forStateName == null || (note.getState() != null && note.getState().equals(forStateName))) {

               Composite nComp = new Composite(this, SWT.NONE);
               nComp.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
               nComp.setLayout(ALayout.getZeroMarginLayout(2, false));
               editor.getToolkit().adapt(nComp);
               nComps.add(nComp);

               Label iconLabel = editor.getToolkit().createLabel(nComp, "");
               if (AtsStateNoteType.Problem.name().equals(note.getType())) {
                  iconLabel.setImage(ImageManager.getImage(FrameworkImage.ERROR));
               } else if (AtsStateNoteType.Warning.name().equals(note.getType())) {
                  iconLabel.setImage(ImageManager.getImage(FrameworkImage.WARNING));
               } else {
                  iconLabel.setImage(ImageManager.getImage(FrameworkImage.INFO_LG));
               }

               String hyperlinkStr =
                  String.format("%sNote", (Strings.isValid(note.getState()) ? note.getState() + " " : ""));
               String labelStr = String.format("%s: %s - %s - [%s]", note.getType(), note.getUserName(),
                  DateUtil.getMMDDYYHHMM(note.getDateObj()), note.getMsg());
               String shortStr = Strings.truncate(labelStr, 150).replaceAll("[\n\r]+", " ");
               XHyperlinkLabelValueSelection wid = new XHyperlinkLabelValueSelection(hyperlinkStr) {

                  @Override
                  public String getCurrentValue() {
                     return shortStr;
                  }

                  @Override
                  public boolean handleSelection() {
                     EntryDialog ed = new EntryDialog(Displays.getActiveShell(), "View/Update State Note", null,
                        "Enter State Notes", MessageDialog.QUESTION, new String[] {"Remove", "Update", "Cancel"}, 2);
                     ed.setFillVertically(true);
                     ed.setEntry(note.getMsg());
                     int result = ed.open();
                     if (result == 0) {
                        return AtsApiService.get().getWorkItemService().getStateNoteService().removeNote(workItem,
                           note);
                     } else if (result == 1) {
                        return AtsApiService.get().getWorkItemService().getStateNoteService().updateNote(workItem, note,
                           ed.getEntry());
                     }
                     return false;
                  }

               };
               wid.setEditable(true);
               wid.setToolTip("Select to View/Modify/Delete");
               wid.createWidgets(editor.getWorkFlowTab().getManagedForm(), nComp, 1);
            }
         }
      } catch (OseeCoreException ex) {
         Label errorLabel = editor.getToolkit().createLabel(this, "Error: " + ex.getLocalizedMessage());
         errorLabel.setForeground(Displays.getSystemColor(SWT.COLOR_RED));
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   public void refresh() {
      for (Composite comp : nComps) {
         if (Widgets.isAccessible(comp)) {
            comp.dispose();
         }
      }
      createNotes(workItem, forStateName, editor);
   }

}
