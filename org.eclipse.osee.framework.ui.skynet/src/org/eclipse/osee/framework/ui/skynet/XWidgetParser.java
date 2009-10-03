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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.widgets.XOption;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayout;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Ryan D. Brooks
 */
public class XWidgetParser {

   public static String EMPTY_WIDGETS = "<xWidgets><XWidget xwidgetType=\"XLabel\" displayName=\" \" /></xWidgets>";

   public static List<DynamicXWidgetLayoutData> extractWorkAttributes(DynamicXWidgetLayout dynamicXWidgetLayout, String xml) throws ParserConfigurationException, SAXException, IOException {
      Document document = Jaxp.readXmlDocument(xml);
      Element rootElement = document.getDocumentElement();

      return extractlayoutDatas(dynamicXWidgetLayout, rootElement);
   }

   public static DynamicXWidgetLayoutData extractlayoutData(DynamicXWidgetLayout dynamicXWidgetLayout, String xml) throws ParserConfigurationException, SAXException, IOException {
      Document document = Jaxp.readXmlDocument(xml);
      Element rootElement = document.getDocumentElement();
      return extractWorkAttribute(dynamicXWidgetLayout, rootElement);
   }

   public static List<DynamicXWidgetLayoutData> extractlayoutDatas(DynamicXWidgetLayout dynamicXWidgetLayout, Element xWidgets) throws ParserConfigurationException, SAXException, IOException {
      NodeList widgets = xWidgets.getElementsByTagName(DynamicXWidgetLayout.XWIDGET);
      List<DynamicXWidgetLayoutData> layoutDatas = new ArrayList<DynamicXWidgetLayoutData>(widgets.getLength());

      for (int i = 0; i < widgets.getLength(); i++) {
         layoutDatas.add(extractWorkAttribute(dynamicXWidgetLayout, (Element) widgets.item(i)));
      }
      return layoutDatas;
   }

   public static String toXml(DynamicXWidgetLayoutData data) throws OseeCoreException, ParserConfigurationException, TransformerException {
      Document doc = Jaxp.newDocument();
      Element element = doc.createElement(DynamicXWidgetLayout.XWIDGET);
      element.setAttribute("displayName", data.getName());
      element.setAttribute("storageName", data.getStorageName());
      element.setAttribute("toolTip", data.getToolTip());
      element.setAttribute("id", data.getId());
      element.setAttribute("xwidgetType", data.getXWidgetName());
      element.setAttribute("defaultValue", data.getDefaultValue());
      for (XOption xOption : data.getXOptionHandler().getXOptions()) {
         if (xOption == XOption.ALIGN_CENTER)
            element.setAttribute("align", "Center");
         else if (xOption == XOption.ALIGN_LEFT)
            element.setAttribute("align", "Left");
         else if (xOption == XOption.ALIGN_RIGHT)
            element.setAttribute("align", "Right");
         else if (xOption == XOption.EDITABLE)
            element.setAttribute("editable", "true");
         else if (xOption == XOption.BEGIN_COMPOSITE_4)
            element.setAttribute("beginComposite", "4");
         else if (xOption == XOption.BEGIN_COMPOSITE_6)
            element.setAttribute("beginComposite", "6");
         else if (xOption == XOption.BEGIN_COMPOSITE_8)
            element.setAttribute("beginComposite", "8");
         else if (xOption == XOption.BEGIN_COMPOSITE_10)
            element.setAttribute("beginComposite", "10");
         else if (xOption == XOption.END_COMPOSITE)
            element.setAttribute("endComposite", "true");
         else if (xOption == XOption.NOT_EDITABLE)
            element.setAttribute("editable", "false");
         else if (xOption == XOption.ENABLED)
            element.setAttribute("enabled", "true");
         else if (xOption == XOption.NOT_ENABLED)
            element.setAttribute("enabled", "false");
         else if (xOption == XOption.REQUIRED)
            element.setAttribute("required", "true");
         else if (xOption == XOption.NOT_REQUIRED)
            element.setAttribute("required", "false");
         else if (xOption == XOption.FILL_HORIZONTALLY)
            element.setAttribute("fill", "Horizontally");
         else if (xOption == XOption.FILL_VERTICALLY)
            element.setAttribute("fill", "Vertically");
         else if (xOption == XOption.HORIZONTAL_LABEL)
            element.setAttribute("horizontalLabel", "true");
         else if (xOption == XOption.VERTICAL_LABEL)
            element.setAttribute("horizontalLabel", "false");
         else if (xOption == XOption.LABEL_AFTER)
            element.setAttribute("labelAfter", "true");
         else if (xOption == XOption.LABEL_BEFORE)
            element.setAttribute("labelAfter", "false");
         else if (xOption == XOption.MULTI_SELECT)
            element.setAttribute("multiSelect", "true");
         else if (xOption == XOption.NONE)
            // do nothing
            ;
         else
            throw new OseeArgumentException("Unhandled xOption \"" + xOption + "\"");
      }
      doc.appendChild(element);
      return Jaxp.getDocumentXml(doc);
   }

