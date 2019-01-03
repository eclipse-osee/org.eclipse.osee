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
package org.eclipse.osee.ats.ide.actions.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osee.ats.api.workflow.note.NoteType;
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
public class NewNotePage extends WizardPage {
   public XList typeList;
   public XText noteText;
   public XList artifactList;
   private final NewNoteWizard wizard;

   public NewNotePage(NewNoteWizard wizard) {
      super("Create new Note", "Create new Note", null);
      this.wizard = wizard;
   }

   @Override
   public void createControl(Composite parent) {
      typeList = new XList("Type");
      typeList.setRequiredEntry(true);
      typeList.setGrabHorizontal(true);
      typeList.setRequiredSelected(1, 1);
      typeList.add(NoteType.getNames());
      typeList.setVerticalLabel(true);
      artifactList = new XList("Against State or Workflow");
      artifactList.setVerticalLabel(true);
      artifactList.setRequiredEntry(true);
      artifactList.setGrabHorizontal(true);
      artifactList.setRequiredSelected(1, 1);
      artifactList.add(wizard.getArtifactNames());
      noteText = new XText("Note");
      noteText.setRequiredEntry(true);
      noteText.setVerticalLabel(true);
      noteText.setFillHorizontally(true);
      noteText.setFillVertically(true);

      Composite composite = new Composite(parent, SWT.NONE);
      composite.setLayout(ALayout.getZeroMarginLayout(1, false));
      composite.setLayoutData(new GridData(GridData.FILL_BOTH));

      Composite topC = new Composite(composite, SWT.NONE);
      topC.setLayout(ALayout.getZeroMarginLayout(2, false));
      GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
      topC.setLayoutData(gridData);

      Composite topCLeft = new Composite(topC, SWT.NONE);
      topCLeft.setLayout(new GridLayout());
      gridData = new GridData(GridData.FILL_HORIZONTAL);
      topCLeft.setLayoutData(gridData);

      typeList.createWidgets(topCLeft, 2);
      typeList.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent event) {
            update();
         };
      });

      Composite topCRight = new Composite(topC, SWT.NONE);
      topCRight.setLayout(new GridLayout());
      gridData = new GridData(GridData.FILL_HORIZONTAL);
      topCRight.setLayoutData(gridData);

      artifactList.createWidgets(topCRight, 2);
      artifactList.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent event) {
            update();
         };
      });
      gridData = new GridData(GridData.FILL_HORIZONTAL);
      gridData.widthHint = 300;
      artifactList.getList().setLayoutData(gridData);

      Composite bottomC = new Composite(composite, SWT.NONE);
      bottomC.setLayout(new GridLayout());
      gridData = new GridData(GridData.FILL_BOTH);
      bottomC.setLayoutData(gridData);

      noteText.setFillHorizontally(true);
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
      if (artifactList.getSelected().isEmpty()) {
         return false;
      }
      return true;
   }

}