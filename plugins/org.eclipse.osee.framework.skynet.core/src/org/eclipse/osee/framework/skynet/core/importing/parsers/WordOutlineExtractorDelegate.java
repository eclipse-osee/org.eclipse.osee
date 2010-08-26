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
package org.eclipse.osee.framework.skynet.core.importing.parsers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IStatusHandler;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifactKind;
import org.eclipse.osee.framework.skynet.core.importing.operations.RoughArtifactCollector;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.word.WordUtil;

/**
 * Test: @link: WordOutlineTest
 * 
 * @author Karol M. Wilk
 */
public class WordOutlineExtractorDelegate implements IArtifactExtractorDelegate {
   private static final String WORD_OUTLINE_PARSER_NAME = "Word Outline";
   // Node: <w:t>1.1.1 or Text</w:t>
   private static final Pattern WT_ELEMENT_REGEX = Pattern.compile("<w:t>(.*?)</w:t>");
   // Node: <wx:t wx:val="1.1.1 "/>
   private static final Pattern LIST_ITEM_REGEX = Pattern.compile("<wx:t wx:val=\"([0-9.]+\\s*)\".*/>");
   private static final Pattern OUTLINE_NUMBER = Pattern.compile("((?>\\d+\\.)+\\d*)\\s*");
   private static final Pattern HYPERLINK_PATTERN = Pattern.compile("<w:hlink .*>.*?</w:hlink>", Pattern.DOTALL);

   public enum ContentType {
      CONTENT,
      OUTLINE_TITLE
   };
   /**
    * Keeps state whether on what user decided last
    */
   private ContentType lastDeterminedContentType = ContentType.OUTLINE_TITLE;
   /**
    * Keeps state whether user was asked for help.
    */
   private boolean userAskedForHelp = false;

   private Map<String, RoughArtifact> duplicateCatcher;

   private RoughArtifact previousNamedArtifact;
   private RoughArtifact roughArtifact;
   private StringBuilder wordFormattedContent;

   private StringBuffer lastHeaderNumber;
   private StringBuffer lastHeaderName;
   private StringBuffer lastContent;

   private boolean initalized = false;

   private final OutlineResolution outlineResolution = new OutlineResolution();
   private IConflictResolvingGui conflictResolvingGui = null;

   public WordOutlineExtractorDelegate() {
      this(new IConflictResolvingGui() {
         @Override
         public ContentType determineContentType(Collection<String> paramList) throws OseeCoreException {
            Status status = new Status(IStatus.WARNING, Activator.PLUGIN_ID, 258, "", null);
            IStatusHandler handler = DebugPlugin.getDefault().getStatusHandler(status);
            Object object = null;
            try {
               object = handler.handleStatus(status, paramList);
            } catch (CoreException ex) {
               OseeExceptions.wrapAndThrow(ex);
            }
            return (ContentType) object;
         }
      });
   }

   public WordOutlineExtractorDelegate(IConflictResolvingGui gui) {
      super();
      conflictResolvingGui = gui;
   }

   public IConflictResolvingGui getOutlineResolvingUi() {
      return conflictResolvingGui;
   }

   public interface IConflictResolvingGui {
      ContentType determineContentType(Collection<String> paramList) throws OseeCoreException;
   }

   @Override
   public boolean isApplicable(IArtifactExtractor parser) {
      return parser != null && WORD_OUTLINE_PARSER_NAME.equals(parser.getName());
   }

   /**
    * Subclasses may extend this method to allocate resources
    */
   @Override
   public void initialize() {
      duplicateCatcher = new HashMap<String, RoughArtifact>();
      lastHeaderNumber = new StringBuffer();
      lastHeaderName = new StringBuffer();
      lastContent = new StringBuffer();
      previousNamedArtifact = null;
      roughArtifact = null;
      wordFormattedContent = new StringBuilder();
      initalized = true;

   }

   /**
    * Sublcasses may extend this method to dispose resources.
    */
   @Override
   public void dispose() {
      duplicateCatcher = null;
      previousNamedArtifact = null;
      roughArtifact = null;
      lastHeaderNumber = null;
      lastHeaderName = null;
      lastContent = null;
      initalized = false;
   }

