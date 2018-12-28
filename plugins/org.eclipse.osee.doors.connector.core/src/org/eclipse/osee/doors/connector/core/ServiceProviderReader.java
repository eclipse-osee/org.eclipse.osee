/*
 * Copyright (c) 2012 Robert Bosch Engineering and Business Solutions Ltd India. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.osee.doors.connector.core;

import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Parser class to parse the the ServiceProvider URL to get the Query Base
 *
 * @author Chandan Bandemutt
 */
public class ServiceProviderReader implements IDoorsArtifactParser {

   /**
    * {@inheritDoc}
    */
   @Override
   public DoorsArtifact parse(DoorsArtifact provider) throws Exception {
      String path = provider.getPath();

      DoorsOSLCConnector doors = new DoorsOSLCConnector();
      String catalogResponse = doors.getCatalogResponse(replace(path), null);

      XPath xpath = XPathFactory.newInstance().newXPath();

      DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
      domFactory.setNamespaceAware(true); // never forget this!
      DocumentBuilder builder = domFactory.newDocumentBuilder();

      InputSource is = new InputSource(new StringReader(catalogResponse));
      Document doc = builder.parse(is);

      Object titleResult = xpath.evaluate("//*[local-name()='QueryCapability']", doc, XPathConstants.NODESET);
      NodeList titleNodes = (NodeList) titleResult;
      if (titleNodes.getLength() > 0) {
         for (int i = 0; i < titleNodes.getLength(); i++) {
            Node node = titleNodes.item(i);

            NodeList childNodes = node.getChildNodes();
            for (int j = 0; j < childNodes.getLength(); j++) {
               Node item = childNodes.item(j);
               if (item.getLocalName() != null && item.getLocalName().equalsIgnoreCase("label")) {
                  provider.setName(item.getTextContent());
               }
            }
         }
      }

      Object result2 =
         xpath.evaluate("//*[local-name()='selectionDialog']//*[local-name()='Dialog']//*[local-name()='dialog']", doc,
            XPathConstants.NODESET);
      NodeList rootNodes1 = (NodeList) result2;
      if (rootNodes1.getLength() > 0) {
         for (int i = 0; i < rootNodes1.getLength();) {
            Node node = rootNodes1.item(i);

            NamedNodeMap attributes = node.getAttributes();

            for (int k1 = 0; k1 < attributes.getLength(); k1++) {
               Node item1 = attributes.item(k1);
               String nodeValue = item1.getNodeValue();
               provider.setSelectionDialogUrl(nodeValue);
            }
            break;
         }
      }

      Object result1 = xpath.evaluate("//*[local-name()='queryBase']", doc, XPathConstants.NODESET);
      NodeList rootNodes = (NodeList) result1;
      if (rootNodes.getLength() > 0) {
         for (int i = 0; i < rootNodes.getLength(); i++) {
            Node node = rootNodes.item(i);

            NamedNodeMap attributes = node.getAttributes();

            for (int k1 = 0; k1 < attributes.getLength(); k1++) {
               Node item1 = attributes.item(k1);
               String nodeValue = item1.getNodeValue();
               QueryReader queryReader = new QueryReader();
               QueryCapabilities queryObjIn = new QueryCapabilities();
               queryObjIn.setPath(replace(nodeValue));
               DoorsArtifact queryObj = queryReader.parse(queryObjIn);
               provider.addChild(queryObj);
            }
         }
      }
      return provider;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String replace(final String url) {
      return url.toLowerCase();
   }
}
