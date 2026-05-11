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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.XWidgetData;
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
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public class WidgetPageUtil {

   public static Map<WidgetOption, XOption> widOptToXOptionMap = new HashMap<>();

   public static void dispose(SwtXWidgetRenderer swtXWidgetRenderer) {
      try {
         for (XWidgetData widData : swtXWidgetRenderer.getXWidgetDatas()) {
            swtXWidgetRenderer.getXWidget(widData).dispose();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public static String getHtml(String backgroundColor, String preHtml, String postHtml, Set<XWidgetData> widDatas,
      String name, SwtXWidgetRenderer swtXWidgetRenderer) {
      StringBuffer sb = new StringBuffer();
      int defaultWidth = 100;
      sb.append(AHTML.startBorderTable(defaultWidth, backgroundColor, name));
      if (preHtml != null) {
         sb.append(preHtml);
      }
      for (XWidgetData widData : widDatas) {
         XWidget xWidget = swtXWidgetRenderer.getXWidget(widData);
         if (xWidget instanceof ArtifactWidget) {
            ((ArtifactWidget) xWidget).setArtifact((Artifact) widData.getArtifact());
         }
         sb.append(xWidget.toHTML(AHTML.LABEL_FONT));
         sb.append(AHTML.newline());
      }
      if (postHtml != null) {
         sb.append(postHtml);
      }
      sb.append(AHTML.endBorderTable());
      return sb.toString();
   }

   public static void createXWidgetLayoutData(XWidgetData widData, XWidget xWidget, FormToolkit toolkit, Artifact art,
      XModifiedListener xModListener, boolean isEditable) {

      // If no tool tip, add global tool tip
      if (!Strings.isValid(xWidget.getToolTip())) {
         String description = "";
         if (widData.getXWidgetName().equals(XCommitManager.class.getSimpleName())) {
            description = XCommitManager.DESCRIPTION;
         }
         AttributeTypeToken type = null;
         if (widData.getStoreId() > 0) {
            type = AttributeTypeManager.getAttributeType(widData.getStoreId());
         }
         if (type == null && Strings.isValid(widData.getStoreName())) {
            type = AttributeTypeManager.getType(widData.getStoreName());
         }
         if (type != null && Strings.isValid(type.getDescription())) {
            description = type.getDescription();
         }
         if (Strings.isValid(description)) {
            xWidget.setToolTip(description);
            widData.setToolTip(description);
         }
      }
      // Store workAttr in control for use by help
      if (xWidget.getControl() != null) {
         xWidget.getControl().setData(widData);
      }

   }

   public static void generateLayoutDatas(AbstractWorkflowArtifact sma, Collection<LayoutItem> layoutItems,
      SwtXWidgetRenderer swtXWidgetRenderer) {
      // Add static XWidgetDatas to statePage
      for (LayoutItem stateItem : layoutItems) {
         if (stateItem instanceof WidgetDefinition) {
            processWidgetDefinition((WidgetDefinition) stateItem, sma, swtXWidgetRenderer);
         } else if (stateItem instanceof CompositeLayoutItem) {
            processComposite((CompositeLayoutItem) stateItem, sma, swtXWidgetRenderer);
         }
      }
      for (XWidgetData widData : swtXWidgetRenderer.getXWidgetDatas()) {
         swtXWidgetRenderer.setupXWidget(widData, false);
      }
   }

   public static void processComposite(CompositeLayoutItem compositeLayoutItem, AbstractWorkflowArtifact sma,
      SwtXWidgetRenderer swtXWidgetRenderer) {
      boolean firstWidget = true;

      // Group Comp is stand-alone renderer item
      boolean inGroupComp = false;
      if (compositeLayoutItem.isGroupComposite()) {
         inGroupComp = true;
         XWidgetData widData = new XWidgetData();
         widData.setName(compositeLayoutItem.getName());
         widData.setBeginGroupComposite(compositeLayoutItem.getNumColumns());
         swtXWidgetRenderer.addXWidgetData(widData);

      }

      List<LayoutItem> stateItems = compositeLayoutItem.getLayoutItems();
      for (int x = 0; x < stateItems.size(); x++) {
         boolean lastWidget = x == stateItems.size() - 1;
         LayoutItem stateItem = stateItems.get(x);
         if (stateItem instanceof WidgetDefinition) {
            XWidgetData widData = processWidgetDefinition((WidgetDefinition) stateItem, sma, swtXWidgetRenderer);
            if (firstWidget) {
               if (compositeLayoutItem.getNumColumns() > 0) {
                  if (!compositeLayoutItem.isGroupComposite()) {
                     widData.setBeginComposite(compositeLayoutItem.getNumColumns());
                  }
               }
            }
            if (lastWidget) {
               widData.setEndComposite(true);
            }
         } else if (stateItem instanceof CompositeLayoutItem) {
            CompositeLayoutItem compLayoutItem = (CompositeLayoutItem) stateItem;
            processComposite(compLayoutItem, sma, swtXWidgetRenderer);
         }
         firstWidget = false;
      }

      if (inGroupComp) {
         XWidgetData widData = new XWidgetData();
         widData.setEndGroupComposite(true);
         swtXWidgetRenderer.addXWidgetData(widData);
      }

   }

   /**
    * TODO This will eventually go away and ATS pages will be generated straight from WidgetDefinitions.
    */
   public static XWidgetData processWidgetDefinition(WidgetDefinition widgetDef, AbstractWorkflowArtifact sma,
      SwtXWidgetRenderer swtXWidgetRenderer) {
      XWidgetData widData = null;
      try {
         widData = new XWidgetData();
         widData.setDefaultValue(widgetDef.getDefaultValue());
         widData.setHeight(widgetDef.getHeight());
         if (widgetDef.getAttributeType() != null) {
            widData.setStoreName(widgetDef.getAttributeType().getName());
            widData.setStoreId(widgetDef.getAttributeType().getId());
            widData.setAttributeType(widgetDef.getAttributeType());
         }
         if (widgetDef.getAttributeType2() != null) {
            widData.setStoreName(widgetDef.getAttributeType().getName());
            widData.setStoreId(widgetDef.getAttributeType().getId());
            widData.setAttributeType2(widgetDef.getAttributeType2());
         }
         widData.setWidgetHints(widgetDef.getWidgetHints());
         widData.setEnumeratedArt(widgetDef.getEnumeratedArt());
         widData.getParameters().putAll(widgetDef.getParameters());
         widData.setRelationTypeSide(widgetDef.getRelationTypeSide());
         widData.setOseeImage(widgetDef.getOseeImage());
         widData.setEnumeratedArt(widgetDef.getEnumeratedArt());
         widData.setComputedCharacteristic(widgetDef.getComputedCharacteristic());
         widData.setToolTip(widgetDef.getToolTip());
         widData.setId(widgetDef.getName());
         widData.setXWidgetName(widgetDef.getXWidgetName());
         widData.setArtifact(sma);
         widData.setName(widgetDef.getName());
         widData.setObject(widgetDef);
         if (widgetDef.is(WidgetOption.MULTI_SELECT)) {
            widData.getXOptionHandler().add(XOption.MULTI_SELECT);
         } else if (widgetDef.is(WidgetOption.SINGLE_SELECT)) {
            widData.getXOptionHandler().add(XOption.SINGLE_SELECT);
         }
         if (widgetDef.is(WidgetOption.RFT)) {
            widData.getXOptionHandler().add(XOption.REQUIRED);
         } else if (widgetDef.is(WidgetOption.RFC)) {
            widData.getXOptionHandler().add(XOption.REQUIRED_FOR_COMPLETION);
         }
         if (widgetDef.is(WidgetOption.FILL_HORZ)) {
            widData.getXOptionHandler().add(XOption.FILL_HORIZONTALLY);
         } else if (widgetDef.is(WidgetOption.FILL_VERT)) {
            widData.getXOptionHandler().add(XOption.FILL_VERTICALLY);
         }
         for (WidgetOption widgetOpt : widgetDef.getOptions().getXOptions()) {
            XOption option = null;
            try {
               if (getWidOptToXOptionMap().containsKey(widgetOpt)) {
                  option = getWidOptToXOptionMap().get(widgetOpt);
               } else {
                  option = XOption.valueOf(widgetOpt.name());
               }
            } catch (IllegalArgumentException ex) {
               // do nothing
            }
            if (option != null) {
               widData.getXOptionHandler().add(option);
            }
         }
         widData.setConditions(widgetDef.getConditions());
         widData.setUserGroup(widgetDef.getUserGroup());
         swtXWidgetRenderer.addXWidgetData(widData);
      } catch (Exception ex) {
         widData = new XWidgetData();
         widData.setId(Lib.generateArtifactIdAsInt().toString());
         widData.setXWidgetName("XLabel");
         widData.setName("Error: " + widgetDef.getName() + " (double-click to view error)");
         widData.setToolTip("Double-click to see error.");
         widData.setDoubleClickText(Lib.exceptionToString(ex));
         OseeLog.logf(StateXWidgetPage.class, Level.SEVERE, ex, "Exception processing widget [%s]",
            widgetDef.getName());
         widData.setObject(widgetDef);
         swtXWidgetRenderer.addXWidgetData(widData);
      }
      return widData;
   }

   public static Map<WidgetOption, XOption> getWidOptToXOptionMap() {
      if (widOptToXOptionMap.isEmpty()) {
         widOptToXOptionMap.put(WidgetOption.SAVE, XOption.AUTO_SAVE);
         widOptToXOptionMap.put(WidgetOption.NOT_SAVE, XOption.NOT_AUTO_SAVE);
         widOptToXOptionMap.put(WidgetOption.FILL_HORZ, XOption.FILL_HORIZONTALLY);
         widOptToXOptionMap.put(WidgetOption.FILL_VERT, XOption.FILL_VERTICALLY);
         widOptToXOptionMap.put(WidgetOption.RFC, XOption.REQUIRED_FOR_COMPLETION);
         widOptToXOptionMap.put(WidgetOption.NOT_RFC, XOption.NOT_REQUIRED_FOR_COMPLETION);
      }
      return widOptToXOptionMap;
   }

}