   private static DynamicXWidgetLayoutData extractWorkAttribute(DynamicXWidgetLayout dynamicXWidgetLayout, Element widget) {
      DynamicXWidgetLayoutData dynamicXWidgetLayoutData = new DynamicXWidgetLayoutData(dynamicXWidgetLayout);

      // Loop through attributes to ensure all are valid and processed
      NamedNodeMap attributes = widget.getAttributes();
      for (int x = 0; x < attributes.getLength(); x++) {
         Node node = attributes.item(x);
         String nodeName = node.getNodeName();
         if (nodeName.equals("displayName")) {
            dynamicXWidgetLayoutData.setName(node.getNodeValue());
            if (dynamicXWidgetLayoutData.getStorageName().equals("")) dynamicXWidgetLayoutData.setStorageName(node.getNodeValue());
         } else if (nodeName.equals("storageName")) {
            dynamicXWidgetLayoutData.setStorageName(node.getNodeValue());
            if (dynamicXWidgetLayoutData.getName().equals("")) dynamicXWidgetLayoutData.setName(node.getNodeValue());
         } else if (nodeName.equals("toolTip"))
            dynamicXWidgetLayoutData.setToolTip(node.getNodeValue());
         else if (nodeName.equals("id"))
            dynamicXWidgetLayoutData.setId(node.getNodeValue());
         else if (nodeName.equals("horizontalLabel"))
            dynamicXWidgetLayoutData.getXOptionHandler().add(
                  Boolean.parseBoolean((node.getNodeValue())) ? XOption.HORIZONTAL_LABEL : XOption.NONE);
         else if (nodeName.equals("labelAfter"))
            dynamicXWidgetLayoutData.getXOptionHandler().add(
                  Boolean.parseBoolean((node.getNodeValue())) ? XOption.LABEL_AFTER : XOption.NONE);
         else if (nodeName.equals("required"))
            dynamicXWidgetLayoutData.getXOptionHandler().add(
                  Boolean.parseBoolean(node.getNodeValue()) ? XOption.REQUIRED : XOption.NONE);
         else if (nodeName.equals("beginComposite"))
            dynamicXWidgetLayoutData.setBeginComposite(Integer.parseInt(node.getNodeValue()));
         else if (nodeName.equals("endComposite"))
            dynamicXWidgetLayoutData.setEndComposite(Boolean.parseBoolean((node.getNodeValue())));
         else if (nodeName.equals("editable"))
            dynamicXWidgetLayoutData.getXOptionHandler().add(
                  Boolean.parseBoolean(node.getNodeValue()) ? XOption.EDITABLE : XOption.NONE);
         else if (nodeName.equals("xwidgetType")) {
            dynamicXWidgetLayoutData.setXWidgetName(node.getNodeValue());
         } else if (nodeName.equals("multiSelect")) {
            dynamicXWidgetLayoutData.getXOptionHandler().add(
                  Boolean.parseBoolean(node.getNodeValue()) ? XOption.MULTI_SELECT : XOption.NONE);
         } else if (nodeName.equals("fill")) {
            String value = node.getNodeValue();
            if (value.equalsIgnoreCase("Horizontally"))
               dynamicXWidgetLayoutData.getXOptionHandler().add(XOption.FILL_HORIZONTALLY);
            else if (value.equalsIgnoreCase("Vertically"))
               dynamicXWidgetLayoutData.getXOptionHandler().add(XOption.FILL_VERTICALLY);
            else
               OseeLog.log(SkynetGuiPlugin.class, Level.WARNING, new IllegalArgumentException(
                     "Unknown Fill Value \"" + value + "\""));
         } else if (nodeName.equals("height"))
            dynamicXWidgetLayoutData.setHeight(Integer.parseInt(node.getNodeValue()));
         else if (nodeName.equals("align")) {
            String value = node.getNodeValue();
            if (value.equalsIgnoreCase("Left"))
               dynamicXWidgetLayoutData.getXOptionHandler().add(XOption.ALIGN_LEFT);
            else if (value.equalsIgnoreCase("Right"))
               dynamicXWidgetLayoutData.getXOptionHandler().add(XOption.ALIGN_RIGHT);
            else if (value.equalsIgnoreCase("Center"))
               dynamicXWidgetLayoutData.getXOptionHandler().add(XOption.ALIGN_CENTER);
            else
               OseeLog.log(SkynetGuiPlugin.class, Level.WARNING, new IllegalArgumentException(
                     "Unknown Align Value \"" + value + "\""));
         } else if (nodeName.equals("defaultValue"))
            dynamicXWidgetLayoutData.setDefaultValue(node.getNodeValue());
         else if (nodeName.equals("keyedBranch"))
            dynamicXWidgetLayoutData.setKeyedBranchName(node.getNodeValue());
         else {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, new OseeArgumentException(
                  "Unsupported XWidget attribute \"" + nodeName + "\""));
         }
      }

      return dynamicXWidgetLayoutData;
   }
}
