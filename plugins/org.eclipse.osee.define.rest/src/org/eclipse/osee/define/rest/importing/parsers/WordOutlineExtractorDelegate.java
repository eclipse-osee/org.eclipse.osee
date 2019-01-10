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
package org.eclipse.osee.define.rest.importing.parsers;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.define.api.importing.IArtifactExtractor;
import org.eclipse.define.api.importing.IArtifactExtractorDelegate;
import org.eclipse.define.api.importing.RoughArtifact;
import org.eclipse.define.api.importing.RoughArtifactCollector;
import org.eclipse.define.api.importing.RoughArtifactKind;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.define.rest.internal.wordupdate.WordUtilities;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @see WordOutlineTest
 */
public class WordOutlineExtractorDelegate implements IArtifactExtractorDelegate {
   private static final String WORD_OUTLINE_PARSER_NAME = "Word Outline";
   // Node: <w:t>1.1.1 or Text</w:t> or <w:t xml:preserve="x">1.1.1 or Text</w:t>
   private static final Pattern WT_ELEMENT_REGEX = Pattern.compile("<w:t.*?>(.*?)</w:t>", Pattern.DOTALL);
   // Node: <wx:t wx:val="1.1.1 "/>
   private static final Pattern LIST_ITEM_REGEX = Pattern.compile("<wx:t wx:val=\"([0-9.]+\\s*)\".*/>");
   private static final Pattern OUTLINE_NUMBER_REGEX = Pattern.compile("((?>\\d+\\.)+\\d*(?>-\\d+)*)\\s*");

   // This assumes that the user uses a generated Table of Contents from Word and does not come up with
   // his/hers own version of a style can call it "TOC\d+"
   private static final Pattern TOC_HYPERLINK_PATTERN =
      Pattern.compile(".*<w:pStyle w:val=\"TOC\\d+?\"/>.*", Pattern.DOTALL);
   private boolean possibleTableOfContents;

   private static String detectedTableOfContentsReportError =
      "Table of Contents found in document. Please remove per the spec on: \n http://wiki.eclipse.org/OSEE/HowTo/ImportArtifacts";

   public enum ContentType {
      CONTENT,
      OUTLINE_TITLE
   };

   /**
    * Keeps state whether on what user decided last
    */
   private ContentType lastDeterminedContentType = ContentType.OUTLINE_TITLE;

   private Map<String, RoughArtifact> duplicateCatcher;
   private Map<String, String> roughArtMeta;

   protected RoughArtifact previousNamedArtifact;
   protected RoughArtifact roughArtifact;
   protected StringBuilder wordFormattedContent;

   protected StringBuffer lastHeaderNumber;
   private StringBuffer lastHeaderName;

   private boolean initalized;

   private final OutlineResolution outlineResolution = new OutlineResolution();

   @Override
   public boolean isApplicable(IArtifactExtractor parser) {
      return parser != null && WORD_OUTLINE_PARSER_NAME.equals(parser.getName());
   }

   /**
    * Subclasses may extend this method to allocate resources
    */
   @Override
   public void initialize() {
      duplicateCatcher = new HashMap<>();
      roughArtMeta = new HashMap<>();
      lastHeaderNumber = new StringBuffer();
      lastHeaderName = new StringBuffer();
      previousNamedArtifact = null;
      roughArtifact = null;
      wordFormattedContent = new StringBuilder();
      initalized = true;
      possibleTableOfContents = false;
   }

   /**
    * Subclasses may extend this method to dispose resources.
    */
   @Override
   public void dispose() {
      duplicateCatcher = null;
      roughArtMeta = null;
      previousNamedArtifact = null;
      roughArtifact = null;
      lastHeaderNumber = null;
      lastHeaderName = null;
      initalized = false;
      possibleTableOfContents = false;
   }

