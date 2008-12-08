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
package org.eclipse.osee.framework.ui.skynet.artifact.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.AttributeXWidgetFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DefaultXWidgetOptionResolver;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPage;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

public class NewAttributesComposite extends Composite {
   private final Artifact artifact;
   private Label warningLabel;
   private final ToolBar toolBar;
   private final FormToolkit toolkit;
   private final ScrolledForm scrolledForm;
   private final Composite mainComp;
   private WorkPage workPage;

   public static final int NAME_COLUMN_INDEX = 0;
   public static final int VALUE_COLUMN_INDEX = 1;
   private final IDirtiableEditor iDirtiableEditor;

   public NewAttributesComposite(IDirtiableEditor iDirtiableEditor, Composite parent, int style, Artifact artifact, ToolBar toolBar) {
      super(parent, style);
      this.iDirtiableEditor = iDirtiableEditor;
      this.artifact = artifact;
      this.toolBar = toolBar;
      setLayout(ALayout.getZeroMarginLayout(1, true));
      setLayoutData(new GridData(GridData.FILL_BOTH));

      toolkit = new FormToolkit(getDisplay());
      scrolledForm = toolkit.createScrolledForm(this);
      scrolledForm.setLayout(new GridLayout(1, false));
      scrolledForm.setLayoutData(new GridData(GridData.FILL_BOTH));

      mainComp = scrolledForm.getBody();
      mainComp.setLayout(new GridLayout(1, false));
      mainComp.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING));
      //      mainComp.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_YELLOW));
      mainComp.layout();

      try {
         List<DynamicXWidgetLayoutData> widgets = new ArrayList<DynamicXWidgetLayoutData>();
         for (AttributeType attrType : getOrderedAttributeTypes()) {
            widgets.add(AttributeXWidgetFactory.getAttributeXWidgetProvider(attrType).getDynamicXWidgetLayoutData(
                  attrType));
         }

         workPage = new WorkPage(widgets, new DefaultXWidgetOptionResolver());
         workPage.createBody(toolkit, mainComp, artifact, xModifiedListener, true);
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      }
   }

   XModifiedListener xModifiedListener = new XModifiedListener() {
      public void widgetModified(XWidget widget) {
         System.out.println("new attr comp - modified listener");
         mainComp.layout();
         iDirtiableEditor.onDirtied();
      };
   };

   public Result isDirty() throws Exception {
      if (workPage == null) return Result.FalseResult;
      for (DynamicXWidgetLayoutData xLayoutData : workPage.getlayoutDatas()) {
         for (XWidget widget : xLayoutData.getDynamicXWidgetLayout().getXWidgets()) {
            if (widget instanceof IArtifactWidget) {
               Result result = ((IArtifactWidget) widget).isDirty();
               if (result.isTrue()) return result;
            }
         }
      }
      return Result.FalseResult;
   }

   private List<AttributeType> getOrderedAttributeTypes() throws Exception {
      List<AttributeType> allTypes = new ArrayList<AttributeType>(artifact.getAttributeTypes());
      List<AttributeType> types = new ArrayList<AttributeType>();
      Map<String, AttributeType> attrMap = new HashMap<String, AttributeType>();

      // Name attribute first
      for (AttributeType type : artifact.getAttributeTypes()) {
         if (type.getName().equals("Name")) {
            types.add(type);
            allTypes.remove(type);
         } else {
            attrMap.put(type.getName(), type);
         }
      }

      //      // All all attributes that have a value
      //      for (AttributeType attributeType : artifact.getAttributeTypes()) {
      //         if (artifact.getAttributes(attributeType.getName()).size() > 0 && !attributeType.getName().equals("Name")) {
      //            types.add(attributeType);
      //            allTypes.remove(attributeType);
      //         }
      //      }

      // Add in alpha order
      String[] names = attrMap.keySet().toArray(new String[attrMap.size()]);
      Arrays.sort(names);
      for (String name : names) {
         types.add(attrMap.get(name));
      }

      return types;
   }

   public void updateLabel(String msg) {
      warningLabel.setText(msg);
      layout();
   }

   public Artifact getArtifact() {
      return artifact;
   }

   /**
    * @return the toolBar
    */
   public ToolBar getToolBar() {
      return toolBar;
   }
}
