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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidgetUtility;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DefaultXWidgetOptionResolver;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayout;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPage;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Roberto E. Escobar
 */
public class BlamInputSection extends BaseBlamSection {

   private final Collection<DynamicXWidgetLayoutData> dynamicInputLayouts;

   public BlamInputSection(FormEditor editor, AbstractBlam abstractBlam, Composite parent, FormToolkit toolkit, int style) {
      super(editor, abstractBlam, parent, toolkit, style);
      this.dynamicInputLayouts = new ArrayList<DynamicXWidgetLayoutData>();
   }

   @Override
   public void initialize(IManagedForm form) {
      super.initialize(form);
      Section section = getSection();
      section.setText("Parameters");
      section.setLayout(new GridLayout());
      section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      updateDataPart();
   }

   public VariableMap getData() {
      VariableMap blamVariableMap = new VariableMap();
      List<XWidget> xWidgets = XWidgetUtility.findXWidgetsInControl(getSection());
      for (XWidget xWidget : xWidgets) {
         blamVariableMap.setValue(xWidget.getLabel(), xWidget.getData());
      }
      return blamVariableMap;
   }

   private void updateDataPart() {
      final IManagedForm form = getManagedForm();
      final FormToolkit toolkit = form.getToolkit();
      final Section section = getSection();

      Control control = section.getClient();
      if (Widgets.isAccessible(control)) {
         control.dispose();
      }
      Composite sectionBody = toolkit.createComposite(section, toolkit.getBorderStyle());
      sectionBody.setLayout(new GridLayout());
      sectionBody.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      createWidgets(sectionBody);

      section.setClient(sectionBody);
      toolkit.paintBordersFor(section);

      section.layout(true);
      form.getForm().getBody().layout(true);

   }

   private void createWidgets(Composite parent) {
      try {
         List<DynamicXWidgetLayoutData> layoutDatas = getDynamicXWidgetLayouts();
         WorkPage workPage = new WorkPage(layoutDatas, new DefaultXWidgetOptionResolver(), getAbstractBlam());
         workPage.createBody(getManagedForm(), parent, null, new XModifiedListener() {

            @Override
            public void widgetModified(XWidget widget) {
               getManagedForm().reflow(true);
            }

         }, true);
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   private List<DynamicXWidgetLayoutData> getDynamicXWidgetLayouts() throws Exception {
      List<DynamicXWidgetLayoutData> itemsToReturn = new ArrayList<DynamicXWidgetLayoutData>();
      itemsToReturn.addAll(getAbstractBlam().getLayoutDatas());
      itemsToReturn.addAll(dynamicInputLayouts);
      return itemsToReturn;
   }

   @Override
   public void dispose() {
      super.dispose();
   }

   @Override
   public void refresh() {
      super.refresh();
      updateDataPart();
   }

   public void setDynamicXWidgetLayouts(Collection<DynamicXWidgetLayout> layouts) {
      if (layouts != null) {
         this.dynamicInputLayouts.clear();
         for (DynamicXWidgetLayout layout : layouts) {
            this.dynamicInputLayouts.addAll(layout.getLayoutDatas());
         }
         refresh();
      }
   }
}
