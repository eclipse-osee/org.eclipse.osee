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
package org.eclipse.osee.framework.ui.skynet.artifact.editor.panels;

import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Roberto E. Escobar
 */
public class AttributesFormSection extends ArtifactEditorFormSection {

   public AttributesFormSection(ArtifactEditor editor, Composite parent, FormToolkit toolkit, int style) {
      super(editor, parent, toolkit, style);
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.forms.AbstractFormPart#initialize(org.eclipse.ui.forms.IManagedForm)
    */
   @Override
   public void initialize(final IManagedForm form) {
      super.initialize(form);
      final FormToolkit toolkit = form.getToolkit();

      Section section = getSection();
      section.setText("Attributes");

      section.setLayout(new GridLayout(1, false));
      section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      AttributePropertiesBlock propertiesBlock = new AttributePropertiesBlock(getEditor());
      Composite sectionBody = propertiesBlock.createContent(form, section);

      section.setClient(sectionBody);
      toolkit.paintBordersFor(section);
   }

}
