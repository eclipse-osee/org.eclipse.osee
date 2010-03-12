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
package org.eclipse.osee.framework.ui.skynet.blam.sections;

import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Roberto E. Escobar
 */
public class BlamUsageSection extends BaseBlamSection {

   private FormText formText;
   private IManagedForm form;

   public BlamUsageSection(FormEditor editor, AbstractBlam abstractBlam, Composite parent, FormToolkit toolkit, int style) {
      super(editor, abstractBlam, parent, toolkit, style);
   }

   @Override
   public void initialize(IManagedForm form) {
      this.form = form;
      super.initialize(form);
      Section section = getSection();
      section.setText("Description and Usage");
      section.setLayout(new GridLayout());
      section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      XFormToolkit.addHelpLinkToSection(form.getToolkit(), section, "/org.eclipse.pde.doc.user/guide/pde_running.htm");
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

         getSection().layout(true);
         form.getForm().getBody().layout(true);

      }

      if (Widgets.isAccessible(formText)) {
         try {
            String data = getAbstractBlam().getDescriptionUsage();
            boolean parseTags = false;
            if (data.startsWith("<form>")) {
               parseTags = true;
            }
            formText.setText(data, parseTags, parseTags);
         } catch (Exception ex) {
            formText.setText(Lib.exceptionToString(ex), false, false);
         }
         getManagedForm().reflow(true);
      }

   }

   @Override
   public void dispose() {
      if (Widgets.isAccessible(formText)) {
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
