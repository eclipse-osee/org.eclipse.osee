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

package org.eclipse.osee.framework.ui.skynet.render.artifactElement;

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
public class WordArtifactElementExtractor  implements IElementExtractor{
   private Element oleDataElement;
   private final Document document;
   int numberOfStartTags;
   int numberOfEndTags;
   
   public WordArtifactElementExtractor(Document document){
      super();
      this.document = document;
      this.numberOfEndTags = 0;
      this.numberOfStartTags = 0;
   }

   public Element getOleDataElement() {
      return oleDataElement;
   }

   public Collection<Element> extractElements() throws DOMException, ParserConfigurationException, SAXException, IOException, OseeCoreException{
      final Collection<Element> artifactElements = new LinkedList<Element>();
      final String elementNameForWordAttribute = WordUtil.elementNameFor("hlink");
      Element rootElement = document.getDocumentElement();
      oleDataElement = null;
      numberOfStartTags = 0;
      numberOfEndTags = 0;

      NodeList nodeList = rootElement.getElementsByTagName("*");
      Node artifactTagParentNode = null;
      Element newArtifactElement = null;
      ParseState parseState = ParseState.LOOKING_FOR_START;
      
      for (int i = 0; i < nodeList.getLength(); i++) {
         Element element = (Element) nodeList.item(i);
         if (isArtifactEditTag(elementNameForWordAttribute, element)) {
            if(parseState == ParseState.LOOKING_FOR_START){
               numberOfStartTags++;
               parseState = ParseState.LOOKING_FOR_END;
               artifactTagParentNode = element.getParentNode().getParentNode();  
               newArtifactElement = document.createElement("WordAttribute.WORD_TEMPLATE_CONTENT");
               populateNewArtifactElementFromHlink(newArtifactElement,element);
               artifactElements.add(newArtifactElement);
            }else if(parseState == ParseState.LOOKING_FOR_END){
               numberOfEndTags++;
               parseState = ParseState.LOOKING_FOR_START;
               artifactTagParentNode = null;
            }
         }else if(parseState == ParseState.LOOKING_FOR_END && element.getParentNode() == artifactTagParentNode 
               && !isArtifactEditTag(elementNameForWordAttribute, element.getFirstChild())){
            newArtifactElement.appendChild(element.cloneNode(true));
         }
      }
      
      validateEditTags();
      return artifactElements;
   }
   
   private void validateEditTags() throws OseeCoreException{
      if(numberOfStartTags == 0 || numberOfEndTags != numberOfStartTags){
         throw new OseeCoreException("This document is missing start/end edit tags, therefore the document will not be saved. You can re-edit the artifact and the edit tags should reappear.");
      }
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
