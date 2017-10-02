/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.report.internal.wordupdate;

import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.xpath.XPath;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.jdk.core.util.xml.SimpleNamespaceContext;
import org.eclipse.osee.framework.jdk.core.util.xml.Xml;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Morgan E. Cook
 */
public class UpdateBookmarkIds {
   private static final String WORD_PREFIX =
      "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><?mso-application progid=\"Word.Document\"?><w:wordDocument xmlns:aml=\"http://schemas.microsoft.com/aml/2001/core\" xmlns:dt=\"uuid:C2F41010-65B3-11d1-A29F-00AA00C14882\" xmlns:ve=\"http://schemas.openxmlformats.org/markup-compatibility/2006\" xmlns:o=\"urn:schemas-microsoft-com:office:office\" xmlns:v=\"urn:schemas-microsoft-com:vml\" xmlns:w10=\"urn:schemas-microsoft-com:office:word\" xmlns:w=\"http://schemas.microsoft.com/office/word/2003/wordml\" xmlns:wx=\"http://schemas.microsoft.com/office/word/2003/auxHint\" xmlns:wsp=\"http://schemas.microsoft.com/office/word/2003/wordml/sp2\" xmlns:sl=\"http://schemas.microsoft.com/schemaLibrary/2003/core\" w:macrosPresent=\"no\" w:embeddedObjPresent=\"no\" w:ocxPresent=\"no\" xml:space=\"preserve\">";

   private static final String WORD_BOOKMARK_START = "Word.Bookmark.Start";
   private static final String WORD_BOOKMARK_END = "Word.Bookmark.End";

   private static final String WORD_BODY_START = "<w:body>";
   private static final String WORD_BODY_END = "</w:body>";
   private static final String WORD_DOC_END = "</w:wordDocument>";

   private static final String WORD_AML_ID_ATTRIBUTE = "aml:id";

   private static final String XPATH_EXPRESSION =
      "//aml:annotation[@w:type='Word.Bookmark.End' or @w:type='Word.Bookmark.Start']";

   private int bookMarkId;

   public UpdateBookmarkIds(int startBookMarkId) {
      this.bookMarkId = startBookMarkId;
   }

   private boolean isStartNode(Node node) {
      return isNode(WORD_BOOKMARK_START, node);
   }

   private boolean isEndNode(Node node) {
      return isNode(WORD_BOOKMARK_END, node);
   }

   private boolean isNode(String typeToCheck, Node node) {
      boolean result = false;
      if (node.getNodeType() == Node.ELEMENT_NODE) {
         Element element = (Element) node;
         String typeName = element.getAttribute("w:type");
         result = Strings.isValid(typeName) && typeToCheck.equals(typeName);
      }
      return result;
   }

   private String stripOffBodyTag(String original) {
      int startIndex = original.indexOf(WORD_BODY_START);
      if (startIndex < 0) {
         startIndex = 0;
      } else {
         startIndex = startIndex + WORD_BODY_START.length();
      }
      int stopIndex = original.indexOf(WORD_BODY_END);
      if (stopIndex < 0) {
         stopIndex = original.length();
      }
      return original.substring(startIndex, stopIndex);
   }

   /**
    * Update the aml:annotation (i.e. "Bookmark") tags with updated aml:id attribute values. The tricky part is
    * maintaining the strange order of the start and end tags that Word seems to produce. Not doing so, or resequencing
    * this order can and will produce strange results in the behavior of the document's references.
    */
   public String fixTags(String content)  {
      String toReturn = content;
      boolean changesMade = false;
      try {

         Document document =
            Jaxp.readXmlDocumentNamespaceAware(WORD_PREFIX + WORD_BODY_START + content + WORD_BODY_END + WORD_DOC_END);
         Element element = document.getDocumentElement();

         XPath xPath = Jaxp.createXPath();
         SimpleNamespaceContext context = new SimpleNamespaceContext();
         Xml.addNamespacesForWordMarkupLanguage(xPath, context);
         Collection<Node> nodes = Jaxp.selectNodesViaXPath(xPath, element, XPATH_EXPRESSION);

         //Get a new value for each aml.id (maintaining the order of the start/end Bookmark tags
         Map<Integer, Integer> oldToNewAmlIds = new HashMap<>();
         for (Node node : nodes) {
            if (isStartNode(node) || isEndNode(node)) {
               changesMade = true;
               Node amlIdNode = node.getAttributes().getNamedItem(WORD_AML_ID_ATTRIBUTE);
               if (amlIdNode != null) {
                  String amlIdStr = amlIdNode.getNodeValue();
                  Integer oldAmlId = new Integer(amlIdStr);
                  Integer newAmlId = oldToNewAmlIds.get(oldAmlId);
                  if (newAmlId == null) {
                     newAmlId = incrementBookmarkId();
                     oldToNewAmlIds.put(oldAmlId, newAmlId);
                  }
                  ((Element) node).setAttribute(WORD_AML_ID_ATTRIBUTE, String.valueOf(newAmlId));
               }
            }
         }

         if (changesMade) {
            //This technique is necessary because Word does not support start and ending empty tags.
            toReturn = stripOffBodyTag(Jaxp.xmlToString(document, false));
         }
      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      }

      return toReturn;
   }

   private int incrementBookmarkId() {
      bookMarkId = bookMarkId + 1;
      if (bookMarkId >= Integer.MAX_VALUE) {
         bookMarkId = 0;
      }
      return bookMarkId;
   }

   @SuppressWarnings("unused")
   private static String xmlSectionToString(Node root) throws XMLStreamException {
      StringWriter writer = new StringWriter();
      XMLOutputFactory factory = XMLOutputFactory.newInstance();
      XMLStreamWriter xmlWriter = null;
      try {
         xmlWriter = factory.createXMLStreamWriter(writer);
         Jaxp.writeNode(xmlWriter, root, false);
      } finally {
         if (xmlWriter != null) {
            xmlWriter.close();
         }
      }
      return writer.toString();
   }
}
