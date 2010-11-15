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

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.linking.OseeLinkBuilder;
import org.eclipse.osee.framework.skynet.core.word.WordUtil;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Jeff C. Phillips
 */
public class WordArtifactElementExtractor implements IElementExtractor {
   private static final String SECTION_TAG = "wx:sect";
   private static final String SUB_SECTION_TAG = "wx:sub-section";
   private static final String BODY_TAG = "w:body";
   private static final String ANNOTATION = "annotation";
   private static final String TYPE = "w:type";
   private static final String NAME = "w:name";
   private static final String PIC = "w:pic";
   private static final String IMGAGE = "v:imagedata";
   private static final String BIN_DATA = "w:binData";
   private static final String BOOKMARK_END = "bookmarkEnd";
   private static final String BOOKMARK_START = "bookmarkStart";
   private static final String BOOKMARK = "oseebookmark";
   private static final String BOOKMARK_END_TYPE = "Word.Bookmark.End";

   private final Map<String, Element> pictureMap;
   private Element oleDataElement;
   private final Document document;
   private int numberOfStartTags;
   private int numberOfEndTags;

   private enum ParseState {
      LOOKING_FOR_START,
      LOOKING_FOR_END
   };

   private enum Side {
      left,
      right
   };

   public WordArtifactElementExtractor(Document document) {
      super();
      this.document = document;
      this.numberOfEndTags = 0;
      this.numberOfStartTags = 0;
      this.pictureMap = new HashMap<String, Element>();
   }

   @Override
   public Element getOleDataElement() {
      return oleDataElement;
   }

   @Override
   public List<Element> extractElements() throws DOMException, OseeCoreException {
      OseeLinkBuilder linkBuilder = new OseeLinkBuilder();
      return extractElements(linkBuilder);
   }

   public List<Element> extractElements(OseeLinkBuilder linkBuilder) throws DOMException, OseeCoreException {
      final List<Element> artifactElements = new LinkedList<Element>();
      Element rootElement = document.getDocumentElement();
      pictureMap.clear();
      oleDataElement = null;
      numberOfStartTags = 0;
      numberOfEndTags = 0;

      NodeList nodeList = rootElement.getElementsByTagName("*");
      Element newArtifactElement = null;
      ParseState parseState = ParseState.LOOKING_FOR_START;

      for (int i = 0; i < nodeList.getLength(); i++) {
         Element element = (Element) nodeList.item(i);
         if (isArtifactEditTag(element)) {
            if (parseState == ParseState.LOOKING_FOR_START) {
               numberOfStartTags++;
               parseState = ParseState.LOOKING_FOR_END;
               newArtifactElement = document.createElement("WordAttribute.WORD_TEMPLATE_CONTENT");
               populateNewArtifactElementFromBookmark(newArtifactElement, getbookmarkDescendant(element), linkBuilder);
               artifactElements.add(newArtifactElement);

               Node clonedElement = cloneWithoutArtifactEditTag(element, Side.right);
               if (elementHasGrandChildren(clonedElement)) {
                  newArtifactElement.appendChild(clonedElement);
               }
            } else if (parseState == ParseState.LOOKING_FOR_END) {
               numberOfEndTags++;
               parseState = ParseState.LOOKING_FOR_START;

               Node clonedElement = cloneWithoutArtifactEditTag(element, Side.left);
               if (elementHasGrandChildren(clonedElement)) {
                  newArtifactElement.appendChild(clonedElement);
               }
            }
         } else if (parseState == ParseState.LOOKING_FOR_END && properLevelChild(element)) {
            handleImages(element);
            newArtifactElement.appendChild(element.cloneNode(true));
         }
      }

      validateEditTags();
      return artifactElements;
   }

   private boolean elementHasGrandChildren(Node element) {
      return element.getChildNodes().getLength() > 0 && element.getChildNodes().item(0).getChildNodes().getLength() > 0;
   }

   private Element getbookmarkDescendant(Element element) {
      NodeList descendants = element.getElementsByTagName("*");
      for (int i = 0; i < descendants.getLength(); i++) {
         Element descendant = (Element) descendants.item(i);
         if (isEditbookmark(descendant)) {
            return descendant;
         }
      }

      throw new IllegalStateException("We only called this because we found it before, should never get here");
   }

   /**
    * @param keepSide TODO
    */
   private Node cloneWithoutArtifactEditTag(Element element, Side keepSide) {
      Collection<Node> removals = new LinkedList<Node>();

      Element clonedElement = (Element) element.cloneNode(true);
      boolean beforeEditTag = true;
      boolean afterEditTag = false;
      NodeList descendants = clonedElement.getElementsByTagName("*");

      for (int i = 0; i < descendants.getLength(); i++) {
         Node descendant = descendants.item(i);
         if (isEditbookmark(descendant)) {
            removals.add(descendant);
            beforeEditTag = false;
         } else if (isBookmarkEnd(descendant)) {
            removals.add(descendant);
            afterEditTag = true;
         } else if ((!beforeEditTag && !afterEditTag) || (beforeEditTag && keepSide == Side.right) || (afterEditTag && keepSide == Side.left)) {
            removals.add(descendant);
         }
      }

      for (Node remove : removals) {
         Node parentNode = remove.getParentNode();
         if (parentNode != null) {
            parentNode.removeChild(remove);
         }
      }

      return clonedElement;
   }

