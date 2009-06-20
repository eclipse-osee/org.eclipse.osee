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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.osee.framework.ui.skynet.blam.BlamEditor;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Roberto E. Escobar
 */
public class BlamOutputSection extends BaseBlamSection {

   private Text formText;

   /**
    * @param editor
    * @param parent
    * @param toolkit
    * @param style
    */
   public BlamOutputSection(BlamEditor editor, Composite parent, FormToolkit toolkit, int style) {
      super(editor, parent, toolkit, style);
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.forms.AbstractFormPart#initialize(org.eclipse.ui.forms.IManagedForm)
    */
   @Override
   public void initialize(IManagedForm form) {
      super.initialize(form);
      Section section = getSection();
      section.setText("Execute");
      section.setLayout(new GridLayout());
      section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      final FormToolkit toolkit = getManagedForm().getToolkit();
      Composite composite = toolkit.createComposite(getSection(), toolkit.getBorderStyle() | SWT.WRAP);
      composite.setLayout(new GridLayout());
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      final Action action = getEditor().getActionBarContributor().getExecuteBlamAction();
      ActionContributionItem contributionItem = new ActionContributionItem(action);
      contributionItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);
      contributionItem.fill(composite);

      formText = toolkit.createText(composite, "BLAM has not yet run\n", SWT.WRAP);
      GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
      gd.heightHint = 500;
      gd.widthHint = 200;
      formText.setLayoutData(gd);

      getSection().setClient(composite);
      toolkit.paintBordersFor(composite);
   }

   public void appendText(String text) {
      if (Widgets.isAccessible(formText)) {
         formText.append(text);
         getManagedForm().reflow(true);
      }
   }

   public void setText(String text) {
      if (Widgets.isAccessible(formText)) {
         formText.setText(text);
         getManagedForm().reflow(true);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.forms.AbstractFormPart#dispose()
    */
   @Override
   public void dispose() {
      if (Widgets.isAccessible(formText)) {
         formText.dispose();
      }
      super.dispose();
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.forms.AbstractFormPart#refresh()
    */
   @Override
   public void refresh() {
      super.refresh();
   }
}
