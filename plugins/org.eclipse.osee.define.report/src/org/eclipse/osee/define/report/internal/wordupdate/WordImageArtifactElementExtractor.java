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
package org.eclipse.osee.define.report.internal.wordupdate;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Jeff C. Phillips
 */
public class WordImageArtifactElementExtractor implements IElementExtractor {
   private static final String SECTION_TAG = "wx:sect";
   private static final String SUB_SECTION_TAG = "wx:sub-section";
   private static final String BODY_TAG = "w:body";
   private static final String PICT = "w:pict";
   private static final String SRC = "src";
   private static final String BIN_DATA = "w:binData";
   private static final String IMAGE = "v:imagedata";
   private static String START_IMG_ID;
   private static String END_IMG_ID;
   private static int START_IMAGE_CHECKSUM;
   private static int END_IMAGE_CHECKSUM;
   private static final String TITLE = "o:title";

   private final Map<String, Element> pictureMap;
   private Element oleDataElement;
   private final Document document;
   private int numberOfStartTags;
   private int numberOfEndTags;
   private String guid;

   private enum ParseState {
      LOOKING_FOR_START,
      LOOKING_FOR_END
   };

   private enum Side {
      left,
      right
   };

   public WordImageArtifactElementExtractor(Document document) {
      this.document = document;
      this.numberOfEndTags = 0;
      this.numberOfStartTags = 0;
      this.pictureMap = new HashMap<>();

      WordImageArtifactElementExtractor.START_IMAGE_CHECKSUM = -1;
      WordImageArtifactElementExtractor.END_IMAGE_CHECKSUM = -1;
   }

   @Override
   public Element getOleDataElement() {
      return oleDataElement;
   }

   @Override
   public List<WordExtractorData> extractElements() throws DOMException {
      OseeLinkBuilder linkBuilder = new OseeLinkBuilder();
      return extractElements(linkBuilder);
   }

   private void resetClassFields() {
      pictureMap.clear();
      numberOfStartTags = 0;
      numberOfEndTags = 0;
      oleDataElement = null;
      guid = null;
   }

   public List<WordExtractorData> extractElements(OseeLinkBuilder linkBuilder) throws DOMException {
      final List<WordExtractorData> artifactElements = new LinkedList<>();
      Element rootElement = document.getDocumentElement();
      resetClassFields();

      NodeList nodeList = rootElement.getElementsByTagName("*");
      ParseState parseState = ParseState.LOOKING_FOR_START;

      handleImages(rootElement);

      oleDataElement = (Element) getElement(rootElement, "w:docOleData");

      WordExtractorData extractorData = null;

      int nodeSize = nodeList.getLength();
      for (int i = 0; i < nodeSize; i++) {
         Element element = (Element) nodeList.item(i);

         if (properLevelChild(element)) {
            if (parseState == ParseState.LOOKING_FOR_END && extractorData != null) {
               if (isArtifactEditTag(element, false)) {
                  parseState = handleEndElement(linkBuilder, extractorData, element);
               } else {
                  extractorData.addChild(element.cloneNode(true));
               }
            } else if (isArtifactEditTag(element, true)) {
               parseState = ParseState.LOOKING_FOR_END;
               //This is where we create a new data object for each artifact
               extractorData = new WordExtractorData();
               handleStartElement(linkBuilder, artifactElements, element, extractorData);
            }
         }
      }

      validateEditTags();
      clearImageIds();
      return artifactElements;
   }

   private ParseState handleEndElement(OseeLinkBuilder linkBuilder, WordExtractorData extractorData, Element element) {
      ParseState parseState;
      numberOfEndTags++;
      guid = null;
      parseState = ParseState.LOOKING_FOR_START;

      Node clonedElement = cloneWithoutArtifactEditImage(element, Side.left, linkBuilder);
      if (elementHasGrandChildren(clonedElement)) {
         extractorData.addChild(clonedElement);
      }
      return parseState;
   }

   private void handleStartElement(OseeLinkBuilder linkBuilder, final List<WordExtractorData> artifactElements, Element element, WordExtractorData extractorData) {
      Element newArtifactElement;
      numberOfStartTags++;
      newArtifactElement = document.createElement("WordAttribute.WORD_TEMPLATE_CONTENT");
      populateNewArtifactElement(newArtifactElement);

      extractorData.setGuid(guid);
      extractorData.addParent(newArtifactElement);

      artifactElements.add(extractorData);

      Node clonedElement = cloneWithoutArtifactEditImage(element, Side.right, linkBuilder);
      if (elementHasGrandChildren(clonedElement)) {
         extractorData.addChild(clonedElement);
      }
   }

   private void clearImageIds() {
      START_IMG_ID = null;
      END_IMG_ID = null;
      START_IMAGE_CHECKSUM = -1;
      END_IMAGE_CHECKSUM = -1;
   }

   private boolean elementHasGrandChildren(Node element) {
      return element.getChildNodes().getLength() > 0 && element.getChildNodes().item(0).getChildNodes().getLength() > 0;
   }

