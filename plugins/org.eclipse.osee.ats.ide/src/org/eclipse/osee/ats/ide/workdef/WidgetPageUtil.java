/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.ats.ide.workdef;

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.workdef.WidgetOption;
import org.eclipse.osee.ats.api.workdef.model.CompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.model.LayoutItem;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;
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
import org.eclipse.osee.framework.ui.skynet.widgets.ArtifactWidget;
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

   public static String getHtml(String backgroundColor, String preHtml, String postHtml, Set<XWidgetRendererItem> set,
      String name) {
      StringBuffer sb = new StringBuffer();
      int defaultWidth = 100;
      sb.append(AHTML.startBorderTable(defaultWidth, backgroundColor, name));
      if (preHtml != null) {
         sb.append(preHtml);
      }
      for (XWidgetRendererItem layoutData : set) {
         XWidget xWidget = layoutData.getXWidget();
         if (xWidget instanceof ArtifactWidget) {
            ((ArtifactWidget) xWidget).setArtifact(layoutData.getArtifact());
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

   public static void createXWidgetLayoutData(XWidgetRendererItem layoutData, XWidget xWidget, FormToolkit toolkit,
      Artifact art, XModifiedListener xModListener, boolean isEditable) {

      // If no tool tip, add global tool tip
      if (!Strings.isValid(xWidget.getToolTip())) {
         String description = "";
         if (layoutData.getXWidgetName().equals(XCommitManager.class.getSimpleName())) {
            description = XCommitManager.DESCRIPTION;
         }
         AttributeTypeToken type = null;
         if (layoutData.getStoreId() > 0) {
            type = AttributeTypeManager.getAttributeType(layoutData.getStoreId());
         }
         if (type == null && Strings.isValid(layoutData.getStoreName())) {
            type = AttributeTypeManager.getType(layoutData.getStoreName());
         }
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

   public static void generateLayoutDatas(AbstractWorkflowArtifact sma, Collection<LayoutItem> layoutItems,
      SwtXWidgetRenderer dynamicXWidgetLayout) {
      // Add static layoutDatas to statePage
      for (LayoutItem stateItem : layoutItems) {
         if (stateItem instanceof WidgetDefinition) {
            processWidgetDefinition((WidgetDefinition) stateItem, sma, dynamicXWidgetLayout);
         } else if (stateItem instanceof CompositeLayoutItem) {
            processComposite((CompositeLayoutItem) stateItem, sma, dynamicXWidgetLayout);
         }
      }
   }

   public static void processComposite(CompositeLayoutItem compositeLayoutItem, AbstractWorkflowArtifact sma,
      SwtXWidgetRenderer dynamicXWidgetLayout) {
      boolean firstWidget = true;

      // Group Comp is stand-alone renderer item
      boolean inGroupComp = false;
      if (compositeLayoutItem.isGroupComposite()) {
         inGroupComp = true;
         XWidgetRendererItem newCompItem = new XWidgetRendererItem(dynamicXWidgetLayout);
         newCompItem.setName(compositeLayoutItem.getName());
         newCompItem.setBeginGroupComposite(compositeLayoutItem.getNumColumns());
         dynamicXWidgetLayout.addWorkLayoutData(newCompItem);

      }

      List<LayoutItem> stateItems = compositeLayoutItem.getLayoutItems();
      for (int x = 0; x < stateItems.size(); x++) {
         boolean lastWidget = x == stateItems.size() - 1;
         LayoutItem stateItem = stateItems.get(x);
         if (stateItem instanceof WidgetDefinition) {
            XWidgetRendererItem renderItem =
               processWidgetDefinition((WidgetDefinition) stateItem, sma, dynamicXWidgetLayout);
            if (firstWidget) {
               if (compositeLayoutItem.getNumColumns() > 0) {
                  if (!compositeLayoutItem.isGroupComposite()) {
                     renderItem.setBeginComposite(compositeLayoutItem.getNumColumns());
                  }
               }
            }
            if (lastWidget) {
               renderItem.setEndComposite(true);
            }
         } else if (stateItem instanceof CompositeLayoutItem) {
            CompositeLayoutItem compLayoutItem = (CompositeLayoutItem) stateItem;
            processComposite(compLayoutItem, sma, dynamicXWidgetLayout);
         }
         firstWidget = false;
      }

      if (inGroupComp) {
         XWidgetRendererItem newCompItem = new XWidgetRendererItem(dynamicXWidgetLayout);
         newCompItem.setEndGroupComposite(true);
         dynamicXWidgetLayout.addWorkLayoutData(newCompItem);
      }

   }

   /**
    * TODO This will eventually go away and ATS pages will be generated straight from WidgetDefinitions.
    */
   public static XWidgetRendererItem processWidgetDefinition(WidgetDefinition widgetDef, AbstractWorkflowArtifact sma,
      SwtXWidgetRenderer dynamicXWidgetLayout) {
      XWidgetRendererItem rItem = null;
      try {
         rItem = new XWidgetRendererItem(dynamicXWidgetLayout);
         rItem.setDefaultValue(widgetDef.getDefaultValue());
         rItem.setHeight(widgetDef.getHeight());
         if (widgetDef.getAttributeType() != null) {
            rItem.setStoreName(widgetDef.getAttributeType().getName());
            rItem.setStoreId(widgetDef.getAttributeType().getId());
            rItem.setAttributeType(widgetDef.getAttributeType());
         }
         if (widgetDef.getAttributeType2() != null) {
            rItem.setStoreName(widgetDef.getAttributeType().getName());
            rItem.setStoreId(widgetDef.getAttributeType().getId());
            rItem.setAttributeType2(widgetDef.getAttributeType2());
         }
         rItem.setWidgetHints(widgetDef.getWidgetHints());
         rItem.setRelationTypeSide(widgetDef.getRelationTypeSide());
         rItem.setOseeImage(widgetDef.getOseeImage());
         rItem.setEnumeratedArt(widgetDef.getEnumeratedArt());
         rItem.setComputedCharacteristic(widgetDef.getComputedCharacteristic());
         rItem.setToolTip(widgetDef.getToolTip());
         rItem.setId(widgetDef.getName());
         rItem.setXWidgetName(widgetDef.getXWidgetName());
         rItem.setArtifact(sma);
         rItem.setName(widgetDef.getName());
         rItem.setObject(widgetDef);
         if (widgetDef.is(WidgetOption.MULTI_SELECT)) {
            rItem.getXOptionHandler().add(XOption.MULTI_SELECT);
         } else if (widgetDef.is(WidgetOption.SINGLE_SELECT)) {
            rItem.getXOptionHandler().add(XOption.SINGLE_SELECT);
         }
         if (widgetDef.is(WidgetOption.REQUIRED_FOR_TRANSITION)) {
            rItem.getXOptionHandler().add(XOption.REQUIRED);
         } else if (widgetDef.is(WidgetOption.REQUIRED_FOR_COMPLETION)) {
            rItem.getXOptionHandler().add(XOption.REQUIRED_FOR_COMPLETION);
         }
         if (widgetDef.is(WidgetOption.FILL_HORIZONTALLY)) {
            rItem.getXOptionHandler().add(XOption.FILL_HORIZONTALLY);
         } else if (widgetDef.is(WidgetOption.FILL_VERTICALLY)) {
            rItem.getXOptionHandler().add(XOption.FILL_VERTICALLY);
         }
         for (WidgetOption widgetOpt : widgetDef.getOptions().getXOptions()) {
            XOption option = null;
            try {
               option = XOption.valueOf(widgetOpt.name());
            } catch (IllegalArgumentException ex) {
               // do nothing
            }
            if (option != null) {
               rItem.getXOptionHandler().add(option);
            }
         }
         for (Entry<String, Object> pair : widgetDef.getParameters().entrySet()) {
            rItem.getParameters().put(pair.getKey(), pair.getValue());
         }
         rItem.setConditions(widgetDef.getConditions());
         dynamicXWidgetLayout.addWorkLayoutData(rItem);
      } catch (Exception ex) {
         rItem = new XWidgetRendererItem(dynamicXWidgetLayout);
         rItem.setId(Lib.generateArtifactIdAsInt().toString());
         rItem.setXWidgetName("XLabel");
         rItem.setName("Error: " + widgetDef.getName() + " (double-click to view error)");
         rItem.setToolTip("Double-click to see error.");
         rItem.setDoubleClickText(Lib.exceptionToString(ex));
         OseeLog.logf(StateXWidgetPage.class, Level.SEVERE, ex, "Exception processing widget [%s]",
            widgetDef.getName());
         rItem.setObject(widgetDef);
         dynamicXWidgetLayout.addWorkLayoutData(rItem);
      }
      return rItem;
   }

}
