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
package org.eclipse.osee.framework.manager.servlet.ats;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.resource.management.IResource;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Roberto E. Escobar
 */
public final class XmlUtil {
   //   private static final Map<String, XPathExpression> expressions = new HashMap<String, XPathExpression>();

   private XmlUtil() {
   }

   //   public static XPathExpression getExpression(XPath xPath, String xPathExpression) throws XPathExpressionException {
   //      XPathExpression toReturn = expressions.get(xPathExpression);
   //      if (toReturn == null) {
   //         toReturn = xPath.compile(xPathExpression);
   //         expressions.put(xPathExpression, toReturn);
   //      return toReturn;
   //   }

   public static XPath createXPath() {
      XPathFactory factory = XPathFactory.newInstance();
      return factory.newXPath();
   }

   public static Collection<Node> findInResource(IResource resource, String expression) throws OseeCoreException {
      try {
         Element element = XmlUtil.readXml(resource);
         XPath xPath = XmlUtil.createXPath();
         return XmlUtil.selectNodesViaXPath(xPath, element, expression);
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      return Collections.emptyList();
   }

   public static Element readXml(IResource resource) throws Exception {
      InputStream inputStream = null;
      try {
         inputStream = resource.getContent();
         return readXML(inputStream);
      } finally {
         Lib.close(inputStream);
      }
   }

   public static Element readXML(InputStream inputStream) throws Exception {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder documentBuilder = factory.newDocumentBuilder();
      Document document = documentBuilder.parse(inputStream);
      document.getDocumentElement().normalize();
      return document.getDocumentElement();
   }

   public static final List<Node> selectNodesViaXPath(XPath xPath, Node startingNode, String xPathExpression) throws XPathExpressionException {
      List<Node> data = new ArrayList<Node>();
      XPathExpression expression = xPath.compile(xPathExpression);
      Object result = expression.evaluate(startingNode, XPathConstants.NODESET);
      NodeList nodeList = (NodeList) result;
      for (int index = 0; index < nodeList.getLength(); index++) {
         data.add(nodeList.item(index));
      }
      return data;
   }

   public static void serialize(XMLStreamWriter writer, Collection<Node> nodes) throws XMLStreamException {
      for (Node node : nodes) {
         writeNode(writer, node);
      }
   }

   public static void serialize(XMLStreamWriter writer, NodeList nodes) throws XMLStreamException {
      for (int index = 0; index < nodes.getLength(); index++) {
         writeNode(writer, nodes.item(index));
      }
   }

   public static void writeNode(XMLStreamWriter writer, Node node) throws XMLStreamException {
      if (node instanceof Element) {
         Element element = (Element) node;

         String namespace = element.getNamespaceURI();
         String prefix = element.getPrefix();
         String name = element.getNodeName();
         if (Strings.isValid(name)) {
            if (prefix != null && namespace != null) {
               writer.writeStartElement(prefix, name, namespace);
            } else if (namespace != null) {
               writer.writeStartElement(namespace, name);
            } else {
               writer.writeStartElement(name);
            }

            if (node.hasAttributes()) {
               NamedNodeMap nodeMap = node.getAttributes();
               for (int index = 0; index < nodeMap.getLength(); index++) {
                  writeAttrNode(writer, nodeMap.item(index));
               }
            }

            if (node.hasChildNodes()) {
               serialize(writer, element.getChildNodes());
            }

            String text = Jaxp.getElementCharacterData(element, true);
            if (Strings.isValid(text)) {
               writer.writeCharacters(text);
            }
            writer.writeEndElement();
         }
      }
   }

   private static void writeAttrNode(XMLStreamWriter writer, Node node) throws XMLStreamException {
      if (node instanceof Attr) {
         Attr attrNode = (Attr) node;

         String namespace = attrNode.getNamespaceURI();
         String prefix = attrNode.getPrefix();
         String name = attrNode.getName();
         String value = attrNode.getValue();
         if (Strings.isValid(name) && Strings.isValid(value)) {
            if (prefix != null && namespace != null) {
               writer.writeAttribute(prefix, namespace, name, value);
            } else if (namespace != null) {
               writer.writeAttribute(namespace, name, value);
            } else {
               writer.writeAttribute(name, value);
            }
         }
      }
   }
}
