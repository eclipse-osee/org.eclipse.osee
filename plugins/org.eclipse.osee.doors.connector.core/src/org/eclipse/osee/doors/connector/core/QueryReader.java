/*
 * Copyright (c) 2012 Robert Bosch Engineering and Business Solutions Ltd India. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.osee.doors.connector.core;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
 * Parser class to parse the the Query base URL to get the Requirements
 *
 * @author Chandan Bandemutt
 */
public class QueryReader implements IDoorsArtifactParser {

   /**
    * Method to parse the DWA URL to get the response
    */
   @Override
   public DoorsArtifact parse(DoorsArtifact queryObjIn) throws Exception {
      QueryCapabilities queryObj;
      if (queryObjIn instanceof QueryCapabilities) {
         queryObj = (QueryCapabilities) queryObjIn;
      } else {
         return queryObjIn; // this only parses QueryCapabilites, if not the right type take no action
      }
      String path = queryObj.getPath();

      String url = "oslc.where=dcterms:created<" + getDate();

      DoorsOSLCConnector doors = new DoorsOSLCConnector();
      String catalogResponse = doors.getCatalogResponse(replace(path), url);

      XPath xpath = XPathFactory.newInstance().newXPath();

      DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
      domFactory.setNamespaceAware(true); // never forget this!
      DocumentBuilder builder = domFactory.newDocumentBuilder();

      InputSource is = new InputSource(new StringReader(catalogResponse));
      Document doc = builder.parse(is);
      Object result1 = xpath.evaluate("//*[local-name()='Requirement']", doc, XPathConstants.NODESET);
      NodeList rootNodes = (NodeList) result1;
      if (rootNodes.getLength() > 0) {
         for (int i = 0; i < rootNodes.getLength(); i++) {
            Node node = rootNodes.item(i);
            NodeList childNodes = node.getChildNodes();

            Requirement requirement = new Requirement();

            NamedNodeMap attributes = node.getAttributes();
            for (int k = 0; k < attributes.getLength(); k++) {
               Node item2 = attributes.item(k);
               String nodeValue = item2.getNodeValue();
               requirement.setPath(nodeValue);
            }

            for (int j = 0; j < childNodes.getLength(); j++) {
               Node item = childNodes.item(j);

               if (item.getLocalName() != null && item.getLocalName().equalsIgnoreCase("title")) {
                  NodeList childNodes2 = item.getChildNodes();
                  for (int k = 0; k < childNodes2.getLength(); k++) {
                     Node item2 = childNodes2.item(k);
                     requirement.setName(item2.getTextContent());
                     queryObj.addrequirements(requirement);
                  }
               }
            }
         }
      }
      return queryObj;
   }

   /**
    * Method to return the todays date
    */
   private String getDate() {

      Calendar currentDate = Calendar.getInstance();
      SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
      String dateNow = formatter.format(currentDate.getTime());
      return dateNow;

   }

   /**
    * Method to return the url replaced to lower case
    */
   @Override
   public String replace(final String url) {
      return url.toLowerCase();
   }
}
