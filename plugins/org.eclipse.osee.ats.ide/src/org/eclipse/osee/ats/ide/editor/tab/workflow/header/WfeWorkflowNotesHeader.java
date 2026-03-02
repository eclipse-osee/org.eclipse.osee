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

import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.XHyperlinkWorkflowNotesWidget;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
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
public class WfeWorkflowNotesHeader extends Composite {

   private final IAtsWorkItem workItem;
   private final WorkflowEditor editor;
   private Composite nComp;
   private final String forStateName;

   public WfeWorkflowNotesHeader(Composite parent, int style, final IAtsWorkItem workItem, String forStateName, final WorkflowEditor editor) {
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

         String wfNote = AtsApiService.get().getAttributeResolver().getSoleAttributeValue(workItem,
            AtsAttributeTypes.WorkflowNotes, "");

         if (Strings.isValid(wfNote)) {
            nComp = new Composite(this, SWT.NONE);
            nComp.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
            nComp.setLayout(ALayout.getZeroMarginLayout(2, false));
            editor.getToolkit().adapt(nComp);

            Label iconLabel = editor.getToolkit().createLabel(nComp, "");
            iconLabel.setImage(ImageManager.getImage(AtsImage.NOTE));

            String hyperlinkStr = "Workflow Notes";
            String labelStr = wfNote;
            String shortStr = Strings.truncate(labelStr, 150, true).replaceAll("[\n\r]+", " ");
            XHyperlinkWorkflowNotesWidget wid = new XHyperlinkWorkflowNotesWidget(hyperlinkStr, shortStr, workItem);
            wid.setEditable(true);
            wid.setToolTip("Select to View/Modify/Delete");
            wid.createWidgets(editor.getWorkFlowTab().getManagedForm(), nComp, 1);
         }
      } catch (OseeCoreException ex) {
         Label errorLabel = editor.getToolkit().createLabel(this, "Error: " + ex.getLocalizedMessage());
         errorLabel.setForeground(Displays.getSystemColor(SWT.COLOR_RED));
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   public void refresh() {
      if (Widgets.isAccessible(nComp)) {
         nComp.dispose();
      }
      createNotes(workItem, forStateName, editor);
   }

}
