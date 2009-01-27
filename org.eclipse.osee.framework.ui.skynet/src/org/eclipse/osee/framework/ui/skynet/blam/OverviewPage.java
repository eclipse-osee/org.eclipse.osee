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

import java.util.List;
import org.eclipse.jface.action.Action;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.skynet.IHelpContextIds;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayout;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IDynamicWidgetLayoutListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Ryan D. Brooks
 */
public class OverviewPage extends FormPage implements IActionable {
   private final BlamWorkflow workflow;
   private DynamicXWidgetLayout dynamicXWidgetLayout;
   private final XFormToolkit toolkit;
   private Composite parametersContainer;
   private final BlamEditor editor;
   private Text outputText;
   private Section parameterSection;
   private Section outputSection;
   private IManagedForm managedForm;
   private Composite outputComp;

   public OverviewPage(BlamEditor editor) {
      super(editor, "overview", "BLAM Workflow");

      this.editor = editor;
      this.toolkit = editor.getToolkit();
      this.workflow = editor.getWorkflow();
      try {
         BlamOperation blamOperation = workflow.getOperations().iterator().next();
         this.dynamicXWidgetLayout = null;
         if (blamOperation instanceof IDynamicWidgetLayoutListener) {
            this.dynamicXWidgetLayout = new DynamicXWidgetLayout((IDynamicWidgetLayoutListener) blamOperation, null);
         } else {
            this.dynamicXWidgetLayout = new DynamicXWidgetLayout();
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public void update(DynamicXWidgetLayout dynamicXWidgetLayout) throws Exception {
      //dispose old widgets before adding the new ones.
      for (Control control : parametersContainer.getChildren()) {
         control.dispose();
      }

      dynamicXWidgetLayout.createBody(toolkit, parametersContainer, null, null, true);
      parametersContainer.layout();
      parametersContainer.getParent().layout();
   }

   /**
    * Loads stored DynamicXWidgetLayoutData and updates the overview page
    * 
    * @param workflow
    */
   private void loadStoredLayoutData(BlamWorkflow workflow) {
      List<DynamicXWidgetLayoutData> layoutDatas = null;
      try {
         layoutDatas = workflow.getLayoutDatas();

         if (layoutDatas != null && !layoutDatas.isEmpty()) {
            dynamicXWidgetLayout.addWorkLayoutDatas(layoutDatas);
            update(dynamicXWidgetLayout);
         }
         parameterSection.setExpanded(true);
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   protected void createFormContent(IManagedForm managedForm) {
      ScrolledForm form = managedForm.getForm();
      form.setText(getEditorInput().getName());
      createToolBarActions(form);
      form.updateToolBar();
      fillBody(managedForm);

      loadStoredLayoutData(workflow);

      managedForm.refresh();
   }

   private void createToolBarActions(ScrolledForm form) {
      Action runAction = new Action("Run BLAM in Job", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            runWorkflow();
         }
      };
      runAction.setToolTipText("Starts the BLAM");
      runAction.setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("run_exc.gif"));
      form.getToolBarManager().add(runAction);

      OseeAts.addButtonToEditorToolBar(editor, this, SkynetGuiPlugin.getInstance(), form.getToolBarManager(),
            BlamEditor.EDITOR_ID, "BLAM Editor");

   }

   private void runWorkflow() {
      VariableMap blamVariableMap = editor.getBlamVariableMap();
      for (DynamicXWidgetLayoutData xWidgetData : dynamicXWidgetLayout.getLayoutDatas()) {
         XWidget widget = xWidgetData.getXWidget();
         blamVariableMap.setValue(widget.getLabel(), widget.getData());
      }

      BlamJob blamJob = new BlamJob(editor);
      blamJob.addListener(editor);
      Jobs.startJob(blamJob);
   }

   public String getActionDescription() {
      return "";
   }

   private void fillBody(IManagedForm managedForm) {
      this.managedForm = managedForm;
      ScrolledForm scrolledForm = managedForm.getForm();
      GridLayout gridLayout = new GridLayout(1, false);
      Composite body = scrolledForm.getBody();
      body.setLayout(gridLayout);
      body.setLayoutData(new GridData(SWT.LEFT, SWT.LEFT, true, false));

      PlatformUI.getWorkbench().getHelpSystem().setHelp(body, IHelpContextIds.MAIN_WORKFLOW_PAGE);

      managedForm.addPart(new SectionPart(createUsageSection(body)));
      managedForm.addPart(new SectionPart(createParametersSection(body)));
      managedForm.addPart(new SectionPart(createOutputSection(body)));
      managedForm.refresh();
   }

   private Section createUsageSection(Composite body) {
      Section section = toolkit.createSection(body, Section.TITLE_BAR);
      section.setText("Description and Usage");
      section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      toolkit.addHelpLinkToSection(section, "/org.eclipse.pde.doc.user/guide/pde_running.htm");

      Composite mainComp = toolkit.createClientContainer(section, 1);
      // mainComp.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_YELLOW));
      mainComp.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING));
      mainComp.layout();

      Text formText = toolkit.createText(mainComp, workflow.getDescriptionUsage(), SWT.NONE);
      formText.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING));

      section.layout();
      return section;
   }

   private Section createOutputSection(Composite body) {
      outputSection = toolkit.createSection(body, Section.TITLE_BAR);
      outputSection.setText("Execute");
      outputSection.setLayoutData(new GridData(GridData.FILL_BOTH));

      outputComp = toolkit.createClientContainer(outputSection, 1);
      // mainComp.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_YELLOW));
      outputComp.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING));
      outputComp.setLayout(new GridLayout(1, false));

      Button button = toolkit.createButton(outputComp, "Run this BLAM", SWT.PUSH);
      button.setImage(SkynetGuiPlugin.getInstance().getImage("run_exc.gif"));
      button.addListener(SWT.MouseUp, new Listener() {
         @Override
         public void handleEvent(Event event) {
            runWorkflow();
         }
      });

      outputText = toolkit.createText(outputComp, "BLAM has not yet run\n", SWT.WRAP);
      GridData gridData = new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING);
      gridData.heightHint = 500;
      outputText.setLayoutData(gridData);

      outputSection.layout();

      return outputSection;
   }

   /**
    * @param line
    */
   public void appendOuputLine(String additionalOutput) {
      outputText.append(additionalOutput);
      managedForm.reflow(true);
   }

   private Section createParametersSection(Composite body) {
      parameterSection = toolkit.createSection(body, Section.TITLE_BAR);
      parameterSection.setText("Parameters");
      parameterSection.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      parametersContainer = toolkit.createClientContainer(parameterSection, 1);

      return parameterSection;
   }

   /**
    * @param line
    */
   public void setOuputText(String text) {
      outputText.setText(text);
   }
}