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
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.skynet.XWidgetParser;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.parts.AttributeFormPart;
import org.eclipse.osee.framework.ui.skynet.util.FormsUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.util.DefaultXWidgetOptionResolver;
import org.eclipse.osee.framework.ui.skynet.widgets.util.IDynamicWidgetLayoutListener;
import org.eclipse.osee.framework.ui.skynet.widgets.util.IXWidgetOptionResolver;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetRendererItem;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.FontManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Donald G. Dunne
 */
public abstract class AtsXWidgetActionFormPage extends FormPage {
   protected SwtXWidgetRenderer dynamicXWidgetLayout;
   protected final XFormToolkit toolkit;
   private Composite parametersContainer;
   private Section parameterSection;
   protected Composite resultsContainer;
   protected Section resultsSection;
   protected ScrolledForm scrolledForm;
   private String title;
   private String xWidgetXml;

   public AtsXWidgetActionFormPage(FormEditor editor, String id, String name) {
      super(editor, id, name);
      this.toolkit = new XFormToolkit();
   }

   public abstract Result isResearchSearchValid();

   public abstract String getXWidgetsXml();

   @Override
   protected void createFormContent(IManagedForm managedForm) {
      scrolledForm = managedForm.getForm();
      FormsUtil.addHeadingGradient(toolkit, scrolledForm, true);

      Composite body = scrolledForm.getBody();
      body.setLayout(ALayout.getZeroMarginLayout(1, true));
      body.setLayoutData(new GridData(SWT.LEFT, SWT.LEFT, false, false));

      xWidgetXml = getXWidgetsXml();
      try {
         if (Strings.isValid(xWidgetXml)) {
            managedForm.addPart(new SectionPart(createParametersSection(managedForm, body)));
         }
         managedForm.addPart(new SectionPart(createResultsSection(body)));
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

      AttributeFormPart.setLabelFonts(body, FontManager.getDefaultLabelFont());

      createToolBar();
      managedForm.refresh();
   }

   protected void createToolBar(IToolBarManager toolBarManager) {
      // provided for subclass implementation
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
            IManagedForm manager = getManagedForm();
            if (manager != null && Widgets.isAccessible(manager.getForm())) {
               getManagedForm().reflow(true);
            }
         }
      });
   }

   private Section createParametersSection(IManagedForm managedForm, Composite body) {
      parameterSection = toolkit.createSection(body, ExpandableComposite.TWISTIE);
      parameterSection.setText("Parameters");
      parameterSection.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      parametersContainer = toolkit.createClientContainer(parameterSection, 1);
      parameterSection.setExpanded(true);

      Composite mainComp = toolkit.createComposite(parametersContainer, SWT.NONE);
      mainComp.setLayout(ALayout.getZeroMarginLayout(3, false));
      mainComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      createButtonCompositeOnLeft(mainComp);
      createSearchParametersOnRight(managedForm, mainComp);
      createParametersSectionCompleted(managedForm, mainComp);

      return parameterSection;
   }

   public void createSearchParametersOnRight(IManagedForm managedForm, Composite mainComp) {
      Composite paramComp = new Composite(mainComp, SWT.NONE);
      paramComp.setLayout(ALayout.getZeroMarginLayout(1, false));
      paramComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      List<XWidgetRendererItem> layoutDatas = null;
      dynamicXWidgetLayout = new SwtXWidgetRenderer(getDynamicWidgetLayoutListener(), getXWidgetOptionResolver());
      try {
         layoutDatas = XWidgetParser.extractWorkAttributes(dynamicXWidgetLayout, xWidgetXml);
         if (layoutDatas != null && !layoutDatas.isEmpty()) {
            dynamicXWidgetLayout.addWorkLayoutDatas(layoutDatas);
            dynamicXWidgetLayout.createBody(managedForm, paramComp, null, null, true);
            parametersContainer.layout();
            parametersContainer.getParent().layout();
         }
         parameterSection.setExpanded(true);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public void createButtonCompositeOnLeft(Composite mainComp) {
      Composite buttonComp = toolkit.createComposite(mainComp, SWT.NONE);
      buttonComp.setLayout(ALayout.getZeroMarginLayout(1, false));
      buttonComp.setLayoutData(new GridData(SWT.NONE, SWT.FILL, false, true));

      Button runButton = toolkit.createButton(buttonComp, "Search", SWT.PUSH);
      GridData gridData = new GridData(SWT.FILL, SWT.BOTTOM, true, true);
      runButton.setLayoutData(gridData);
      runButton.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            handleSearchButtonPressed();
         }
      });

      buttonComp.layout();
   }

   public IDynamicWidgetLayoutListener getDynamicWidgetLayoutListener() {
      return null;
   }

   public IXWidgetOptionResolver getXWidgetOptionResolver() {
      return new DefaultXWidgetOptionResolver();
   }

   public abstract void handleSearchButtonPressed();

   /**
    * Create extra controls and return title if it changed
    */
   public void createParametersSectionCompleted(IManagedForm managedForm, Composite mainComp) {
      // do nothing
   }

   public void setTableTitle(final String title, final boolean warning) {
      this.title = Strings.truncate(title, WorldEditor.TITLE_MAX_LENGTH);
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            if (Widgets.isAccessible(scrolledForm)) {
               scrolledForm.setText(title);
            }
         };
      });
   }

   public abstract Section createResultsSection(Composite body);

   public String getCurrentTitleLabel() {
      String useTitle = "World Editor";
      if (title != null) {
         useTitle = title;
      }
      return Strings.truncate(useTitle, WorldEditor.TITLE_MAX_LENGTH);
   }

}