   /**
    * Core of processing different WordML content "chunks".
    */
   @Override
   public final void processContent(OrcsApi orcsApi, ActivityLog activityLog, RoughArtifactCollector collector, boolean forceBody, boolean forcePrimaryType, String headerNumber, String listIdentifier, String paragraphStyle, String content, boolean isParagraph) {
      if (Strings.isValid(content) && initalized) {

         if (!possibleTableOfContents) {
            possibleTableOfContents = TOC_HYPERLINK_PATTERN.matcher(content).matches();
            if (possibleTableOfContents && activityLog != null) {
               activityLog.getDebugLogger().error("Document cannot contain a table of contents");
            }
         }

         StringBuilder outlineNumber = new StringBuilder(); //Number i.e. 1.1
         StringBuilder outlineName = new StringBuilder(); //Title i.e. Scope

         boolean newOutlineNumber = processOutlineNumberAndName(content, outlineNumber, outlineName, paragraphStyle);

         if (newOutlineNumber) {
            setContent();
            String number = outlineNumber.toString();
            roughArtifact = setUpNewArtifact(orcsApi, activityLog, collector, number);
            if (roughArtifact == null) {
               return;
            }
            previousNamedArtifact = roughArtifact;
            processHeadingText(roughArtifact, WordUtilities.textOnly(outlineName.toString()));
            roughArtMeta.put(number, paragraphStyle);
            resetReqNumber();
         } else {
            addChildRoughArtifact(orcsApi, activityLog, content, collector);
         }

      } else {
         throw new OseeCoreException(
            "%s::processContent() Either passed in content is invalid or *Delegate hasn't been initialized...",
            toString());
      }
   }

   protected void addChildRoughArtifact(OrcsApi orcsApi, ActivityLog activityLog, String content, RoughArtifactCollector collector) {
      // Override with inheriting class if needed
      // Allows child classes to choose to make given content into additional RoughArtifacts
      wordFormattedContent.append(content);
   }

   protected void resetReqNumber() {
      //Override with inheriting class if needed
   }

   /**
    * Gets content and attempts to extract outline number and title, if it fails with regular regex, it tries
    * specializedOutlineNumberTitleExtract()
    */
   private boolean processOutlineNumberAndName(String content, StringBuilder outlineNumberStorage, StringBuilder outlineName, String paragraphStyle) {
      Matcher listItemMatcher = LIST_ITEM_REGEX.matcher(content);
      if (listItemMatcher.find()) { // wx:val grab

         String number = listItemMatcher.group(1).trim();

         if (duplicateCatcher.get(number) == null) {

            if (previousNamedArtifact == null) {

               outlineNumberStorage.append(number); //definitely store because no other artifact exist so far

            } else {

               boolean valid = determineIfValid(number, paragraphStyle);
               if (valid) {
                  outlineNumberStorage.append(number);
               }

            }

         }

      } else {
         specializedOutlineNumberTitleExtract(content, outlineNumberStorage, outlineName, paragraphStyle);
      }

      boolean outlineNumberDetected = outlineNumberStorage.length() != 0;
      if (outlineNumberDetected) {
         lastHeaderNumber.setLength(0);
         setLastHeaderNumber(outlineNumberStorage.toString());
         grabNameAndTemplateContent(content, outlineName);

         if (outlineName.length() != 0) {
            lastHeaderName.setLength(0);
            lastHeaderName.append(outlineName.toString());
         }
      }
      return outlineNumberDetected;
   }

   private boolean determineIfValid(String number, String paragraphStyle) {
      boolean result = false;

      if (previousNamedArtifact != null && previousNamedArtifact.getSectionNumber() != null) {
         String sectionNumber = previousNamedArtifact.getSectionNumber().getNumberString();
         if (checkSectionNumber(sectionNumber)) {
            return true; // special case of numbering requirements below a section number
         }
         String metaData = roughArtMeta.get(sectionNumber);
         paragraphStyle = Strings.isValid(paragraphStyle) ? paragraphStyle : Strings.EMPTY_STRING;

         boolean invalid = outlineResolution.isInvalidOutlineNumber(number, sectionNumber);
         result = !invalid && RoughArtifactMetaData.matches(metaData, paragraphStyle);
      } else {
         result = true; //accept since there is no previous
      }

      return result;
   }

   /**
    * Allows child classes to handle a special case section number
    */
   protected boolean checkSectionNumber(String sectionNumber) {
      return false;
   }

   /**
    * Grabs outline text or content. Stores results in outLineStorage.
    */
   private void grabNameAndTemplateContent(String paragraph, StringBuilder outLineStorage) {
      if (outLineStorage.length() == 0) {
         Matcher wtElementMatcher = WT_ELEMENT_REGEX.matcher(paragraph);
         while (wtElementMatcher.find()) {
            Matcher checkingForOutlineNumber = OUTLINE_NUMBER_REGEX.matcher(wtElementMatcher.group(1));
            if (!checkingForOutlineNumber.matches()) {
               outLineStorage.append(wtElementMatcher.group(1));
            }
         }
      }
   }