   private boolean isBookmarkEnd(Node descendant) {
      boolean isBookmark = false;
      String annotationName = WordUtil.elementNameFor(ANNOTATION);

      if (descendant.getNodeName().contains(BOOKMARK_END)) {
         isBookmark = true;
      } else if (descendant.getNodeName().contains(annotationName)) {
         Node destinationAttribute = descendant.getAttributes().getNamedItem(TYPE);
         isBookmark = destinationAttribute.getNodeValue().contains(BOOKMARK_END_TYPE);
      }
      return isBookmark;
   }

   private boolean isEditbookmark(Node element) {
      return isBookmarkStart(element);
   }

   private boolean isBookmarkStart(Node element) {
      boolean isBookmarkStart = false;
      String annotationName = WordUtil.elementNameFor(ANNOTATION);
      String name = element.getNodeName();

      if (name.contains(annotationName) || name.contains(BOOKMARK_START)) {
         Node destinationAttribute = element.getAttributes().getNamedItem(NAME);
         if (destinationAttribute != null && destinationAttribute.getNodeValue().contains(BOOKMARK)) {
            isBookmarkStart = true;
         }
      }
      return isBookmarkStart;
   }

   private void validateEditTags() throws OseeCoreException {
      if (numberOfStartTags == 0 || numberOfEndTags != numberOfStartTags) {
         throw new OseeCoreException(
            "This document is missing start/end edit tags, therefore the document will not be saved. You can re-edit the artifact and the edit tags should reappear.");
      }
   }

   private void populateNewArtifactElementFromBookmark(Element newArtifactElement, Element element, OseeLinkBuilder linkBuilder) throws DOMException {
      newArtifactElement.setAttribute("guid", linkBuilder.extractGuid(element));
   }

   private boolean isArtifactEditTag(Element element) {
      if (!properLevelChild(element)) {
         return false;
      }

      NodeList descendants = element.getElementsByTagName("*");
      for (int i = 0; i < descendants.getLength(); i++) {
         Node descendant = descendants.item(i);
         if (isEditbookmark(descendant)) {
            return true;
         }
      }
      return false;
   }

   private boolean properLevelChild(Element element) {
      return properLevelChildWord2003(element) || properLevelChildWord2007(element);
   }

   private void handleImages(Element element) {
      NodeList descendants = element.getElementsByTagName("*");
      for (int i = 0; i < descendants.getLength(); i++) {
         Node descendant = descendants.item(i);
         if (descendant.getNodeName().contains(PIC)) {
            NodeList imageDataElement = ((Element) descendant).getElementsByTagName(IMGAGE);
            if (imageDataElement.getLength() > 0) {
               String imgKey = ((Element) imageDataElement.item(0)).getAttribute("src");
               Element storedPictureElement = pictureMap.get(imgKey);
               NodeList binDataElement = ((Element) descendant).getElementsByTagName(BIN_DATA);

               if (storedPictureElement != null) {
                  if (binDataElement.getLength() == 0) {
                     descendant.appendChild(storedPictureElement);
                  }
               } else {
                  pictureMap.put(imgKey, (Element) binDataElement.item(0));
               }
            }
         }
      }
   }

   private String getAncestorName(Element element, int level) {
      String name = "";

      Node parent = element;
      for (int i = 0; i < level; i++) {
         if (parent != null) {
            parent = parent.getParentNode();
         }
      }

      if (parent != null) {
         name = parent.getNodeName();
      }

      return name;
   }

   private boolean properLevelChildWord2003(Element element) {
      String grandParentName = getAncestorName(element, 2);
      String parentName = getAncestorName(element, 1);
      String myName = element.getNodeName();

      boolean nonSubsectionBodyChild =
         parentName.equals(BODY_TAG) && !myName.equals(SUB_SECTION_TAG) && !myName.equals(SECTION_TAG);
      boolean sectionChild =
         grandParentName.equals(BODY_TAG) && parentName.equals(SECTION_TAG) && !myName.equals(SUB_SECTION_TAG);
      boolean subsectionChild = parentName.equals(SUB_SECTION_TAG) && !myName.equals(SUB_SECTION_TAG);

      return nonSubsectionBodyChild || sectionChild || subsectionChild;
   }

   private boolean properLevelChildWord2007(Element element) {
      String parentName = getAncestorName(element, 1);
      String myName = element.getNodeName();

      boolean nonSubsectionBodyChild =
         parentName.equals(BODY_TAG) && !myName.equals(SUB_SECTION_TAG) && !myName.equals(SECTION_TAG);
      boolean subsectionChild = parentName.equals(SUB_SECTION_TAG) && !myName.equals(SUB_SECTION_TAG);

      return nonSubsectionBodyChild || subsectionChild;
   }
}