   private Node cloneWithoutArtifactEditImage(Element element, Side keepSide, OseeLinkBuilder linkBuilder) {
      Collection<Node> removals = new LinkedList<>();

      Element clonedElement = (Element) element.cloneNode(true);
      boolean beforeEditTag = true;
      boolean afterEditTag = false;
      NodeList descendants = clonedElement.getElementsByTagName("*");
      int nodeSize = descendants.getLength();

      for (int i = 0; i < nodeSize; i++) {
         Node descendant = descendants.item(i);
         if (isEditStartImage(descendant)) {
            removals.add(descendant);
            beforeEditTag = false;
         } else if (isEditEndImage(descendant)) {
            removals.add(descendant);
            afterEditTag = true;
         } else if (beforeEditTag && keepSide == Side.right || afterEditTag && keepSide == Side.left) {
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

   private boolean isEditEndImage(Node descendant) {
      return isEditImage(descendant, false);
   }

   private boolean isEditStartImage(Node descendant) {
      return isEditImage(descendant, true);
   }

   private boolean isEditImage(Node element, boolean lookingForStartImage) {
      boolean hasEditImage = false;
      String name = element.getNodeName();

      if (name.equals(PICT)) {
         hasEditImage = isImageBinData((Element) element, lookingForStartImage);
      }
      return hasEditImage;
   }

   private boolean isImageBinData(Element pictElement, boolean lookingForStart) {
      boolean foundBinData = false;
      String imageId;

      if (lookingForStart) {
         imageId = START_IMG_ID;
      } else {
         imageId = END_IMG_ID;
      }

      if (imageId == null) {
         foundBinData = compareBinData(pictElement, lookingForStart);
      } else {
         foundBinData = compareImageId(imageId, getImageId(pictElement));
      }

      if (foundBinData) {
         setGuid(pictElement);
      }
      return foundBinData;
   }

   private boolean compareImageId(String storedImageId, String currentElementId) {
      return storedImageId.equals(currentElementId);
   }

   private boolean compareBinData(Element pictElement, boolean lookingForStart) {
      boolean foundBindata = false;
      int imageCheckSum = getImageChecksum(lookingForStart);
      Node currentBinData = getElement(pictElement, BIN_DATA);

      if (currentBinData != null) {
         Node bindDataValue = currentBinData.getFirstChild();
         foundBindata = getCheckSum(bindDataValue.getNodeValue()) == imageCheckSum;
         if (foundBindata) {
            if (lookingForStart) {
               START_IMG_ID = getImageId(pictElement);
            } else {
               END_IMG_ID = getImageId(pictElement);
            }
         }
      }
      return foundBindata;
   }

   private int getImageChecksum(boolean lookingForStart) {
      String binData;
      int imageCheckSum;

      if (lookingForStart) {
         binData = OseeLinkBuilder.START_BIN_DATA;

         if (START_IMAGE_CHECKSUM == -1) {
            START_IMAGE_CHECKSUM = getCheckSum(binData);
         }
         imageCheckSum = START_IMAGE_CHECKSUM;
      } else {
         binData = OseeLinkBuilder.END_BIN_DATA;

         if (END_IMAGE_CHECKSUM == -1) {
            END_IMAGE_CHECKSUM = getCheckSum(binData);
         }
         imageCheckSum = END_IMAGE_CHECKSUM;
      }
      return imageCheckSum;
   }

   private String getImageId(Element pictElement) {
      String imgId = null;
      Node imageData = getElement(pictElement, IMAGE);

      if (imageData != null) {
         Node srcAttribute = imageData.getAttributes().getNamedItem(SRC);
         if (srcAttribute != null) {
            imgId = srcAttribute.getNodeValue();
         }
      }
      return imgId;
   }

   private void setGuid(Element pictElement) {
      Node imageData = getElement(pictElement, IMAGE);

      if (imageData != null) {
         Node srcAttribute = imageData.getAttributes().getNamedItem(TITLE);
         if (srcAttribute != null) {
            guid = srcAttribute.getNodeValue();
         }
      }
   }

   private Node getElement(Element element, String name) {
      Node discoveredNode = null;
      NodeList descendants = element.getElementsByTagName(name);

      if (descendants.getLength() > 0) {
         discoveredNode = descendants.item(0);
      }

      return discoveredNode;
   }

   private static int getCheckSum(String data) {
      int checksum = -1;
      for (int index = 0; index < data.length(); index++) {
         char character = data.charAt(index);
         if (character != '\n' && character != '\t' && character != '\r' && character != ' ') {
            checksum += character;
         }
      }
      return checksum;
   }

   private void validateEditTags()  {
      if (numberOfStartTags == 0 || numberOfEndTags != numberOfStartTags) {
         throw new OseeCoreException(
            "This document is missing start/end edit tags, therefore the document will not be saved. You can re-edit the artifact and the edit tags should reappear.");
      }
   }

   private void populateNewArtifactElement(Element newArtifactElement) throws DOMException {
      newArtifactElement.setAttribute("guid", guid);
   }

   private boolean isArtifactEditTag(Element element, boolean lookingForStart) {
      boolean isArtifactEditTag = false;
      NodeList descendants = element.getElementsByTagName(PICT);
      int nodeSize = descendants.getLength();
      for (int i = 0; i < nodeSize; i++) {
         Node descendant = descendants.item(i);
         isArtifactEditTag = isEditImage(descendant, lookingForStart);

         if (isArtifactEditTag) {
            break;
         }
      }
      return isArtifactEditTag;
   }

   private boolean properLevelChild(Element element) {
      return properLevelChildWord2003(element) || properLevelChildWord2007(element);
   }

   private void handleImages(Element element) {
      NodeList descendants = element.getElementsByTagName(PICT);
      int nodeSize = descendants.getLength();
      for (int i = 0; i < nodeSize; i++) {
         Node descendant = descendants.item(i);
         NodeList imageDataElement = ((Element) descendant).getElementsByTagName(IMAGE);
         if (imageDataElement.getLength() > 0) {
            String imgKey = ((Element) imageDataElement.item(0)).getAttribute(SRC);
            Element storedPictureElement = pictureMap.get(imgKey);
            NodeList binDataElement = ((Element) descendant).getElementsByTagName(BIN_DATA);

            if (storedPictureElement != null) {
               if (binDataElement.getLength() == 0) {
                  descendant.appendChild(storedPictureElement.cloneNode(true));
               }
            } else {
               pictureMap.put(imgKey, (Element) binDataElement.item(0));
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