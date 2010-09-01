/*
 * Created on Aug 30, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.word;

import java.io.StringWriter;
import java.util.Stack;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.jdk.core.util.xml.Xml;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class UpdateBookmarkIds {
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
      int startIndex = original.indexOf("<body>");
      if (startIndex < 0) {
         startIndex = 0;
      } else {
         startIndex = startIndex + 6;
      }
      int stopIndex = original.indexOf("</body>");
      if (stopIndex < 0) {
         stopIndex = original.length();
      }
      return original.substring(startIndex, stopIndex);
   }

   public String fixTags(String content) throws OseeCoreException {
      String toReturn = content;
      boolean changesMade = false;
      try {
         Document document = Jaxp.readXmlDocument("<body>" + content + "</body>");
         Element element = document.getDocumentElement();

         Stack<Element> nodeStack = new Stack<Element>();
         Node[] list =
            Xml.selectNodeList(element, "//annotation[@type='Word.Bookmark.End' or @type='Word.Bookmark.Start']");

         for (int index = 0; index < list.length; index++) {
            Node currentNode = list[index];
            if (isStartNode(currentNode)) {
               nodeStack.push((Element) currentNode);
            } else if (isEndNode(currentNode)) {
               if (!nodeStack.isEmpty()) {
                  changesMade = true;
                  Element startNode = nodeStack.pop();
                  Element endNode = (Element) currentNode;
                  int newId = ++bookMarkId;
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

   private static String xmlSectionToString(Node root) throws XMLStreamException {
      StringWriter writer = new StringWriter();
      XMLOutputFactory factory = XMLOutputFactory.newInstance();
      XMLStreamWriter xmlWriter = factory.createXMLStreamWriter(writer);
      Jaxp.writeNode(xmlWriter, root);
      return writer.toString();
   }

}
