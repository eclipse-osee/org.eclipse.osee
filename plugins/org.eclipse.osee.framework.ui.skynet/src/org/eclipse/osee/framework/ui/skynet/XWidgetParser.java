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

package org.eclipse.osee.framework.ui.skynet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.XOption;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetRendererItem;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Ryan D. Brooks
 */
public class XWidgetParser {

   public static final String EMPTY_WIDGETS =
      "<xWidgets><XWidget xwidgetType=\"XLabel\" displayName=\" \" /></xWidgets>";

   public static List<XWidgetRendererItem> extractWorkAttributes(SwtXWidgetRenderer dynamicXWidgetLayout, String xml)  {
      List<XWidgetRendererItem> data = Collections.emptyList();
      try {
         Document document = Jaxp.readXmlDocument(xml);
         Element rootElement = document.getDocumentElement();
         data = extractlayoutDatas(dynamicXWidgetLayout, rootElement);
      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
      return data;
   }

   public static XWidgetRendererItem extractlayoutData(SwtXWidgetRenderer dynamicXWidgetLayout, String xml)  {
      XWidgetRendererItem data = null;
      try {
         Document document = Jaxp.readXmlDocument(xml);
         Element rootElement = document.getDocumentElement();
         data = extractWorkAttribute(dynamicXWidgetLayout, rootElement);
      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
      return data;
   }

   public static List<XWidgetRendererItem> extractlayoutDatas(SwtXWidgetRenderer dynamicXWidgetLayout, Element xWidgets) {
      NodeList widgets = xWidgets.getElementsByTagName(SwtXWidgetRenderer.XWIDGET);
      List<XWidgetRendererItem> layoutDatas = new ArrayList<>(widgets.getLength());

      for (int i = 0; i < widgets.getLength(); i++) {
         layoutDatas.add(extractWorkAttribute(dynamicXWidgetLayout, (Element) widgets.item(i)));
      }
      return layoutDatas;
   }

   public static String toXml(XWidgetRendererItem data)  {
      String xmlData = null;
      try {
         Document doc = Jaxp.newDocumentNamespaceAware();
         Element element = doc.createElement(SwtXWidgetRenderer.XWIDGET);
         element.setAttribute("displayName", data.getName());
         element.setAttribute("storageName", data.getStoreName());
         element.setAttribute("toolTip", data.getToolTip());
         element.setAttribute("id", data.getId());
         element.setAttribute("xwidgetType", data.getXWidgetName());
         element.setAttribute("defaultValue", data.getDefaultValue());

         for (XOption xOption : data.getXOptionHandler().getXOptions()) {
            if (Strings.isValid(xOption.keyword, xOption.value)) {
               element.setAttribute(xOption.keyword, xOption.value);
            }
         }
         doc.appendChild(element);
         xmlData = Jaxp.getDocumentXml(doc);
      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
      return xmlData;
   }

   private static XWidgetRendererItem extractWorkAttribute(SwtXWidgetRenderer dynamicXWidgetLayout, Element widget) {
      XWidgetRendererItem dynamicXWidgetLayoutData = new XWidgetRendererItem(dynamicXWidgetLayout);

      // Loop through attributes to ensure all are valid and processed
      NamedNodeMap attributes = widget.getAttributes();
      for (int x = 0; x < attributes.getLength(); x++) {
         Node node = attributes.item(x);
         String nodeName = node.getNodeName();
         if (nodeName.equals("displayName")) {
            String displayNamevalue = node.getNodeValue();
            dynamicXWidgetLayoutData.setName(displayNamevalue);
            if (!Strings.isValid(dynamicXWidgetLayoutData.getStoreName())) {
               dynamicXWidgetLayoutData.setStoreName(displayNamevalue);
            }
         } else if (nodeName.equals("storageName")) {
            String storeNameValue = node.getNodeValue();
            dynamicXWidgetLayoutData.setStoreName(storeNameValue);
         } else if (nodeName.equals("toolTip")) {
            dynamicXWidgetLayoutData.setToolTip(node.getNodeValue());
         } else if (nodeName.equals("id")) {
            dynamicXWidgetLayoutData.setId(node.getNodeValue());
         } else if (nodeName.equals("horizontalLabel")) {
            dynamicXWidgetLayoutData.getXOptionHandler().add(
               Boolean.parseBoolean(node.getNodeValue()) ? XOption.HORIZONTAL_LABEL : XOption.NONE);
         } else if (nodeName.equals("labelAfter")) {
            dynamicXWidgetLayoutData.getXOptionHandler().add(
               Boolean.parseBoolean(node.getNodeValue()) ? XOption.LABEL_AFTER : XOption.NONE);
         } else if (nodeName.equals("required")) {
            dynamicXWidgetLayoutData.getXOptionHandler().add(
               Boolean.parseBoolean(node.getNodeValue()) ? XOption.REQUIRED : XOption.NONE);
         } else if (nodeName.equals("sorted")) {
            dynamicXWidgetLayoutData.getXOptionHandler().add(
               Boolean.parseBoolean(node.getNodeValue()) ? XOption.SORTED : XOption.NONE);
         } else if (nodeName.equals("displayLabel")) {
            dynamicXWidgetLayoutData.getXOptionHandler().add(
               Boolean.parseBoolean(node.getNodeValue()) ? XOption.NONE : XOption.NO_LABEL);
         } else if (nodeName.equals("beginComposite")) {
            dynamicXWidgetLayoutData.setBeginComposite(Integer.parseInt(node.getNodeValue()));
         } else if (nodeName.equals("beginGroupComposite")) {
            dynamicXWidgetLayoutData.setBeginGroupComposite(Integer.parseInt(node.getNodeValue()));
         } else if (nodeName.equals("endGroupComposite")) {
            dynamicXWidgetLayoutData.setEndGroupComposite(Boolean.parseBoolean(node.getNodeValue()));
         } else if (nodeName.equals("endComposite")) {
            dynamicXWidgetLayoutData.setEndComposite(Boolean.parseBoolean(node.getNodeValue()));
         } else if (nodeName.equals("editable")) {
            dynamicXWidgetLayoutData.getXOptionHandler().add(
               Boolean.parseBoolean(node.getNodeValue()) ? XOption.EDITABLE : XOption.NONE);
         } else if (nodeName.equals("xwidgetType")) {
            dynamicXWidgetLayoutData.setXWidgetName(node.getNodeValue());
         } else if (nodeName.equals("multiSelect")) {
            dynamicXWidgetLayoutData.getXOptionHandler().add(
               Boolean.parseBoolean(node.getNodeValue()) ? XOption.MULTI_SELECT : XOption.NONE);
         } else if (nodeName.equals("fill")) {
            String value = node.getNodeValue();
            if (value.equalsIgnoreCase("Horizontally")) {
               dynamicXWidgetLayoutData.getXOptionHandler().add(XOption.FILL_HORIZONTALLY);
            } else if (value.equalsIgnoreCase("Vertically")) {
               dynamicXWidgetLayoutData.getXOptionHandler().add(XOption.FILL_VERTICALLY);
            } else {
               OseeLog.log(Activator.class, Level.WARNING,
                  new IllegalArgumentException("Unknown Fill Value \"" + value + "\""));
            }
         } else if (nodeName.equals("height")) {
            dynamicXWidgetLayoutData.setHeight(Integer.parseInt(node.getNodeValue()));
         } else if (nodeName.equals("align")) {
            String value = node.getNodeValue();
            if (value.equalsIgnoreCase("Left")) {
               dynamicXWidgetLayoutData.getXOptionHandler().add(XOption.ALIGN_LEFT);
            } else if (value.equalsIgnoreCase("Right")) {
               dynamicXWidgetLayoutData.getXOptionHandler().add(XOption.ALIGN_RIGHT);
            } else if (value.equalsIgnoreCase("Center")) {
               dynamicXWidgetLayoutData.getXOptionHandler().add(XOption.ALIGN_CENTER);
            } else {
               OseeLog.log(Activator.class, Level.WARNING,
                  new IllegalArgumentException("Unknown Align Value \"" + value + "\""));
            }
         } else if (nodeName.equals("defaultValue")) {
            dynamicXWidgetLayoutData.setDefaultValue(node.getNodeValue());
         } else if (nodeName.equals("keyedBranch")) {
            dynamicXWidgetLayoutData.setKeyedBranchName(node.getNodeValue());
         } else {
            OseeLog.log(Activator.class, Level.SEVERE,
               new OseeArgumentException("Unsupported XWidget attribute \"" + nodeName + "\""));
         }
      }

      return dynamicXWidgetLayoutData;
   }
}
