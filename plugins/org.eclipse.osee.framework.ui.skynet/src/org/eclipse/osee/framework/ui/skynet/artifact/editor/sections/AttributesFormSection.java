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

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.IActionContributor;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.parts.AttributeFormPart;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Roberto E. Escobar
 */
public class AttributesFormSection extends ArtifactEditorFormSection {

   private IActionContributor actionContributor;
   private AttributeFormPart formPart;
   private IToolBarManager toolBarManager;

   public AttributesFormSection(ArtifactEditor editor, Composite parent, FormToolkit toolkit, int style) {
      super(editor, parent, toolkit, style);
   }

   @Override
   public void initialize(IManagedForm form) {
      super.initialize(form);
      Section section = getSection();
      section.setText("Attributes");
      section.setLayout(new GridLayout(1, false));
      section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

      addToolBar(form);
      updateDataPart();
      updateToolBarVisibility();
   }

   private IToolBarManager getToolBarManager() {
      if (toolBarManager == null) {
         toolBarManager = new ToolBarManager(SWT.FLAT);
      }
      return toolBarManager;
   }

   private void addToolBar(IManagedForm form) {
      final FormToolkit toolkit = form.getToolkit();
      Composite composite = toolkit.createComposite(getSection());
      composite.setLayout(ALayout.getZeroMarginLayout());
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      composite.setBackground(getSection().getBackground());

      IToolBarManager manager = getToolBarManager();
      ((ToolBarManager) manager).createControl(composite);
      getActionContributor().contributeToToolBar(manager);
      manager.update(true);

      getSection().setTextClient(composite);
   }

   private IActionContributor getActionContributor() {
      if (actionContributor == null) {
         actionContributor = new AttributeActionContribution((ArtifactEditor) getEditor());
      }
      return actionContributor;
   }

   private void updateDataPart() {
      final IManagedForm form = getManagedForm();
      final FormToolkit toolkit = form.getToolkit();
      final Section section = getSection();

      if (formPart != null) {
         form.removePart(formPart);
         formPart.dispose();
         Control control = section.getClient();
         if (control != null && !control.isDisposed()) {
            control.dispose();
         }
      }
      Composite sectionBody = toolkit.createComposite(section, toolkit.getBorderStyle());
      sectionBody.setLayout(ALayout.getZeroMarginLayout());
      sectionBody.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      formPart = new AttributeFormPart((ArtifactEditor) getEditor());
      form.addPart(formPart);
      formPart.createContents(sectionBody);
      section.setClient(sectionBody);
      toolkit.paintBordersFor(section);

      section.layout(true);
      form.getForm().getBody().layout(true);
   }

   @Override
   public void refresh() {
      super.refresh();
      updateDataPart();
      updateToolBarVisibility();
   }

   private void updateToolBarVisibility() {
      boolean isReadOnly = !getEditorInput().isReadOnly();
      getSection().getTextClient().setVisible(isReadOnly);
      for (IContributionItem item : getToolBarManager().getItems()) {
         item.setVisible(isReadOnly);
      }
      getToolBarManager().update(true);
   }

   @Override
   public void dispose() {
      formPart.dispose();
      super.dispose();
   }
}
