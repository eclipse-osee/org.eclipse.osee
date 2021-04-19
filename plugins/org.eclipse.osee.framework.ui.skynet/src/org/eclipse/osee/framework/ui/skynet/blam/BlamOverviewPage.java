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

package org.eclipse.osee.framework.ui.skynet.blam;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.osee.framework.core.operation.NullOperationLogger;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.help.ui.OseeHelpContext;
import org.eclipse.osee.framework.ui.plugin.util.HelpUtil;
import org.eclipse.osee.framework.ui.skynet.blam.sections.BlamInputSection;
import org.eclipse.osee.framework.ui.skynet.blam.sections.BlamOutputSection;
import org.eclipse.osee.framework.ui.skynet.blam.sections.BlamUsageSection;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * @author Ryan D. Brooks
 */
public class BlamOverviewPage extends FormPage {

   private BlamOutputSection outputSection;
   private BlamInputSection inputSection;

   public BlamOverviewPage(BlamEditor editor) {
      super(editor, "overview", editor.getEditorInput().getBlamOperation().getTabTitle());
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
      HelpUtil.setHelp(form.getBody(), OseeHelpContext.BLAM_EDITOR);

      int sectionStyle = ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED | ExpandableComposite.TWISTIE;

      if (getEditorInput().getBlamOperation().showUsageSection()) {
         managedForm.addPart(new BlamUsageSection(getEditor(), getEditorInput().getBlamOperation(), form.getBody(),
            managedForm.getToolkit(), sectionStyle));
      }
      inputSection = new BlamInputSection(getEditor(), getEditorInput().getBlamOperation(), form.getBody(),
         managedForm.getToolkit(), sectionStyle);
      managedForm.addPart(inputSection);
      if (getEditorInput().getBlamOperation().showExecuteSection()) {
         Action executeBlamAction = getEditor().getActionBarContributor().getExecuteBlamAction();
         outputSection = new BlamOutputSection(getEditor(), getEditorInput().getBlamOperation(), form.getBody(),
            managedForm.getToolkit(), sectionStyle, executeBlamAction);
         managedForm.addPart(outputSection);
      }

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

   public void appendOutput(final String additionalOutput) {
      if (outputSection != null) {
         outputSection.appendText(additionalOutput);
      }
   }

   public void setOuputText(final String text) {
      if (outputSection != null) {
         outputSection.setText(text);
      }
   }

   public OperationLogger getReporter() {
      OperationLogger logger = null;
      if (outputSection != null) {
         logger = outputSection.getOutput();
      } else {
         logger = NullOperationLogger.getSingleton();
      }
      return logger;
   }

   public VariableMap getInput() {
      return inputSection.getData();
   }

   public void refreshTextSize() {
      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            outputSection.refresh();
         }
      });
   }
}
