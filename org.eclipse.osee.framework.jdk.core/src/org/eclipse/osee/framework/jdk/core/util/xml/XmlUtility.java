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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.sun.org.apache.xml.internal.dtm.ref.DTMNodeList;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

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

   public static final Element appendNewElementWithText(Node parentElementName, String newElementTagName, String newText) {
      Element newElement = null;
      try {
         newElement = parentElementName.getOwnerDocument().createElement(newElementTagName);
         parentElementName.appendChild(newElement);
         if (newText != null) {
            Node newTextNode = parentElementName.getOwnerDocument().createTextNode(newText);
            newElement.appendChild(newTextNode);
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
      ;
      return newElement;
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

   public static final Element appendNewElementWithTextAndOneAttribute(Node parentElementName, String newElementTagName, String newText, String attributeName, String attributeValue) {
      Element newElement = null;
      try {
         newElement = appendNewElementWithText(parentElementName, newElementTagName, newText);
         newElement.setAttribute(attributeName, attributeValue);
      } catch (Exception e) {
         e.printStackTrace();
      }
      ;
      return newElement;
   }

   public final static String restartNumberingWhenPreparingToEditWithWord(InputStream myInputStream) throws XPathExpressionException, ParserConfigurationException, TransformerException, SAXException, IOException {
      SimpleNamespaceContext mySimpleNamespaceContext = new SimpleNamespaceContext();
      addNamespacesForWordMarkupLanguage(myXPath, mySimpleNamespaceContext);
      Document myDocument = Jaxp.readXmlDocumentNamespaceAware(myInputStream);
      Element myDocumentElement = myDocument.getDocumentElement();
      Node[] myListss = selectNodeList(myDocumentElement, "descendant::w:lists");
      Node[] myLists = selectNodeList(myDocumentElement, "descendant::w:lists/w:list");
      String myLastUsedListInitializeListFormat =
            selectNodeList(myLists[myLists.length - 1], "@w:ilfo")[0].getNodeValue();//
      int myNextILFO = Integer.parseInt(myLastUsedListInitializeListFormat);
      Node[] myListDefinition =
            selectNodeList(myDocumentElement, "descendant::w:listDef[child::w:lvl[1]/w:pStyle/@w:val = 'listlvl1'][1]");//<w:pStyle w:val="listlvl1"/>
      String mylistDefaultID = selectNodeList(myListDefinition[0], "@w:listDefId")[0].getNodeValue();
      Node[] myWord_Formatted_Contents = selectNodeList(myDocument, "descendant::ns1:Word_Formatted_Content");
      for (int i = 0; i < myWord_Formatted_Contents.length; i++) {
         Node[] myPStyles =
               selectNodeList(myWord_Formatted_Contents[i], "descendant::w:pPr[child::w:pStyle[@w:val = 'listlvl1']]");
         for (int j = 0; j < Math.min(1, myPStyles.length); j++) {
            Node[] myListProperties = selectNodeList(myPStyles[j], "child::w:listPr");
            myNextILFO++;
            Element newWList =
                  appendNewElementWithTextAndOneAttribute(myListss[0], "w:list", null, "w:ilfo", "" + myNextILFO);
            appendNewElementWithTextAndOneAttribute(newWList, "w:ilst", null, "w:val", mylistDefaultID);
            Element new_lvlOverride =
                  appendNewElementWithTextAndOneAttribute(newWList, "w:lvlOverride", null, "w:ilvl", "0");
            appendNewElementWithTextAndOneAttribute(new_lvlOverride, "w:startOverride", null, "w:val", "1");
            appendNewElementWithTextAndOneAttribute(myListProperties[0], "w:ilvl", null, "w:val", "0");
            appendNewElementWithTextAndOneAttribute(myListProperties[0], "w:ilfo", null, "w:val", "" + myNextILFO);
         }
      }
      String myString = Jaxp.xmlToString(myDocument, Jaxp.getPrettyFormat(myDocument));
      return myString;
   }

   //   public final static String removeRestartNumberingWhenPreparingToPersist(String myInputString) throws XPathExpressionException, ParserConfigurationException, TransformerException, SAXException, IOException {
   //      String myReturnString = myInputString;
   //      SimpleNamespaceContext mySimpleNamespaceContext = new SimpleNamespaceContext();
   //      addNamespacesForWordMarkupLanguage(myXPath, mySimpleNamespaceContext);
   //      Document myDocument = Jaxp.readXmlDocumentNamespaceAware(wordLeader.concat(myInputString).concat(wordTrailer));
   //      Element myDocumentElement = myDocument.getDocumentElement();
   //      boolean isChanged = false;
   //      String[] selectThese = {"descendant::w:listPr/w:ilvl", "descendant::w:listPr/w:ilfo"};
   //      for (int i = 0; i < selectThese.length; i++) {
   //         Node[] myILVLAndILFO = selectNodeList(myDocumentElement, selectThese[i]);//
   //         for (int j = 0; j < myILVLAndILFO.length; j++) {
   //            myILVLAndILFO[j].getParentNode().removeChild(myILVLAndILFO[j]);
   //            isChanged = true;
   //         }
   //      }
   //      if (isChanged) {
   //         byte[] myByteArray = getFormattedContent(myDocumentElement);
   //         myReturnString = new String(myByteArray);
   //      }
   //      return myReturnString;
   //   }

   public static byte[] getFormattedContent(Element formattedItemElement) {
      ByteArrayOutputStream data = new ByteArrayOutputStream();
      OutputFormat format = Jaxp.getCompactFormat(formattedItemElement.getOwnerDocument());
      format.setOmitDocumentType(true);
      format.setOmitXMLDeclaration(true);
      XMLSerializer serializer = new XMLSerializer(data, format);

      try {
         for (Element e : Jaxp.getChildDirects(formattedItemElement))
            serializer.serialize(e);
      } catch (IOException ex) {
         throw new RuntimeException(ex);
      }

      return data.toByteArray();
   }

   public static final Object addNamespacesForWordMarkupLanguage(XPath myXPath, SimpleNamespaceContext mySimpleNamespaceContext) {
      try {
         if (myXPath.getNamespaceContext() == null) {
            mySimpleNamespaceContext.addNamespace("w", "http://schemas.microsoft.com/office/word/2003/wordml");
            mySimpleNamespaceContext.addNamespace("wx", "http://schemas.microsoft.com/office/word/2003/auxHint");
            mySimpleNamespaceContext.addNamespace("o", "urn:schemas-microsoft-com:office:office");
            mySimpleNamespaceContext.addNamespace("v", "urn:schemas-microsoft-com:vml");
            mySimpleNamespaceContext.addNamespace("aml", "http://schemas.microsoft.com/aml/2001/core");
            mySimpleNamespaceContext.addNamespace("dt", "uuid:C2F41010-65B3-11d1-A29F-00AA00C14882");
            mySimpleNamespaceContext.addNamespace("ns0", "http://www.w3.org/2001/XMLSchema");
            mySimpleNamespaceContext.addNamespace("ns1", "http://eclipse.org/artifact.xsd");
            mySimpleNamespaceContext.addNamespace("ns2", "urn:schemas-microsoft-com:office:smarttags");
            mySimpleNamespaceContext.addNamespace("sl", "http://schemas.microsoft.com/schemaLibrary/2003/core");
            mySimpleNamespaceContext.addNamespace("st0", "urn:schemas-microsoft-com:office:smarttags");
            mySimpleNamespaceContext.addNamespace("st1", "urn:schemas-microsoft-com:office:smarttags");
            mySimpleNamespaceContext.addNamespace("st2", "urn:schemas-microsoft-com:office:smarttags");
            mySimpleNamespaceContext.addNamespace("st3", "urn:schemas-microsoft-com:office:smarttags");
            mySimpleNamespaceContext.addNamespace("st4", "urn:schemas-microsoft-com:office:smarttags");
            mySimpleNamespaceContext.addNamespace("w10", "urn:schemas-microsoft-com:office:word");
            mySimpleNamespaceContext.addNamespace("wsp", "http://schemas.microsoft.com/office/word/2003/wordml/sp2");
            mySimpleNamespaceContext.addNamespace("foo", "http://apache.org/foo");
            mySimpleNamespaceContext.addNamespace("bar", "http://apache.org/bar");
            myXPath.setNamespaceContext(mySimpleNamespaceContext);
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
      return null;
   }

   public static final Node[] selectNodeList(Node startingNode, String xPathExpression) throws XPathExpressionException {
      Object publisherNodeSet = null;

      publisherNodeSet = myXPath.evaluate(xPathExpression, startingNode, XPathConstants.NODESET);
      DTMNodeList myNodeList = (DTMNodeList) publisherNodeSet;
      Node[] resultNodes = new Node[myNodeList.getLength()];
      for (int i = 0; i < resultNodes.length; i++) {
         resultNodes[i] = myNodeList.item(i);
      }
      return resultNodes;
   }

}