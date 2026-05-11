/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
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

   public static List<XWidgetData> extractWidgetDatas(String xml) {
      List<XWidgetData> data = Collections.emptyList();
      try {
         Document document = Jaxp.readXmlDocument(xml);
         Element rootElement = document.getDocumentElement();
         data = extractXWidgetDatas(rootElement);
      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
      return data;
   }

   public static XWidgetData extractWidgetData(String xml) {
      XWidgetData widData = null;
      try {
         Document document = Jaxp.readXmlDocument(xml);
         Element rootElement = document.getDocumentElement();
         widData = extractXWidgetData(rootElement);
      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
      return widData;
   }

   public static List<XWidgetData> extractXWidgetDatas(Element xWidgets) {
      NodeList widgets = xWidgets.getElementsByTagName(SwtXWidgetRenderer.XWIDGET);
      List<XWidgetData> widDatas = new ArrayList<>(widgets.getLength());

      for (int i = 0; i < widgets.getLength(); i++) {
         widDatas.add(extractXWidgetData((Element) widgets.item(i)));
      }
      return widDatas;
   }

   public static String toXml(XWidgetData widData) {
      String xmlData = null;
      try {
         Document doc = Jaxp.newDocumentNamespaceAware();
         Element element = doc.createElement(SwtXWidgetRenderer.XWIDGET);
         element.setAttribute("displayName", widData.getName());
         element.setAttribute("storageName", widData.getStoreName());
         element.setAttribute("toolTip", widData.getToolTip());
         element.setAttribute("id", widData.getId());
         element.setAttribute("xwidgetType", widData.getXWidgetName());
         element.setAttribute("defaultValue", widData.getDefaultValue());

         for (XOption xOption : widData.getXOptionHandler().getXOptions()) {
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

   private static XWidgetData extractXWidgetData(Element widget) {
      XWidgetData widData = new XWidgetData();

      // Loop through attributes to ensure all are valid and processed
      NamedNodeMap attributes = widget.getAttributes();
      for (int x = 0; x < attributes.getLength(); x++) {
         Node node = attributes.item(x);
         String nodeName = node.getNodeName();
         if (nodeName.equals("displayName")) {
            String displayNamevalue = node.getNodeValue();
            widData.setName(displayNamevalue);
            if (!Strings.isValid(widData.getStoreName())) {
               widData.setStoreName(displayNamevalue);
            }
         } else if (nodeName.equals("storageName")) {
            String storeNameValue = node.getNodeValue();
            widData.setStoreName(storeNameValue);
         } else if (nodeName.equals("toolTip")) {
            widData.setToolTip(node.getNodeValue());
         } else if (nodeName.equals("id")) {
            widData.setId(node.getNodeValue());
         } else if (nodeName.equals("horizontalLabel")) {
            widData.getXOptionHandler().add(
               Boolean.parseBoolean(node.getNodeValue()) ? XOption.HORIZONTAL_LABEL : XOption.NONE);
         } else if (nodeName.equals("labelAfter")) {
            widData.getXOptionHandler().add(
               Boolean.parseBoolean(node.getNodeValue()) ? XOption.LABEL_AFTER : XOption.NONE);
         } else if (nodeName.equals("required")) {
            widData.getXOptionHandler().add(
               Boolean.parseBoolean(node.getNodeValue()) ? XOption.REQUIRED : XOption.NONE);
         } else if (nodeName.equals("sorted")) {
            widData.getXOptionHandler().add(Boolean.parseBoolean(node.getNodeValue()) ? XOption.SORTED : XOption.NONE);
         } else if (nodeName.equals("displayLabel")) {
            widData.getXOptionHandler().add(
               Boolean.parseBoolean(node.getNodeValue()) ? XOption.NONE : XOption.NO_LABEL);
         } else if (nodeName.equals("beginComposite")) {
            widData.setBeginComposite(Integer.parseInt(node.getNodeValue()));
         } else if (nodeName.equals("beginGroupComposite")) {
            widData.setBeginGroupComposite(Integer.parseInt(node.getNodeValue()));
         } else if (nodeName.equals("endGroupComposite")) {
            widData.setEndGroupComposite(Boolean.parseBoolean(node.getNodeValue()));
         } else if (nodeName.equals("endComposite")) {
            widData.setEndComposite(Boolean.parseBoolean(node.getNodeValue()));
         } else if (nodeName.equals("editable")) {
            widData.getXOptionHandler().add(
               Boolean.parseBoolean(node.getNodeValue()) ? XOption.EDITABLE : XOption.NONE);
         } else if (nodeName.equals("xwidgetType")) {
            widData.setXWidgetName(node.getNodeValue());
         } else if (nodeName.equals("multiSelect")) {
            widData.getXOptionHandler().add(
               Boolean.parseBoolean(node.getNodeValue()) ? XOption.MULTI_SELECT : XOption.NONE);
         } else if (nodeName.equals("singleSelect")) {
            widData.getXOptionHandler().add(
               Boolean.parseBoolean(node.getNodeValue()) ? XOption.SINGLE_SELECT : XOption.NONE);
         } else if (nodeName.equals("fill")) {
            String value = node.getNodeValue();
            if (value.equalsIgnoreCase("Horizontally")) {
               widData.getXOptionHandler().add(XOption.FILL_HORIZONTALLY);
            } else if (value.equalsIgnoreCase("Vertically")) {
               widData.getXOptionHandler().add(XOption.FILL_VERTICALLY);
            } else {
               OseeLog.log(Activator.class, Level.WARNING,
                  new IllegalArgumentException("Unknown Fill Value \"" + value + "\""));
            }
         } else if (nodeName.equals("height")) {
            widData.setHeight(Integer.parseInt(node.getNodeValue()));
         } else if (nodeName.equals("align")) {
            String value = node.getNodeValue();
            if (value.equalsIgnoreCase("Left")) {
               widData.getXOptionHandler().add(XOption.ALIGN_LEFT);
            } else if (value.equalsIgnoreCase("Right")) {
               widData.getXOptionHandler().add(XOption.ALIGN_RIGHT);
            } else if (value.equalsIgnoreCase("Center")) {
               widData.getXOptionHandler().add(XOption.ALIGN_CENTER);
            } else {
               OseeLog.log(Activator.class, Level.WARNING,
                  new IllegalArgumentException("Unknown Align Value \"" + value + "\""));
            }
         } else if (nodeName.equals("defaultValue")) {
            widData.setDefaultValue(node.getNodeValue());
         } else if (nodeName.equals("keyedBranch")) {
            widData.setKeyedBranchName(node.getNodeValue());
         } else {
            OseeLog.log(Activator.class, Level.SEVERE,
               new OseeArgumentException("Unsupported XWidget attribute \"" + nodeName + "\""));
         }
      }

      return widData;
   }
}