   @Override
   public final void processContent(RoughArtifactCollector collector, boolean forceBody, boolean forcePrimaryType, String headerNumber, String listIdentifier, String paragraphStyle, String content, boolean isParagraph) throws OseeCoreException {

      if (Strings.isValid(content) && initalized && !HYPERLINK_PATTERN.matcher(content).find()) {
         StringBuilder outlineNumber = new StringBuilder(); //Number i.e. 1.1
         StringBuilder outlineName = new StringBuilder(); //Title i.e. Scope
         StringBuilder outlineContent = null; // Content, text, table content, etc.

         boolean newOutlineNumber = processOutlineNumberAndName(content, outlineNumber, outlineName);

         //outline number detection failed, try content 
         if (!newOutlineNumber) {
            processContentOfParagraph(content, outlineContent);
         }

         if (collector != null && newOutlineNumber) {
            setContent();
            roughArtifact = setUpNewArtifact(collector, outlineNumber.toString());
            previousNamedArtifact = roughArtifact;
            processHeadingText(roughArtifact, WordUtil.textOnly(outlineName.toString()));
         } else if (roughArtifact != null) {
            wordFormattedContent.append(content);
         }
      } else {
         throw new OseeCoreException(String.format(
            "%s::processContent() Either passed in content is invalid or *Delegate hasn't been initialized...",
            this.toString()));
      }

   }

   /**
    * Given content (a <w:p> paragraph), fill outlineContent with extracted content information from
    * grabNameAndTemplateContent()
    * 
    * @param content a <w:p> paragraph.
    * @param outlineContent data structure to fill as new content gets extracted.
    * @return if found any new content or not...
    */
   private boolean processContentOfParagraph(String content, StringBuilder outlineContent) {
      outlineContent = new StringBuilder(300); //average content is larger than 16 chars
      grabNameAndTemplateContent(content, outlineContent);

      boolean newOutlineContent = false;
      newOutlineContent = outlineContent.length() != 0;

      if (newOutlineContent) {
         resetLastContent();
         setLastContent(outlineContent.toString());
      }

      return newOutlineContent;
   }

   /**
    * Gets content and attempts to extract outline number and title, if it fails with regular regex, it tries
    * specializedOutlineNumberTitleExtract()
    * 
    * @param content
    * @param outlineNumber
    * @param outlineName
    * @return
    * @throws OseeCoreException
    */
   private boolean processOutlineNumberAndName(String content, StringBuilder outlineNumber, StringBuilder outlineName) throws OseeCoreException {
      Matcher listItemMatcher = LIST_ITEM_REGEX.matcher(content);
      if (listItemMatcher.find()) { // wx:val grab
         //does duplicate catcher contain this number already?
         String number = listItemMatcher.group(1).trim();
         if (duplicateCatcher.get(number) == null) {
            outlineNumber.append(number);
         }
      } else {
         specializedOutlineNumberTitleExtract(content, outlineNumber, outlineName);
      }

      boolean outlineNumberDetected = outlineNumber.length() != 0;
      if (outlineNumberDetected) {
         resetLastHeaderNumber();
         setLastHeaderNumber(outlineNumber.toString());
         grabNameAndTemplateContent(content, outlineName);

         if (outlineName.length() != 0) {
            resetLastHeaderName();
            setLastHeaderName(outlineName.toString());
         }
      }
      return outlineNumberDetected;
   }

   /**
    * Grabs outline text or content. Stores results in outLineStorage.
    * 
    * @param paragraph
    * @param outLineStorage
    */
   private void grabNameAndTemplateContent(String paragraph, StringBuilder outLineStorage) {
      if (outLineStorage.length() == 0) {
         Matcher wtElementMatcher = WT_ELEMENT_REGEX.matcher(paragraph);
         while (wtElementMatcher.find()) {
            Matcher checkingForOutlineNumber = OUTLINE_NUMBER.matcher(wtElementMatcher.group(1));
            if (!checkingForOutlineNumber.matches()) {
               outLineStorage.append(wtElementMatcher.group(1));
            }
         }
      }
   }

