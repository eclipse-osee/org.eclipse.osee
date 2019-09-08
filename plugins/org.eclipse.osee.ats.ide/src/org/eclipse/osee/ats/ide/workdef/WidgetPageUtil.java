/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.workdef;

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.workdef.IAtsCompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.IAtsLayoutItem;
import org.eclipse.osee.ats.api.workdef.IAtsWidgetDefinition;
import org.eclipse.osee.ats.api.workdef.WidgetOption;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.util.widgets.commit.XCommitManager;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XOption;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetRendererItem;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public class WidgetPageUtil {

   public static void dispose(SwtXWidgetRenderer dynamicXWidgetLayout) {
      try {
         for (XWidgetRendererItem layoutData : dynamicXWidgetLayout.getLayoutDatas()) {
            layoutData.getXWidget().dispose();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public static String getHtml(String backgroundColor, String preHtml, String postHtml, Set<XWidgetRendererItem> set, String name) {
      StringBuffer sb = new StringBuffer();
      int defaultWidth = 100;
      sb.append(AHTML.startBorderTable(defaultWidth, backgroundColor, name));
      if (preHtml != null) {
         sb.append(preHtml);
      }
      for (XWidgetRendererItem layoutData : set) {
         XWidget xWidget = layoutData.getXWidget();
         if (xWidget instanceof IArtifactWidget) {
            ((IArtifactWidget) xWidget).setArtifact(layoutData.getArtifact());
         }
         sb.append(layoutData.getXWidget().toHTML(AHTML.LABEL_FONT));
         sb.append(AHTML.newline());
      }
      if (postHtml != null) {
         sb.append(postHtml);
      }
      sb.append(AHTML.endBorderTable());
      return sb.toString();
   }

   public static void createXWidgetLayoutData(XWidgetRendererItem layoutData, XWidget xWidget, FormToolkit toolkit, Artifact art, XModifiedListener xModListener, boolean isEditable) {

      // If no tool tip, add global tool tip
      if (!Strings.isValid(xWidget.getToolTip())) {
         String description = "";
         if (layoutData.getXWidgetName().equals(XCommitManager.WIDGET_NAME)) {
            description = XCommitManager.DESCRIPTION;
         }
         AttributeTypeToken type = AttributeTypeManager.getType(layoutData.getStoreName());
         if (type != null && Strings.isValid(type.getDescription())) {
            description = type.getDescription();
         }
         if (Strings.isValid(description)) {
            xWidget.setToolTip(description);
            layoutData.setToolTip(description);
         }
      }
      // Store workAttr in control for use by help
      if (xWidget.getControl() != null) {
         xWidget.getControl().setData(layoutData);
      }

   }

   public static void generateLayoutDatas(AbstractWorkflowArtifact sma, Collection<IAtsLayoutItem> layoutItems, SwtXWidgetRenderer dynamicXWidgetLayout) {
      // Add static layoutDatas to statePage
      for (IAtsLayoutItem stateItem : layoutItems) {
         if (stateItem instanceof IAtsWidgetDefinition) {
            processWidgetDefinition((IAtsWidgetDefinition) stateItem, sma, dynamicXWidgetLayout);
         } else if (stateItem instanceof IAtsCompositeLayoutItem) {
            processComposite((IAtsCompositeLayoutItem) stateItem, sma, dynamicXWidgetLayout);
         }
      }
   }

   public static void processComposite(IAtsCompositeLayoutItem compositeStateItem, AbstractWorkflowArtifact sma, SwtXWidgetRenderer dynamicXWidgetLayout) {
      boolean firstWidget = true;
      List<IAtsLayoutItem> stateItems = compositeStateItem.getaLayoutItems();
      for (int x = 0; x < stateItems.size(); x++) {
         boolean lastWidget = x == stateItems.size() - 1;
         IAtsLayoutItem stateItem = stateItems.get(x);
         if (stateItem instanceof IAtsWidgetDefinition) {
            XWidgetRendererItem data =
               processWidgetDefinition((IAtsWidgetDefinition) stateItem, sma, dynamicXWidgetLayout);
            if (firstWidget) {
               if (compositeStateItem.getNumColumns() > 0) {
                  data.setBeginComposite(compositeStateItem.getNumColumns());
               }
            }
            if (lastWidget) {
               data.setEndComposite(true);
            }
         } else if (stateItem instanceof IAtsCompositeLayoutItem) {
            processComposite((IAtsCompositeLayoutItem) stateItem, sma, dynamicXWidgetLayout);
         }
         firstWidget = false;
      }
   }

   /**
    * TODO This will eventually go away and ATS pages will be generated straight from WidgetDefinitions.
    */
   public static XWidgetRendererItem processWidgetDefinition(IAtsWidgetDefinition widgetDef, AbstractWorkflowArtifact sma, SwtXWidgetRenderer dynamicXWidgetLayout) {
      XWidgetRendererItem data = null;
      try {
         data = new XWidgetRendererItem(dynamicXWidgetLayout);
         data.setDefaultValue(widgetDef.getDefaultValue());
         data.setHeight(widgetDef.getHeight());
         if (widgetDef.getAttributeType() != null) {
            data.setStoreName(widgetDef.getAttributeType().getName());
            data.setStoreId(widgetDef.getAttributeType().getId());
         }
         data.setToolTip(widgetDef.getToolTip());
         data.setId(widgetDef.getName());
         data.setXWidgetName(widgetDef.getXWidgetName());
         data.setArtifact(sma);
         data.setName(widgetDef.getName());
         data.setObject(widgetDef);
         if (widgetDef.is(WidgetOption.REQUIRED_FOR_TRANSITION)) {
            data.getXOptionHandler().add(XOption.REQUIRED);
         } else if (widgetDef.is(WidgetOption.REQUIRED_FOR_COMPLETION)) {
            data.getXOptionHandler().add(XOption.REQUIRED_FOR_COMPLETION);
         }
         for (WidgetOption widgetOpt : widgetDef.getOptions().getXOptions()) {
            XOption option = null;
            try {
               option = XOption.valueOf(widgetOpt.name());
            } catch (IllegalArgumentException ex) {
               // do nothing
            }
            if (option != null) {
               data.getXOptionHandler().add(option);
            }
         }
         for (Entry<String, Object> pair : widgetDef.getParameters().entrySet()) {
            data.getParameters().put(pair.getKey(), pair.getValue());
         }
         dynamicXWidgetLayout.addWorkLayoutData(data);
      } catch (Exception ex) {
         data = new XWidgetRendererItem(dynamicXWidgetLayout);
         data.setId(Lib.generateArtifactIdAsInt().toString());
         data.setXWidgetName("XLabel");
         data.setName("Error: " + widgetDef.getName() + " (double-click to view error)");
         data.setToolTip("Double-click to see error.");
         data.setDoubleClickText(Lib.exceptionToString(ex));
         OseeLog.logf(StateXWidgetPage.class, Level.SEVERE, ex, "Exception processing widget [%s]",
            widgetDef.getName());
         data.setObject(widgetDef);
         dynamicXWidgetLayout.addWorkLayoutData(data);
      }
      return data;
   }

}
