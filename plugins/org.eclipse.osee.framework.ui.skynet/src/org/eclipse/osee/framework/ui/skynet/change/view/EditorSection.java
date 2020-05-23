/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.framework.ui.skynet.change.view;

import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

public class EditorSection extends SectionPart {

   public static interface IWidget {
      public void onUpdate();

      public void onCreate(IManagedForm form, Composite parent);

      public void onLoading();
   }

   private final IWidget widget;
   private final String sectionTitle;
   private final boolean spanVertically;

   public EditorSection(IWidget widget, String sectionTitle, Composite parent, FormToolkit toolkit, int style, boolean spanVertically) {
      super(parent, toolkit, style);
      this.widget = widget;
      this.sectionTitle = sectionTitle;
      this.spanVertically = spanVertically;
   }

   @Override
   public void initialize(IManagedForm form) {
      super.initialize(form);
      Section section = getSection();
      section.setText(sectionTitle);
      section.setLayout(new GridLayout());
      section.setLayoutData(new GridData(SWT.FILL, spanVertically ? SWT.FILL : SWT.TOP, true, spanVertically));
      updateDataPart(true);
   }

   private void updateDataPart(boolean isCreate) {
      final IManagedForm form = getManagedForm();
      if (isCreate) {
         final FormToolkit toolkit = form.getToolkit();
         final Section section = getSection();

         Control control = section.getClient();
         if (Widgets.isAccessible(control)) {
            control.dispose();
         }
         Composite sectionBody = toolkit.createComposite(section, toolkit.getBorderStyle());
         sectionBody.setLayout(new GridLayout());
         sectionBody.setLayoutData(new GridData(SWT.FILL, spanVertically ? SWT.FILL : SWT.TOP, true, spanVertically));

         widget.onCreate(getManagedForm(), sectionBody);

         section.setClient(sectionBody);
         toolkit.paintBordersFor(section);

         section.layout(true);
      } else {
         widget.onUpdate();
      }
      form.getForm().getBody().layout(true);
   }

   @Override
   public void dispose() {
      super.dispose();
   }

   @Override
   public void refresh() {
      super.refresh();
      updateDataPart(false);
   }

}
