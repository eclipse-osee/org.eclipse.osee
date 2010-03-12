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
package org.eclipse.osee.framework.ui.skynet.artifact.editor.sections;

import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Roberto E. Escobar
 */
public class DetailsFormSection extends ArtifactEditorFormSection {

   private FormText formText;

   public DetailsFormSection(ArtifactEditor editor, Composite parent, FormToolkit toolkit, int style) {
      super(editor, parent, toolkit, style);
   }

   @Override
   public void initialize(IManagedForm form) {
      super.initialize(form);
      Section section = getSection();
      section.setText("Details");
      section.setLayout(new GridLayout());
      section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      updateText(true);
   }

   private void updateText(boolean isCreate) {
      if (isCreate) {
         final FormToolkit toolkit = getManagedForm().getToolkit();
         Composite composite = toolkit.createComposite(getSection(), toolkit.getBorderStyle() | SWT.WRAP);
         composite.setLayout(new GridLayout());
         composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

         formText = toolkit.createFormText(composite, false);
         GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
         gd.widthHint = 200;
         formText.setLayoutData(gd);

         getSection().setClient(composite);
         toolkit.paintBordersFor(composite);
      }

      if (Widgets.isAccessible(formText)) {
         try {
            formText.setText(Artifacts.getDetailsFormText(getEditorInput().getArtifact()), true, true);
         } catch (Exception ex) {
            formText.setText(Lib.exceptionToString(ex), false, false);
         }
         getManagedForm().reflow(true);
      }
   }

   @Override
   public void dispose() {
      if (formText != null && !formText.isDisposed()) {
         formText.dispose();
      }
      super.dispose();
   }

   @Override
   public void refresh() {
      super.refresh();
      updateText(false);
   }

}