   /**
    * Specializes in extraction of "1. scope" type of outline number and names. Outline name can also be spread out over
    * multiple <w:t>s
    * 
    * @param paragraph
    * @param outlineNumberStorage
    * @param outlineTitleStorage
    * @throws OseeCoreException
    */
   private void specializedOutlineNumberTitleExtract(String paragraph, StringBuilder outlineNumberStorage, StringBuilder outlineTitleStorage) throws OseeCoreException {
      StringBuilder wtStorage = new StringBuilder(paragraph.length());
      Matcher wtElementMatcher = WT_ELEMENT_REGEX.matcher(paragraph);
      while (wtElementMatcher.find()) {
         wtStorage.append(wtElementMatcher.group(1));
      }

      int indexOfFirstSpace = wtStorage.toString().indexOf(" ");
      if (indexOfFirstSpace != -1) {
         CharSequence title = wtStorage.subSequence(0, indexOfFirstSpace);
         Matcher outlineNumberMatcher = OUTLINE_NUMBER.matcher(title);
         if (outlineNumberMatcher.matches()) {
            processSpecializedOutlineNumberAndTitle(outlineNumberMatcher.group(),
               (String) wtStorage.subSequence(indexOfFirstSpace, wtStorage.length()), outlineNumberStorage,
               outlineTitleStorage);
         } else {
            outlineTitleStorage = wtStorage;
         }
      } else {
         //must be just content
         outlineTitleStorage = wtStorage;
      }

   }

   private void processSpecializedOutlineNumberAndTitle(String currentOutlineNumber, String formOfOutlineTitle, StringBuilder outlineNumberStorage, StringBuilder outlineTitleStorage) throws OseeCoreException {
      String lastOutlineNumber = getLastHeaderNumber();

      lastDeterminedContentType = ContentType.OUTLINE_TITLE;

      if (outlineResolution.isInvalidOutlineNumber(currentOutlineNumber, lastOutlineNumber)) {
         if (duplicateCatcher.get(currentOutlineNumber) == null) {
            userAskedForHelp = true;
            Collection<String> paramList = new ArrayList<String>();
            paramList.add(lastOutlineNumber);
            paramList.add(currentOutlineNumber);
            lastDeterminedContentType = conflictResolvingGui.determineContentType(paramList);
         } else {
            lastDeterminedContentType = ContentType.CONTENT;
         }
      } else {
         userAskedForHelp = false;
      }

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

   public boolean userAskedForHelp() {
      return userAskedForHelp;
   }

   private void setContent() {
      if (roughArtifact != null) {
         roughArtifact.addAttribute(CoreAttributeTypes.WordTemplateContent, wordFormattedContent.toString());
         wordFormattedContent.setLength(0);
      }
   }

   @Override
   public void finish() {
      setContent();
   }

   public void processHeadingText(RoughArtifact roughArtifact, String headingText) {
      roughArtifact.addAttribute("Name", headingText.trim());
   }

   /**
    * Checks if another artifact with the same outlineNumber was created
    */
   private RoughArtifact setUpNewArtifact(RoughArtifactCollector collector, String outlineNumber) throws OseeCoreException {
      RoughArtifact duplicateArtifact = duplicateCatcher.get(outlineNumber);
      if (duplicateArtifact == null) {
         RoughArtifact roughArtifact = new RoughArtifact(RoughArtifactKind.PRIMARY);
         duplicateCatcher.put(outlineNumber, roughArtifact);

         collector.addRoughArtifact(roughArtifact);
         roughArtifact.setSectionNumber(outlineNumber);

         roughArtifact.addAttribute(CoreAttributeTypes.ParagraphNumber, outlineNumber);

         return roughArtifact;
      } else {
         throw new OseeStateException(String.format(
            "Paragraph %s found more than once following \"%s\" which is a duplicate of %s", outlineNumber,
            previousNamedArtifact.getName(), duplicateArtifact.getName()));
      }
   }

   @Override
   public String getName() {
      return "QuickSilver ICDs and General Outline Documents";
   }

   public String getLastHeaderNumber() {
      return getBufferString(lastHeaderNumber);
   }

   private void setLastHeaderNumber(String lastHeaderNumber) {
      this.lastHeaderNumber.append(lastHeaderNumber);
   }

   public String getLastHeaderName() {
      return getBufferString(lastHeaderName);
   }

   private void setLastHeaderName(String lastHeaderName) {
      this.lastHeaderName.append(lastHeaderName);
   }

   public String getLastContent() {
      return getBufferString(lastContent);
   }

   private void setLastContent(String lastContent) {
      this.lastContent.append(lastContent);
   }

   private String getBufferString(StringBuffer builder) {
      return builder != null ? builder.toString() : null;
   }

   private void resetLastHeaderNumber() {
      this.lastHeaderNumber.setLength(0);
   }

   private void resetLastHeaderName() {
      this.lastHeaderName.setLength(0);
   }

   private void resetLastContent() {
      this.lastContent.setLength(0);
   }
}
