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
import org.eclipse.osee.framework.ui.skynet.widgets.XList;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Donald G. Dunne
 */
public class NewStateNotePage extends WizardPage {
   public XList typeList;
   public XText noteText;
   public XList stateList;
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

      typeList = new XList("Type");
      typeList.setRequiredEntry(true);
      typeList.setGrabHorizontal(true);
      typeList.setRequiredSelected(1, 1);
      typeList.add(AtsStateNoteType.getNames());
      typeList.setVerticalLabel(true);
      typeList.createWidgets(topCLeft, 2);
      typeList.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent event) {
            update();
         };
      });

      Composite topCRight = new Composite(topC, SWT.NONE);
      topCRight.setLayout(new GridLayout());
      topCRight.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      stateList = new XList("Against State");
      stateList.setVerticalLabel(true);
      stateList.setRequiredEntry(true);
      stateList.setGrabHorizontal(true);
      stateList.setRequiredSelected(1, 1);
      stateList.add(wizard.getStateNames());
      stateList.createWidgets(topCRight, 2);
      stateList.getList().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      stateList.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent event) {
            update();
         };
      });
      GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
      gridData.widthHint = 300;
      stateList.getList().setLayoutData(gridData);

      Composite bottomC = new Composite(composite, SWT.NONE);
      bottomC.setLayout(new GridLayout());
      bottomC.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      noteText = new XText("Note");
      noteText.setRequiredEntry(true);
      noteText.setVerticalLabel(true);
      noteText.setFillHorizontally(true);
      noteText.setFillVertically(true);
      noteText.createWidgets(bottomC, 2);
      noteText.addModifyListener(new ModifyListener() {
         @Override
         public void modifyText(org.eclipse.swt.events.ModifyEvent event) {
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