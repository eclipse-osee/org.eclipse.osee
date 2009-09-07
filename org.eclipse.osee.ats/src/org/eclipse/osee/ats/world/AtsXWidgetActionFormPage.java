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
package org.eclipse.osee.ats.world;

import java.util.List;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.skynet.XWidgetParser;
import org.eclipse.osee.framework.ui.skynet.util.FormsUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DefaultXWidgetOptionResolver;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayout;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IDynamicWidgetLayoutListener;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IXWidgetOptionResolver;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Donald G. Dunne
 */
public abstract class AtsXWidgetActionFormPage extends FormPage {
   protected DynamicXWidgetLayout dynamicXWidgetLayout;
   protected final XFormToolkit toolkit;
   private Composite parametersContainer;
   private Section parameterSection;
   protected Composite resultsContainer;
   protected Section resultsSection;
   protected ScrolledForm scrolledForm;
   private String title;

   public AtsXWidgetActionFormPage(FormEditor editor, String id, String name) {
      super(editor, id, name);
      this.toolkit = new XFormToolkit(SkynetGuiPlugin.getInstance().getSharedFormColors(Display.getCurrent()));
   }

   public abstract Result isResearchSearchValid() throws OseeCoreException;

   public abstract String getXWidgetsXml() throws OseeCoreException;

   @Override
   protected void createFormContent(IManagedForm managedForm) {
      scrolledForm = managedForm.getForm();
      FormsUtil.addHeadingGradient(toolkit, scrolledForm, true);

      Composite body = scrolledForm.getBody();
      body.setLayout(ALayout.getZeroMarginLayout(1, true));
      body.setLayoutData(new GridData(SWT.LEFT, SWT.LEFT, false, false));

      try {
         if (getXWidgetsXml() != null && !getXWidgetsXml().equals("")) {
            managedForm.addPart(new SectionPart(createParametersSection(managedForm, body)));
         }
         managedForm.addPart(new SectionPart(createResultsSection(body)));
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }

      createToolBar();
      managedForm.refresh();
   }

   protected void createToolBar(IToolBarManager toolBarManager) {

   }

   private void createToolBar() {
      IToolBarManager toolBarManager = scrolledForm.getToolBarManager();
      createToolBar(toolBarManager);
      scrolledForm.updateToolBar();
   }

   public void reflow() {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            getManagedForm().reflow(true);
         }
      });
   }

   private Section createParametersSection(IManagedForm managedForm, Composite body) {
      parameterSection = toolkit.createSection(body, Section.NO_TITLE);
      parameterSection.setText("Parameters");
      parameterSection.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      parametersContainer = toolkit.createClientContainer(parameterSection, 1);
      parameterSection.setExpanded(true);

      Composite mainComp = toolkit.createComposite(parametersContainer, SWT.NONE);
      mainComp.setLayout(ALayout.getZeroMarginLayout(2, false));
      mainComp.setLayoutData(new GridData(SWT.NONE, SWT.FILL, false, true));

      Button runButton = toolkit.createButton(mainComp, "Search", SWT.PUSH);
      GridData gridData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
      runButton.setLayoutData(gridData);
      runButton.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            handleSearchButtonPressed();
         }
      });

      Composite paramComp = new Composite(mainComp, SWT.NONE);
      paramComp.setLayout(ALayout.getZeroMarginLayout(1, false));
      paramComp.setLayoutData(new GridData(SWT.NONE, SWT.FILL, false, true));

      List<DynamicXWidgetLayoutData> layoutDatas = null;
      dynamicXWidgetLayout = new DynamicXWidgetLayout(getDynamicWidgetLayoutListener(), getXWidgetOptionResolver());
      try {
         layoutDatas = XWidgetParser.extractWorkAttributes(dynamicXWidgetLayout, getXWidgetsXml());
         if (layoutDatas != null && !layoutDatas.isEmpty()) {
            dynamicXWidgetLayout.addWorkLayoutDatas(layoutDatas);
            dynamicXWidgetLayout.createBody(managedForm, paramComp, null, null, true);
            parametersContainer.layout();
            parametersContainer.getParent().layout();
         }
         parameterSection.setExpanded(true);
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }

      return parameterSection;
   }

   public IDynamicWidgetLayoutListener getDynamicWidgetLayoutListener() {
      return null;
   }

   public IXWidgetOptionResolver getXWidgetOptionResolver() {
      return new DefaultXWidgetOptionResolver();
   }

   public abstract void handleSearchButtonPressed();

   public void setTableTitle(final String title, final boolean warning) {
      this.title = title;
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            scrolledForm.setText(title);
         };
      });
   }

   public abstract Section createResultsSection(Composite body) throws OseeCoreException;

   public ScrolledForm getScrolledForm() {
      return scrolledForm;
   }

   public String getCurrentTitleLabel() {
      if (title != null) {
         return title;
      } else
         return WorldEditor.EDITOR_ID;
   }

}