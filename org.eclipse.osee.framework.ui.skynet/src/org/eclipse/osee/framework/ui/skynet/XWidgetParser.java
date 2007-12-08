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
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayout;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData.Align;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData.Fill;
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

   public XWidgetParser() {
      super();
   }

   public List<DynamicXWidgetLayoutData> extractWorkAttributes(DynamicXWidgetLayout dynamicXWidgetLayout, String xml) throws ParserConfigurationException, SAXException, IOException {
      Document document = Jaxp.readXmlDocument(xml);
      Element rootElement = document.getDocumentElement();

      return extractlayoutDatas(dynamicXWidgetLayout, rootElement);
   }

   public List<DynamicXWidgetLayoutData> extractlayoutDatas(DynamicXWidgetLayout dynamicXWidgetLayout, Element xWidgets) throws ParserConfigurationException, SAXException, IOException {
      NodeList widgets = xWidgets.getElementsByTagName(DynamicXWidgetLayout.XWIDGET);
      List<DynamicXWidgetLayoutData> layoutDatas = new ArrayList<DynamicXWidgetLayoutData>(widgets.getLength());

      for (int i = 0; i < widgets.getLength(); i++) {
         layoutDatas.add(extractWorkAttribute(dynamicXWidgetLayout, (Element) widgets.item(i)));
      }
      return layoutDatas;
   }

   private DynamicXWidgetLayoutData extractWorkAttribute(DynamicXWidgetLayout dynamicXWidgetLayout, Element widget) {
      DynamicXWidgetLayoutData dynamicXWidgetLayoutData = new DynamicXWidgetLayoutData(dynamicXWidgetLayout);

      // Loop through attributes to ensure all are valid and processed
      NamedNodeMap attributes = widget.getAttributes();
      for (int x = 0; x < attributes.getLength(); x++) {
         Node node = attributes.item(x);
         String nodeName = node.getNodeName();
         if (nodeName.equals("displayName")) {
            dynamicXWidgetLayoutData.setName(node.getNodeValue());
            if (dynamicXWidgetLayoutData.getLayoutName().equals("")) dynamicXWidgetLayoutData.setlayoutName(node.getNodeValue());
         } else if (nodeName.equals("storageName")) {
            dynamicXWidgetLayoutData.setlayoutName(node.getNodeValue());
            if (dynamicXWidgetLayoutData.getName().equals("")) dynamicXWidgetLayoutData.setName(node.getNodeValue());
         } else if (nodeName.equals("toolTip"))
            dynamicXWidgetLayoutData.setToolTip(node.getNodeValue());
         else if (nodeName.equals("helpContextId")) {
            // Not used anymore
         } else if (nodeName.equals("helpPluginId")) {
            // Not used anymore
         } else if (nodeName.equals("beginComposite"))
            dynamicXWidgetLayoutData.setBeginComposite(Integer.parseInt(node.getNodeValue()));
         else if (nodeName.equals("endComposite"))
            dynamicXWidgetLayoutData.setEndComposite(Boolean.parseBoolean((node.getNodeValue())));
         else if (nodeName.equals("horizontalLabel"))
            dynamicXWidgetLayoutData.setHorizontalLabel(Boolean.parseBoolean((node.getNodeValue())));
         else if (nodeName.equals("labelAfter"))
            dynamicXWidgetLayoutData.setLabelAfter(Boolean.parseBoolean((node.getNodeValue())));
         else if (nodeName.equals("required"))
            dynamicXWidgetLayoutData.setRequired(Boolean.parseBoolean(node.getNodeValue()));
         else if (nodeName.equals("xwidgetType"))
            dynamicXWidgetLayoutData.setXWidgetName(node.getNodeValue());
         else if (nodeName.equals("fill"))
            dynamicXWidgetLayoutData.setFill(Fill.valueOf(node.getNodeValue()));
         else if (nodeName.equals("height"))
            dynamicXWidgetLayoutData.setHeight(Integer.parseInt(node.getNodeValue()));
         else if (nodeName.equals("align"))
            dynamicXWidgetLayoutData.setAlign(Align.valueOf(node.getNodeValue()));
         else {
            OSEELog.logException(SkynetGuiPlugin.class, new Exception(
                  "Unsupported XWidget attribute \"" + nodeName + "\""), false);
         }
      }

      return dynamicXWidgetLayoutData;
   }
}
