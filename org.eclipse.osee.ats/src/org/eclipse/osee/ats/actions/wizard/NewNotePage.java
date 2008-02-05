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
package org.eclipse.osee.ats.actions.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osee.framework.ui.skynet.ats.NoteType;
import org.eclipse.osee.framework.ui.skynet.widgets.XList;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionListener;
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

   /**
    * @param wizard -
    */
   public NewNotePage(NewNoteWizard wizard) {
      super("Create new Note", "Create new Note", null);
      this.wizard = wizard;
   }

   public void createControl(Composite parent) {
      typeList = new XList("Type");
      typeList.setRequiredEntry(true);
      typeList.setGrabHorizontal(true);
      typeList.setRequiredSelected(1, 1);
      typeList.add(NoteType.getNames());
      typeList.setVerticalLabel(true);
      artifactList = new XList("Against Artifact");
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
      GridData gd = new GridData(GridData.FILL_HORIZONTAL);
      topC.setLayoutData(gd);

      Composite topCLeft = new Composite(topC, SWT.NONE);
      topCLeft.setLayout(new GridLayout());
      gd = new GridData(GridData.FILL_HORIZONTAL);
      topCLeft.setLayoutData(gd);

      typeList.createWidgets(topCLeft, 2);
      typeList.addSelectionListener(new SelectionListener() {
         public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
         };

         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
            update();
         };
      });

      Composite topCRight = new Composite(topC, SWT.NONE);
      topCRight.setLayout(new GridLayout());
      gd = new GridData(GridData.FILL_HORIZONTAL);
      topCRight.setLayoutData(gd);

      artifactList.createWidgets(topCRight, 2);
      artifactList.addSelectionListener(new SelectionListener() {
         public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
         };

         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
            update();
         };
      });

      Composite bottomC = new Composite(composite, SWT.NONE);
      bottomC.setLayout(new GridLayout());
      gd = new GridData(GridData.FILL_BOTH);
      bottomC.setLayoutData(gd);

      noteText.createWidgets(bottomC, 2);
      noteText.addModifyListener(new ModifyListener() {
         public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
            update();
         };
      });

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
      if (typeList.getSelected().size() == 0) {
         return false;
      }
      if (artifactList.getSelected().size() == 0) {
         return false;
      }
      return true;
   }

}