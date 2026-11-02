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

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osee.ats.api.workflow.note.AtsStateNoteType;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXListWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXTextWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.listen.XModifiedListener;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Donald G. Dunne
 */
public class NewStateNotePage extends WizardPage {
   public XXListWidget typeList;
   public XXTextWidget noteText;
   public XXListWidget stateList;
   private final NewStateNoteWizard wizard;

   public NewStateNotePage(NewStateNoteWizard wizard) {
      super("Create New State Note", "Create New State Note", null);
      this.wizard = wizard;
   }

   @Override
   public void createControl(Composite parent) {
      Composite composite = new Composite(parent, SWT.NONE);
      composite.setLayout(ALayout.getZeroMarginLayout(1, false));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      Composite topC = new Composite(composite, SWT.NONE);
      topC.setLayout(ALayout.getZeroMarginLayout(2, false));
      topC.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      Composite topCLeft = new Composite(topC, SWT.NONE);
      topCLeft.setLayout(new GridLayout());
      GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
      gd.heightHint = 300;
      topCLeft.setLayoutData(gd);

      typeList = new XXListWidget("Type");
      typeList.setRequiredEntry(true);
      typeList.setSelectable(AtsStateNoteType.getNames());
      typeList.setVerticalLabel(true);
      typeList.createWidgets(topCLeft, 2);
      typeList.getSwtList().addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent event) {
            update();
         };
      });

      Composite topCRight = new Composite(topC, SWT.NONE);
      topCRight.setLayout(new GridLayout());
      topCRight.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      stateList = new XXListWidget("Against State");
      stateList.setVerticalLabel(true);
      stateList.setRequiredEntry(true);
      stateList.setSelectable(wizard.getStateNames());
      stateList.createWidgets(topCRight, 2);
      stateList.getSwtList().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      stateList.getSwtList().addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent event) {
            update();
         };
      });
      GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
      gridData.widthHint = 300;
      stateList.getSwtList().setLayoutData(gridData);

      Composite bottomC = new Composite(composite, SWT.NONE);
      bottomC.setLayout(new GridLayout());
      bottomC.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      noteText = new XXTextWidget("Note");
      noteText.setRequiredEntry(true);
      noteText.setVerticalLabel(true);
      noteText.setFillHorizontally(true);
      noteText.setFillVertically(true);
      noteText.createWidgets(bottomC, 2);
      noteText.addXModifiedListener(new XModifiedListener() {

         @Override
         public void widgetModified(XWidget widget) {
            update();
         };
      });
      GridData gridData2 = new GridData(GridData.FILL_BOTH);
      gridData2.heightHint = 200;
      noteText.getStyledText().setLayoutData(gridData2);

      setControl(composite);

      topCRight.layout(true, true);
      topCLeft.layout(true, true);
      topC.layout(true, true);
      composite.layout(true, true);
      composite.getParent().layout(true, true);
   }

   public void update() {
      getContainer().updateButtons();
   }

   @Override
   public boolean isPageComplete() {
      if (noteText.get().equals("")) {
         return false;
      }
      if (typeList.getSelected().isEmpty()) {
         return false;
      }
      if (stateList.getSelected().isEmpty()) {
         return false;
      }
      return true;
   }

}