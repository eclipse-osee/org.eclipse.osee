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
package org.eclipse.osee.define.rest.internal.wordupdate;

import java.util.Collection;
import java.util.LinkedList;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Jeff C. Phillips
 * @author David W. Miller
 */
public class MergeEditArtifactElementExtractor implements IElementExtractor {
   private Element oleDataElement;
   private final Document document;
   private final String guid;

   public MergeEditArtifactElementExtractor(String guid, Document document) {
      super();
      this.guid = guid;
      this.document = document;
   }

   @Override
   public Element getOleDataElement() {
      return oleDataElement;
   }

   @Override
   public Collection<WordExtractorData> extractElements() throws DOMException {
      final Collection<WordExtractorData> artifactElements = new LinkedList<>();
      Collection<Element> sectList = new LinkedList<>();
      Element rootElement = document.getDocumentElement();
      oleDataElement = null;

      NodeList nodeList = rootElement.getElementsByTagName("*");

      for (int i = 0; i < nodeList.getLength(); i++) {
         Element element = (Element) nodeList.item(i);
         if (element.getNodeName().endsWith("wx:sect")) {
            //handle the case where there exists two wx:sext elements
            sectList.add(element);
         }
         if (element.getNodeName().endsWith("body")) {
            WordExtractorData extractorData = new WordExtractorData();
            extractorData.addParent(element);
            extractorData.setGuid(guid);

            artifactElements.add(extractorData);
         } else if (oleDataElement == null && element.getNodeName().endsWith("docOleData")) {
            oleDataElement = element;
         }
      }
      /**
       * When creating a three way merge the tags are not added as they create conflicts. Therefore we remove template
       * information using the listnum fldChar tag. The following code checks for the attribute tags and if they are not
       * there removes all the paragraphs following the one that contains the fldChar
       */
      if (!sectList.isEmpty()) {
         handleMultiSectTags(sectList);
      }
      return artifactElements;
   }

   private void handleMultiSectTags(Collection<Element> sectList) {
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
   private boolean cleanUpParagraph(Node rootNode) {
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
}
