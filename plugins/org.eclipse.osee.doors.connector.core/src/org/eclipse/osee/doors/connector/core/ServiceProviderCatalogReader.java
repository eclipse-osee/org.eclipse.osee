/*
 * Copyright (c) 2012 Robert Bosch Engineering and Business Solutions Ltd India. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.osee.doors.connector.core;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
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
   public DoorsArtifact parse(DoorsArtifact provider) throws Exception {

      String path = provider.getPath();

      DoorsOSLCConnector doors = new DoorsOSLCConnector();
      String catalogResponse = doors.getCatalogResponse(path, null);
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
            Node catalogUri = node.getAttributes().getNamedItem("rdf:about");
            if (catalogUri == null) {
               continue;
            }
            String cleanedPath = catalogUri.toString().substring(11, catalogUri.toString().length() - 1);
            if (cleanedPath.equals(path)) {
               continue;
            }
            NodeList childNodes = node.getChildNodes();
            if (childNodes == null) {
               continue;
            }
            for (int j = 0; j < childNodes.getLength(); j++) {
               Node item = childNodes.item(j);
               if (item != null && item.getLocalName() != null && item.getLocalName().equalsIgnoreCase("title")) {
                  ServiceProviderCatalog child = new ServiceProviderCatalog();
                  child.setName(item.getTextContent());
                  child.setPath(cleanedPath);
                  provider.addChild(child);
                  break;
               }
            }
         }
      }
      InputSource is2 = new InputSource(new StringReader(catalogResponse));
      Document doc2 = builder.parse(is2);
      Object result2 = xpath.evaluate("//*[local-name()='ServiceProvider']", doc2, XPathConstants.NODESET);
      NodeList spNodes = (NodeList) result2;
      if (spNodes.getLength() > 0) {
         for (int i = 0; i < spNodes.getLength(); i++) {
            Node node = spNodes.item(i);
            Node catalogUri = node.getAttributes().getNamedItem("rdf:about");
            if (catalogUri == null) {
               continue;
            }
            String cleanedPath = catalogUri.toString().substring(11, catalogUri.toString().length() - 1);
            if (cleanedPath.equals(path)) {
               continue;
            }
            NodeList childNodes = node.getChildNodes();
            if (childNodes == null) {
               continue;
            }
            for (int j = 0; j < childNodes.getLength(); j++) {
               Node item = childNodes.item(j);
               if (item != null && item.getLocalName() != null && item.getLocalName().equalsIgnoreCase("title")) {
                  ServiceProvider child = new ServiceProvider();
                  child.setName(item.getTextContent());
                  child.setPath(cleanedPath);
                  provider.addChild(child);
                  break;
               }
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
      String newUrl = url.toLowerCase();
      return newUrl;
   }

   public void debugPrintToFile(String content) {

      BufferedWriter bw = null;
      FileWriter fw = null;
      String FILENAME = String.format("C:\\UserData\\debugout\\%d.txt", System.currentTimeMillis());
      try {
         fw = new FileWriter(FILENAME);
         bw = new BufferedWriter(fw);
         bw.write(content);

      } catch (IOException e) {
         e.printStackTrace();
      } finally {

         try {

            if (bw != null) {
               bw.close();
            }

            if (fw != null) {
               fw.close();
            }

         } catch (IOException ex) {
            ex.printStackTrace();
         }
      }

   }
}