   /**
    * Specializes in extraction of "1.0 scope" type of outline number and names. Outline name can also be spread out
    * over multiple {@code <w:t> } elements.<br/>
    */
   private void specializedOutlineNumberTitleExtract(String paragraph, StringBuilder outlineNumberStorage, StringBuilder outlineTitleStorage, String paragraphStyle) {
      StringBuilder wtStorage = new StringBuilder(paragraph.length());
      Matcher wtElementMatcher = WT_ELEMENT_REGEX.matcher(paragraph);
      while (wtElementMatcher.find()) {
         wtStorage.append(wtElementMatcher.group(1));
      }

      int indexOfFirstSpace = wtStorage.toString().indexOf(" ");
      if (indexOfFirstSpace != -1) {
         CharSequence paragraphNumber = wtStorage.subSequence(0, indexOfFirstSpace);
         Matcher outlineNumberMatcher = OUTLINE_NUMBER_REGEX.matcher(paragraphNumber);
         if (outlineNumberMatcher.matches() && paragraphNumber.length() > 2) { //length check excludes 1. non-zero based paragraph numbers.
            processSpecializedOutlineNumberAndTitle(outlineNumberMatcher.group(),
               wtStorage.subSequence(indexOfFirstSpace, wtStorage.length()).toString(), outlineNumberStorage,
               outlineTitleStorage, paragraphStyle);
         } else {
            outlineTitleStorage = wtStorage;
         }
      } else {
         //must be just content
         outlineTitleStorage = wtStorage;
      }

   }

   private void processSpecializedOutlineNumberAndTitle(String currentOutlineNumber, String formOfOutlineTitle, StringBuilder outlineNumberStorage, StringBuilder outlineTitleStorage, String paragraphStyle) {
      boolean valid = determineIfValid(currentOutlineNumber, paragraphStyle);
      lastDeterminedContentType = valid ? ContentType.OUTLINE_TITLE : ContentType.CONTENT;

      switch (lastDeterminedContentType) {
         case CONTENT:
            // do nothing because when method exits it will be processed as content
            break;
         case OUTLINE_TITLE:
         default:
            outlineNumberStorage.append(currentOutlineNumber);
            if (Strings.isValid(formOfOutlineTitle)) {
               outlineTitleStorage.append(formOfOutlineTitle);
            }
            break;
      }
   }

   protected void postProcessContent(StringBuilder wordFormattedContent, RoughArtifact roughArtifact) {
      //Override with inheriting class if needed
   }

   /**
    * Sets up storage (word formatted storage) for new artifact.
    */
   protected void setContent() {
      if (roughArtifact != null) {
         roughArtifact.addAttribute(CoreAttributeTypes.WordTemplateContent, wordFormattedContent.toString());
         postProcessContent(wordFormattedContent, roughArtifact);
         wordFormattedContent.setLength(0);
      }
   }

   @Override
   public void finish(OrcsApi orcsApi, ActivityLog activityLog, RoughArtifactCollector collector) {
      setContent();
   }

   @Override
   public void finish() {
      setContent();
   }

   public void processHeadingText(RoughArtifact roughArtifact, String headingText) {
      roughArtifact.setName(headingText.trim());
   }

   /**
    * Checks if another artifact with the same outlineNumber was created
    */
   private RoughArtifact setUpNewArtifact(OrcsApi orcsApi, ActivityLog activityLog, RoughArtifactCollector collector, String outlineNumber) {
      RoughArtifact duplicateArtifact = duplicateCatcher.get(outlineNumber);
      if (duplicateArtifact == null) {
         RoughArtifact roughArtifact = new RoughArtifact(orcsApi, activityLog, RoughArtifactKind.PRIMARY);
         duplicateCatcher.put(outlineNumber, roughArtifact);

         if (collector != null) {
            collector.addRoughArtifact(roughArtifact);
         }
         roughArtifact.setSectionNumber(outlineNumber);

         roughArtifact.addAttribute(CoreAttributeTypes.ParagraphNumber, outlineNumber);

         return roughArtifact;
      } else {
         String previousArtifcatName = previousNamedArtifact != null ? previousNamedArtifact.getName() : "null";
         activityLog.getDebugLogger().info(
            "Paragraph %s found more than once following \"%s\" which is a duplicate of %s", outlineNumber,
            previousArtifcatName, duplicateArtifact.getName());
         return null;
      }
   }

   @Override
   public String getName() {
      return "General Outline Documents";
   }

   public String getLastHeaderNumber() {
      return getBufferString(lastHeaderNumber);
   }

   private void setLastHeaderNumber(String headerNumber) {
      lastHeaderNumber.append(headerNumber);
   }

   public String getLastHeaderName() {
      return getBufferString(lastHeaderName);
   }

   private String getBufferString(StringBuffer builder) {
      return builder != null ? builder.toString() : null;
   }
}
