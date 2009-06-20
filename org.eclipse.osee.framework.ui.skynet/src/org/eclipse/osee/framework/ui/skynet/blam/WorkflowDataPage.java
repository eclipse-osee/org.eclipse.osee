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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.IHelpContextIds;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.skynet.XWidgetParser;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Ryan D. Brooks
 */
public class WorkflowDataPage extends FormPage {
   private final BlamWorkflow workflow;
   private final XFormToolkit toolkit;
   private final List<Text> parameters;
   private final List<Text> localVariables;
   private final BlamOverviewPage overviewPage;
   private final DynamicXWidgetLayout dynamicXWidgetLayout;

   public WorkflowDataPage(BlamEditor editor, BlamOverviewPage overviewPage) {
      super(editor, "overview", "Workflow Data");

      this.workflow = editor.getEditorInput().getArtifact();
      this.toolkit = editor.getToolkit();
      this.parameters = new LinkedList<Text>();
      this.localVariables = new LinkedList<Text>();
      this.overviewPage = overviewPage;
      this.dynamicXWidgetLayout = new DynamicXWidgetLayout();
   }

   @Override
   protected void createFormContent(IManagedForm managedForm) {
      ScrolledForm form = managedForm.getForm();
      form.setText("Workflow Data");
      createToolBarActions(form);
      form.updateToolBar();
      fillBody(managedForm);
      managedForm.refresh();
   }

   private void createToolBarActions(final ScrolledForm form) {
      Action runAction = new Action("Generate Workflow Overview Page", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            StringBuilder strB = new StringBuilder(parameters.size() * 140);
            strB.append("<Widgets>");
            for (Text variableText : parameters) {
               strB.append(variableText.getText());
            }
            strB.append("</Widgets>");
            //            XWidgetParser widgetParser = new XWidgetParser();

            String widgetXml = strB.toString();

            try {
               dynamicXWidgetLayout.addWorkLayoutDatas(XWidgetParser.extractWorkAttributes(dynamicXWidgetLayout,
                     widgetXml));
               overviewPage.setDynamicXWidgetLayouts(Arrays.asList(dynamicXWidgetLayout));

               workflow.saveLayoutData(widgetXml);
            } catch (Exception ex) {
               OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
               MessageDialog.openError(form.getShell(), "Error", ex.getLocalizedMessage());
            }
         }
      };
      runAction.setToolTipText("Generates or regenerates the workflow overview page based on the widgets defined here");
      runAction.setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.GEAR));
      form.getToolBarManager().add(runAction);
   }

   private void fillBody(IManagedForm managedForm) {
      Composite body = managedForm.getForm().getBody();
      GridLayout gridLayout = new GridLayout(1, true);
      body.setLayout(gridLayout);

      PlatformUI.getWorkbench().getHelpSystem().setHelp(body, IHelpContextIds.MAIN_WORKFLOW_PAGE);

      createParametersSection(body);
      createLocalVariablesSection(body);
   }

   private void createLocalVariablesSection(Composite body) {
      Section section = toolkit.createSection(body, Section.TWISTIE | Section.TITLE_BAR);
      section.setText("Local Variables");
      section.setExpanded(true);
      section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      toolkit.addHelpLinkToSection(section, "/org.eclipse.pde.doc.user/guide/pde_running.htm");

      Composite variablesComposite = toolkit.createClientContainer(section, 2);
      addNewVariableLinkToSection(section, variablesComposite, localVariables);
   }

   private void createParametersSection(Composite body) {
      Section section = toolkit.createSection(body, Section.TWISTIE | Section.TITLE_BAR);
      section.setText("Workflow Parameters");
      section.setExpanded(true);
      section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      Composite variablesComposite = toolkit.createClientContainer(section, 2);
      addNewVariableLinkToSection(section, variablesComposite, parameters);
   }

   private void addNewVariableLinkToSection(final Section section, Composite variablesComposite, List<Text> variableTexts) {
      ImageHyperlink addLink = new ImageHyperlink(section, SWT.NULL);
      toolkit.adapt(addLink, true, true);
      addLink.setImage(ImageManager.getImage(FrameworkImage.ADD_GREEN));
      addLink.setBackground(section.getTitleBarGradientBackground());
      addLink.addHyperlinkListener(new AddListener(variablesComposite, variableTexts));
      section.setTextClient(addLink);
   }

   private class AddListener extends HyperlinkAdapter {
      private final Composite variablesComposite;
      private final List<Text> variableTexts;
      private final RemoveListener removeListener;

      public AddListener(Composite variablesComposite, List<Text> variableTexts) {
         this.variablesComposite = variablesComposite;
         this.variableTexts = variableTexts;
         removeListener = new RemoveListener(variableTexts);
      }

      @Override
      public void linkActivated(HyperlinkEvent ev) {
         ImageHyperlink removeLink = new ImageHyperlink(variablesComposite, SWT.NULL);
         toolkit.adapt(removeLink, true, true);
         removeLink.setImage(ImageManager.getImage(FrameworkImage.REMOVE));
         removeLink.addHyperlinkListener(removeListener);

         Text variableWidget =
               toolkit.createText(variablesComposite,
                     "<XWidget xwidgetType=\"XListDropViewer\" displayName=\"Get This\" />");
         removeLink.setData(variableWidget);
         variableTexts.add(variableWidget);
         variableWidget.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
         variablesComposite.getParent().getParent().layout();
      }
   }

   private class RemoveListener extends HyperlinkAdapter {
      private final List<Text> variableTexts;

      public RemoveListener(List<Text> variableTexts) {
         this.variableTexts = variableTexts;
      }

      @Override
      public void linkActivated(HyperlinkEvent ev) {
         ImageHyperlink removeLink = (ImageHyperlink) ev.widget;
         Text variableWidget = (Text) removeLink.getData();

         GridData gridData = (GridData) variableWidget.getLayoutData();
         gridData.exclude = true;
         variableWidget.setVisible(false);
         variableTexts.remove(variableWidget);

         gridData = (GridData) removeLink.getLayoutData();
         gridData.exclude = true;
         removeLink.setVisible(false);
         removeLink.getParent().getParent().getParent().layout();
      }
   }
}