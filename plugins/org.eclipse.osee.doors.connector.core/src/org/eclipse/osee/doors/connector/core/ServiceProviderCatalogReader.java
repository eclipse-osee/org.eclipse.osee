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
 * Parser class to parse the the ServiceProviderCatalog URL
 *
 * @author Chandan Bandemutt
 */
public class ServiceProviderCatalogReader implements IDoorsArtifactParser {

   boolean flag = false;

   /**
    * {@inheritDoc}
    */
   @Override
   public DoorsArtifact parse(final String path) throws Exception {
      ServiceProviderCatalog serviceProviderCatalog = new ServiceProviderCatalog();
      serviceProviderCatalog.setPath(path);

      DoorsOSLCConnector doors = new DoorsOSLCConnector();
      String catalogResponse = doors.getCatalogResponse(path, null);
      while (catalogResponse.contains("500")) {
         catalogResponse = doors.getCatalogResponse(path, null);
      }

      XPath xpath = XPathFactory.newInstance().newXPath();

      DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
      domFactory.setNamespaceAware(true); // never forget this!
      DocumentBuilder builder = domFactory.newDocumentBuilder();

      InputSource is = new InputSource(new StringReader(catalogResponse));
      Document doc = builder.parse(is);
      Object result1 = xpath.evaluate("//*[local-name()='ServiceProviderCatalog']", doc, XPathConstants.NODESET);
      NodeList rootNodes = (NodeList) result1;
      if (rootNodes.getLength() > 0) {
         for (int i = 0; i < rootNodes.getLength(); i++) {
            Node node = rootNodes.item(i);

            NodeList childNodes = node.getChildNodes();
            for (int j = 0; j < childNodes.getLength(); j++) {
               Node item = childNodes.item(j);
               if ((item.getLocalName() != null) && item.getLocalName().equalsIgnoreCase("title")) {
                  serviceProviderCatalog.setName(item.getTextContent());
               }
            }
            NodeList childNodes1 = node.getChildNodes();
            for (int j = 0; j < childNodes1.getLength(); j++) {
               Node item = childNodes1.item(j);
               if ((item.getLocalName() != null) && item.getLocalName().equalsIgnoreCase("serviceProviderCatalog")) {
                  NamedNodeMap attributes = item.getAttributes();

                  for (int k1 = 0; k1 < attributes.getLength(); k1++) {
                     Node item1 = attributes.item(k1);
                     String nodeValue = item1.getNodeValue();
                     this.flag = true;
                     String replace = replace(nodeValue);
                     DoorsArtifact child = parse(replace);
                     serviceProviderCatalog.addChild(child);
                  }
               } else {
                  if (this.flag) {
                     if ((item.getLocalName() != null) && item.getLocalName().equalsIgnoreCase("serviceProvider")) {
                        NamedNodeMap attributes = item.getAttributes();

                        for (int k1 = 0; k1 < attributes.getLength(); k1++) {
                           Node item1 = attributes.item(k1);
                           String nodeValue = item1.getNodeValue();
                           ServiceProviderReader providerReader = new ServiceProviderReader();
                           DoorsArtifact serviceProviderObj = providerReader.parse(nodeValue);
                           serviceProviderCatalog.addChild(serviceProviderObj);
                        }
                     }
                  }
               }
            }
            this.flag = false;
         }
      }
      return serviceProviderCatalog;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String replace(final String url) {
      String newUrl = url.toLowerCase();
      return newUrl;
   }
}
