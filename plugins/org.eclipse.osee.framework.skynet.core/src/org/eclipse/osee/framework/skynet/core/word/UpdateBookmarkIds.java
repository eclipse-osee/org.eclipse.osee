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
package org.eclipse.osee.framework.skynet.core.word;

import java.io.StringWriter;
import java.util.Collection;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.xpath.XPath;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.jdk.core.util.xml.SimpleNamespaceContext;
import org.eclipse.osee.framework.jdk.core.util.xml.Xml;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class UpdateBookmarkIds {
   private static final String WORD_PREFIX =
      "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><?mso-application progid=\"Word.Document\"?><w:wordDocument xmlns:aml=\"http://schemas.microsoft.com/aml/2001/core\" xmlns:dt=\"uuid:C2F41010-65B3-11d1-A29F-00AA00C14882\" xmlns:ve=\"http://schemas.openxmlformats.org/markup-compatibility/2006\" xmlns:o=\"urn:schemas-microsoft-com:office:office\" xmlns:v=\"urn:schemas-microsoft-com:vml\" xmlns:w10=\"urn:schemas-microsoft-com:office:word\" xmlns:w=\"http://schemas.microsoft.com/office/word/2003/wordml\" xmlns:wx=\"http://schemas.microsoft.com/office/word/2003/auxHint\" xmlns:wsp=\"http://schemas.microsoft.com/office/word/2003/wordml/sp2\" xmlns:sl=\"http://schemas.microsoft.com/schemaLibrary/2003/core\" w:macrosPresent=\"no\" w:embeddedObjPresent=\"no\" w:ocxPresent=\"no\" xml:space=\"preserve\">";
   private static final Pattern WORD_BODY_PATTERN = Pattern.compile("<w:body>(.*?)</w:body>");
   private static final String XPATH_EXPRESSION =
      "//aml:annotation[@w:type='Word.Bookmark.End' or @w:type='Word.Bookmark.Start']";

   private int bookMarkId;

   public UpdateBookmarkIds(int startBookMarkId) {
      this.bookMarkId = startBookMarkId;
   }

   private boolean isStartNode(Node node) {
      return isNode("Word.Bookmark.Start", node);
   }

   private boolean isEndNode(Node node) {
      return isNode("Word.Bookmark.End", node);
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
      Matcher matcher = WORD_BODY_PATTERN.matcher(original);
      String toReturn = original;
      if (matcher.find()) {
         toReturn = matcher.group(1);
      }
      return toReturn;
   }

   public String fixTags(String content) throws OseeCoreException {
      String toReturn = content;
      boolean changesMade = false;
      try {

         Document document =
            Jaxp.readXmlDocumentNamespaceAware(WORD_PREFIX + "<w:body>" + content + "</w:body></w:wordDocument>");
         Element element = document.getDocumentElement();

         Stack<Element> nodeStack = new Stack<Element>();

         XPath xPath = Jaxp.createXPath();
         SimpleNamespaceContext context = new SimpleNamespaceContext();
         Xml.addNamespacesForWordMarkupLanguage(xPath, context);
         Collection<Node> nodes = Jaxp.selectNodesViaXPath(xPath, element, XPATH_EXPRESSION);

         for (Node currentNode : nodes) {
            if (isStartNode(currentNode)) {
               nodeStack.push((Element) currentNode);
            } else if (isEndNode(currentNode)) {
               if (!nodeStack.isEmpty()) {
                  changesMade = true;
                  Element startNode = nodeStack.pop();
                  Element endNode = (Element) currentNode;
                  int newId = incrementBookmarkId();
                  startNode.setAttribute("aml:id", String.valueOf(newId));
                  endNode.setAttribute("aml:id", String.valueOf(newId));
               }
            }
         }
         if (changesMade) {
            String data = xmlSectionToString(element);
            toReturn = stripOffBodyTag(data);
         }
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
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
