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

package org.eclipse.osee.framework.ui.skynet.artifact.editor.tab.art;

import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.header.ArtEdBaseTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * Artifact Editor - Attributes tab. Uses the common ArtEdBaseTab header and hosts AttributeFormPart for attribute
 * widgets.
 *
 * @author Donald G. Dunne
 */
public class ArtAttrTab extends ArtEdBaseTab {

   private AttributeFormPart formPart;

   public ArtAttrTab(ArtifactEditor editor, String id, String title) {
      super(editor, id, title);
   }

   @Override
   protected String getTabName() {
      return "Attributes";
   }

   @Override
   protected void createSectionToolbar(Composite toolbarParent) {
      ToolBarManager sectionToolBar = new ToolBarManager(SWT.FLAT);
      sectionToolBar.createControl(toolbarParent);
      sectionToolBar.getControl().setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
      sectionToolBar.getControl().setBackground(
         toolbarParent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
      AttributeActionContribution actionContrib = new AttributeActionContribution(this);
      actionContrib.contributeToToolBar(sectionToolBar);
      sectionToolBar.update(true);
   }

   @Override
   protected void createTabBody(Composite parent, FormToolkit toolkit) {
      IManagedForm managedForm = getManagedForm();
      formPart = new AttributeFormPart(getEditor());
      managedForm.addPart(formPart);
      formPart.createContents(parent);
      formPart.computeTextSizesAndReflow();
   }

   @Override
   protected void refreshTabBody() {
      if (formPart != null) {
         formPart.refresh();
      }
   }

   @Override
   public void dispose() {
      if (formPart != null) {
         formPart.dispose();
      }
      super.dispose();
   }

   public AttributeFormPart getFormPart() {
      return formPart;
   }
}
