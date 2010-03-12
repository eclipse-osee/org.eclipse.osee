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
package org.eclipse.osee.framework.ui.skynet.blam;

import java.util.Collection;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.osee.framework.ui.skynet.IHelpContextIds;
import org.eclipse.osee.framework.ui.skynet.blam.sections.BlamInputSection;
import org.eclipse.osee.framework.ui.skynet.blam.sections.BlamOutputSection;
import org.eclipse.osee.framework.ui.skynet.blam.sections.BlamUsageSection;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayout;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Ryan D. Brooks
 */
public class BlamOverviewPage extends FormPage {

   private BlamOutputSection outputSection;
   private BlamInputSection inputSection;

   public BlamOverviewPage(BlamEditor editor) {
      super(editor, "overview", "BLAM Workflow");
   }

   @Override
   public void showBusy(boolean busy) {
      super.showBusy(busy);
      if (Widgets.isAccessible(getManagedForm().getForm())) {
         getManagedForm().getForm().getForm().setBusy(busy);
      }
   }

   @Override
   protected void createFormContent(IManagedForm managedForm) {
      super.createFormContent(managedForm);

      final ScrolledForm form = managedForm.getForm();
      final FormToolkit toolkit = managedForm.getToolkit();

      GridLayout layout = new GridLayout();
      layout.numColumns = 1;
      layout.marginHeight = 10;
      layout.marginWidth = 6;
      layout.horizontalSpacing = 20;
      form.getBody().setLayout(layout);
      form.getBody().setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));

      updateTitle(form);
      updateImage(form);
      addToolBar(toolkit, form, true);
      managedForm.getMessageManager().setAutoUpdate(false);
      PlatformUI.getWorkbench().getHelpSystem().setHelp(form.getBody(), IHelpContextIds.MAIN_WORKFLOW_PAGE);

      int sectionStyle = Section.TITLE_BAR | Section.EXPANDED | Section.TWISTIE;

      managedForm.addPart(new BlamUsageSection(getEditor(), getEditorInput().getBlamOperation(), form.getBody(),
            managedForm.getToolkit(), sectionStyle));
      inputSection =
            new BlamInputSection(getEditor(), getEditorInput().getBlamOperation(), form.getBody(),
                  managedForm.getToolkit(), sectionStyle);
      outputSection =
            new BlamOutputSection(getEditor(), getEditorInput().getBlamOperation(), form.getBody(),
                  managedForm.getToolkit(), sectionStyle, getEditor().getActionBarContributor().getExecuteBlamAction());

      managedForm.addPart(inputSection);
      managedForm.addPart(outputSection);

      managedForm.refresh();
      form.layout();
   }

   private void updateTitle(ScrolledForm form) {
      form.setText(getEditorInput().getName());
   }

   private void updateImage(ScrolledForm form) {
      form.setImage(getEditor().getEditorInput().getImage());
   }

   private void addToolBar(FormToolkit toolkit, ScrolledForm form, boolean add) {
      IToolBarManager manager = form.getToolBarManager();
      if (add) {
         getEditor().getActionBarContributor().contributeToToolBar(manager);
         manager.update(true);
      } else {
         manager.removeAll();
      }
      form.reflow(true);
   }

   @Override
   public BlamEditor getEditor() {
      return (BlamEditor) super.getEditor();
   }

   @Override
   public BlamEditorInput getEditorInput() {
      return (BlamEditorInput) super.getEditorInput();
   }

   public void refresh() {
      final ScrolledForm sForm = getManagedForm().getForm();
      for (IFormPart part : getManagedForm().getParts()) {
         part.refresh();
      }
      sForm.getBody().layout(true);
      sForm.reflow(true);
      getManagedForm().refresh();
   }

   public void appendOutput(final String additionalOutput) {
      outputSection.appendText(additionalOutput);
   }

   public void setOuputText(final String text) {
      outputSection.setText(text);
   }

   public Appendable getOutput() {
      return outputSection.getOutput();
   }

   public VariableMap getInput() {
      return inputSection.getData();
   }

   public void setDynamicXWidgetLayouts(Collection<DynamicXWidgetLayout> layouts) {
      inputSection.setDynamicXWidgetLayouts(layouts);
   }
}