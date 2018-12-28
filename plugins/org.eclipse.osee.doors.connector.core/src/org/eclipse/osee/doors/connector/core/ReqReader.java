/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.doors.connector.core;

import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * @author David W. Miller
 */
public class ReqReader implements IDoorsArtifactParser {

   /**
    * Method to parse the DWA URL to get the response
    */
   @Override
   public DoorsArtifact parse(DoorsArtifact reqIn) throws Exception {
      Requirement reqObj;
      if (reqIn instanceof Requirement) {
         reqObj = (Requirement) reqIn;
      } else {
         return reqIn;
      }
      String path = reqObj.getPath();
      // for now, all the parsing does is get the shortName from the shortTitle xml attribute
      // if the shortName is set, don't redo the parsing
      if (Strings.isInValid(reqObj.getShortName())) {

         DoorsOSLCConnector doors = new DoorsOSLCConnector();
         // set query string to null for now, can be used later to get specifics about the requirement
         String catalogResponse = doors.getCatalogResponse(replace(path), null);

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
               String name = findReqName(node);
               if (!name.isEmpty()) {
                  reqObj.setShortName(name);
                  break;
               }
            }
         }
      }
      return reqObj;
   }

   private String findReqName(Node node) {
      String toReturn = "";
      NodeList childNodes = node.getChildNodes();
      for (int j = 0; j < childNodes.getLength(); j++) {
         Node item = childNodes.item(j);
         if (item.getLocalName() != null && item.getLocalName().equalsIgnoreCase("shortTitle")) {
            NodeList childNodes2 = item.getChildNodes();
            Node item2 = childNodes2.item(0);
            toReturn = item2.getTextContent();
            break;
         }
      }
      return toReturn;
   }

   /**
    * Method to return the url replaced to lower case
    */
   @Override
   public String replace(final String url) {
      return url.toLowerCase();
   }
}
