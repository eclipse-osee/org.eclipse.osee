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
package org.eclipse.osee.framework.jdk.core.util.xml;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Paul K. Waldfogel
 */
public class XmlUtility {
   public final static XPathFactory myXPathFactory = XPathFactory.newInstance();
   public final static XPath myXPath = myXPathFactory.newXPath();
   public final static String wordLeader1 =
         "<?xml version='1.0' encoding='UTF-8' standalone='yes'?>" + "<?mso-application progid='Word.Document'?>";
   public final static String wordLeader2 =
         "<w:wordDocument xmlns:w='http://schemas.microsoft.com/office/word/2003/wordml' xmlns:v='urn:schemas-microsoft-com:vml' xmlns:w10='urn:schemas-microsoft-com:office:word' xmlns:sl='http://schemas.microsoft.com/schemaLibrary/2003/core' xmlns:aml='http://schemas.microsoft.com/aml/2001/core' xmlns:wx='http://schemas.microsoft.com/office/word/2003/auxHint' xmlns:o='urn:schemas-microsoft-com:office:office' xmlns:dt='uuid:C2F41010-65B3-11d1-A29F-00AA00C14882' xmlns:wsp='http://schemas.microsoft.com/office/word/2003/wordml/sp2' xmlns:ns0='http://www.w3.org/2001/XMLSchema' xmlns:ns1='http://eclipse.org/artifact.xsd' xmlns:st1='urn:schemas-microsoft-com:office:smarttags' w:macrosPresent='no' w:embeddedObjPresent='no' w:ocxPresent='no' xml:space='preserve'>";
   public final static String wordLeader = wordLeader1.concat(wordLeader2);
   public final static String wordBody = "<w:body></w:body>";
   public final static String wordTrailer = "</w:wordDocument> ";

   public static String treatNonUTF8Characters(String contentString) {
      String resultString = contentString;
      String[][] nonUTF8CharactersOfInterest = { {"–", "-"}, {"’", "'"}, {"’", "'"}, {"“", "\""}, {"”", "\""}};//Wider than usual dash , smaller than usual bullet
      for (int i = 0; i < nonUTF8CharactersOfInterest.length; i++) {
         String[] splitsOfNonUTF8 = resultString.split(nonUTF8CharactersOfInterest[i][0]);//Wider than usual dash or bullet
         if (splitsOfNonUTF8.length > 1) {
            StringBuffer myStringBuffer = new StringBuffer();
            for (int j = 0; j < splitsOfNonUTF8.length; j++) {
               myStringBuffer.append(splitsOfNonUTF8[j]);
               if (splitsOfNonUTF8[j].length() > 0 && j < splitsOfNonUTF8.length - 1) {
                  myStringBuffer.append(nonUTF8CharactersOfInterest[i][1]);
               }
            }
            resultString = myStringBuffer.toString();
         }
      }
      String[] splits = resultString.split("[^\\p{Space}\\p{Graph}]");
      int stringPosition = 0;
      if (splits.length > 1) {
         StringBuffer myStringBuffer = new StringBuffer();
         for (int i = 0; i < splits.length; i++) {
            stringPosition = stringPosition + splits[i].length();
            myStringBuffer.append(splits[i]);
            stringPosition = stringPosition + 1;
            if (splits[i].length() > 0 && i < splits.length - 1) {
               myStringBuffer.append("-");
            }
         }
         resultString = myStringBuffer.toString();
      }

      return resultString;
   }

   public static final Element appendNewElementWithTextCData(Node parentElementName, String newElementTagName, String newText) {
      Element newElement = null;
      try {
         newElement = parentElementName.getOwnerDocument().createElement(newElementTagName);
         parentElementName.appendChild(newElement);
         if (newText != null) {
            Node newTextNode = parentElementName.getOwnerDocument().createCDATASection(newText);
            newElement.appendChild(newTextNode);
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
      ;
      return newElement;
   }
}