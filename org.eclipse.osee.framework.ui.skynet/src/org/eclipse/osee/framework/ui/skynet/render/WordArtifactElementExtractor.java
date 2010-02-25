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

package org.eclipse.osee.framework.ui.skynet.render;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.linking.OseeLinkParser;
import org.eclipse.osee.framework.skynet.core.word.WordUtil;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Jeff C. Phillips
 */
public class WordArtifactElementExtractor {
   private Element oleDataElement;
   private final Document document;
   
   public WordArtifactElementExtractor(Document document) {
      super();
      this.document = document;
   }

   public Element getOleDataElement() {
      return oleDataElement;
   }

   public Collection<Element> extract(boolean isSingleEdit) throws DOMException, ParserConfigurationException, SAXException, IOException, OseeCoreException{
      final Collection<Element> artifacts = new LinkedList<Element>();
      final String elementNameForWordAttribute = WordUtil.elementNameFor("hlink");
      Collection<Element> sectList = new LinkedList<Element>();
      Element rootElement = document.getDocumentElement();
      Element body = null;
      boolean containsEditTag = false;
      oleDataElement = null;

      NodeList nodeList = rootElement.getElementsByTagName("*");
      Node artifactTagParentNode = null;
      Element newArtifactElement = null;
      ParseState parseState = ParseState.LOOKING_FOR_START;
      
      for (int i = 0; i < nodeList.getLength(); i++) {
         Element element = (Element) nodeList.item(i);
         if (isArtifactEditTag(elementNameForWordAttribute, element)) {
            if(parseState == ParseState.LOOKING_FOR_START){
               parseState = ParseState.LOOKING_FOR_END;
               artifactTagParentNode = element.getParentNode().getParentNode();  
               containsEditTag = true;
               
               newArtifactElement = document.createElement("WordAttribute.WORD_TEMPLATE_CONTENT");
               populateNewArtifactElementFromHlink(newArtifactElement,element);
               artifacts.add(newArtifactElement);
            }else if(parseState == ParseState.LOOKING_FOR_END){
               parseState = ParseState.LOOKING_FOR_START;
               artifactTagParentNode = null;
            }
         }else if(parseState == ParseState.LOOKING_FOR_END && element.getParentNode() == artifactTagParentNode 
               && !isArtifactEditTag(elementNameForWordAttribute, element.getFirstChild())){
            newArtifactElement.appendChild(element.cloneNode(true));
         }
         if (element.getNodeName().endsWith("wx:sect")) {
            //handle the case where there exists two wx:sext elements
            if (element != null) {
               sectList.add(element);
            }
         }
         if (element.getNodeName().endsWith("body") && isSingleEdit) {
            artifacts.add(element);
            body = element;
         } else if (oleDataElement == null && element.getNodeName().endsWith("docOleData")) {
            oleDataElement = element;
         }
      }
      //When creating a three way merge the tags are not added as they create conflicts.  Therefore
      //we remove template information using the listnum fldChar tag.  The following code checks for the 
      //attribute tags and if they are not there removes all the paragraphs following the one that contains the 
      //fldChar
      if (containsEditTag) {
         artifacts.remove(body);
      } else if (!sectList.isEmpty()) {
         handleMultiSectTags(sectList);
      }
      return artifacts;
   }
   
   private void handleMultiSectTags(Collection<Element> sectList) throws OseeCoreException {
      boolean containTag = false;
      // need to check all wx:sect for the listnum tag
      for (Element sectElem : sectList) {
         containTag |= cleanUpParagraph(sectElem);
      }
      if (!containTag) {
         throw new OseeCoreException("This document does not contain the approporate tags to be correctly saved.");
      }
   }
   
   //To handle the case of sub-sections
   private boolean cleanUpParagraph(Node rootNode) throws OseeCoreException {
      boolean worked = false;
      boolean delete = false;
      Node node = rootNode.getFirstChild();
      while (node != null) {
         Node nextNode = node.getNextSibling();
         if (node.getNodeName().endsWith("sub-section")) {
            worked = cleanUpParagraph(node);
         } else {
            String content = node.getTextContent();
            if (content != null && content.contains("LISTNUM\"listreset\"")) {
               delete = true;
            }
            if (delete) {
               rootNode.removeChild(node);
            }
         }
         node = nextNode;
      }
      return worked || delete;
   }

   /**
    * @param newArtifactElement 
    * @param element
    * @throws DOMException 
    * @throws OseeCoreException 
    */
   private void populateNewArtifactElementFromHlink(Element newArtifactElement, Element element) throws OseeCoreException, DOMException {
      OseeLinkParser linkParser = new OseeLinkParser();
      linkParser.parse(element.getAttribute("w:dest"));
      newArtifactElement.setAttribute("guid", linkParser.getGuid());
   }

   /**
    * @param elementNameForWordAttribute
    * @param element
    * @return
    */
   private boolean isArtifactEditTag(final String elementNameForWordAttribute, Node element) {
      boolean isValid = false;
      if(element != null && element.getNodeName().contains(elementNameForWordAttribute)){
         isValid = element.getTextContent().contains("OSEE_EDIT");
      }
      return isValid ;
   }
   
   private enum ParseState{
      LOOKING_FOR_START, LOOKING_FOR_END;
   }
}
