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
package org.eclipse.osee.framework.ui.skynet.Import;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.util.Readers;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.word.WordUtil;

/**
 * @author Andrew M. Finkbeiner
 * @author Robert A. Fisher
 */
public class WordOutlineExtractor extends WordExtractor {
   private static final String description =
         "Extract data from a Word XML file with an outline, making an artifact for each outline numbered section";
   private static final String PARAGRAPH_TAG_WITH_ATTRS = "<w:p ";
   private static final String PARAGRAPH_TAG_EMPTY = "<w:p/>";
   private static final String PARAGRAPH_TAG = "<w:p>";
   private static final String TABLE_TAG_WITH_ATTRS = "<w:tbl ";
   private static final String TABLE_TAG_EMPTY = "<w:tbl/>";
   private static final String TABLE_TAG = "<w:tbl>";
   private static final CharSequence[] BODY_TAGS =
         new CharSequence[] {PARAGRAPH_TAG, PARAGRAPH_TAG_EMPTY, PARAGRAPH_TAG_WITH_ATTRS, TABLE_TAG, TABLE_TAG_EMPTY,
               TABLE_TAG_WITH_ATTRS, BODY_END};

   // A regex for reading xml elements. Assumes that an element never has a descendant with the same name as itself
   private static final Pattern internalAttributeElementsPattern =
         Pattern.compile("<((\\w+:)?(\\w+))(\\s+.*?)((/>)|(>(.*?)</\\1>))",
               Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
   private static final Pattern proofErrTagKiller = Pattern.compile("</?w:proofErr.*?/?>");
   private static final int NAMESPACE_GROUP = 2;
   private static final int ELEMENT_NAME_GROUP = 3;
   private static final int ATTRIBUTE_BLOCK_GROUP = 4;
   private static final int CONTENT_GROUP = 8;

   private Matcher reqNumberMatcher;
   private Matcher reqListMatcher;
   private Stack<String> currentListStack;
   private Stack<String> clonedCurrentListStack;
   private int lastDepthNumber;
   private String headerNumber;
   private String listIdentifier;
   private final ArtifactSubtypeDescriptor headingDescriptor;
   private final ArtifactSubtypeDescriptor mainDescriptor;
   private final int maxExtractionDepth;
   private boolean forceBody;
   private boolean forcePrimaryType;
   private String paragraphStyle;

   private final IWordOutlineContentHandler handler;

   public WordOutlineExtractor(ArtifactSubtypeDescriptor mainDescriptor, Branch branch, int maxExtractionDepth, IWordOutlineContentHandler handler) throws SQLException {

      super(branch);

      if (mainDescriptor == null) throw new IllegalArgumentException("mainDescriptor can not be null");
      if (branch == null) throw new IllegalArgumentException("branch can not be null");
      if (handler == null) throw new IllegalArgumentException("handler can not be null");

      this.handler = handler;

      this.headerNumber = "";
      this.listIdentifier = "";
      this.reqNumberMatcher = Pattern.compile("(\\d+\\.)*(\\d+\\.?)\\s*").matcher("");
      this.reqListMatcher = Pattern.compile("\\w+\\)", Pattern.CASE_INSENSITIVE).matcher("");

      this.currentListStack = new Stack<String>();
      this.clonedCurrentListStack = new Stack<String>();
      this.headingDescriptor = configurationPersistenceManager.getArtifactSubtypeDescriptor("Heading");
      this.mainDescriptor = mainDescriptor;
      this.maxExtractionDepth = maxExtractionDepth;
   }

   public static String getDescription() {
      return description;
   }

   @SuppressWarnings("unchecked")
   public void discoverArtifactAndRelationData(File importFile) throws Exception {

      Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(importFile), "UTF-8"));

      if (Readers.forward(reader, BODY_START) == null) {
         throwFileFormatError(importFile, "no start of body tag");
      }

      handler.init(this, headingDescriptor, mainDescriptor);

      try {
         CharSequence element;
         StringBuilder content = new StringBuilder(2000);

         // Process the next available body tag
         while ((element = Readers.forward(reader, BODY_TAGS)) != null) {

            if (element == BODY_END) {
               return;
            } else {
               // Get the next parsable chunk from the stream. This will throttle the amount of the file read in to
               // memory at one time to the smallest area that will provide all the necessary context.
               content.setLength(0);
               content.append(element);

               boolean emptyTagWithAttrs = false;
               // If the tag had attributes, check that it isn't empty
               if (element == PARAGRAPH_TAG_WITH_ATTRS || element == TABLE_TAG_WITH_ATTRS) {
                  if (Readers.forward(reader, (Appendable) content, ">") == null) {
                     throwFileFormatError(importFile, "did not find expected end of tag");
                  }
                  emptyTagWithAttrs = content.toString().endsWith("/>");
               }

               if (element == PARAGRAPH_TAG || (!emptyTagWithAttrs && element == PARAGRAPH_TAG_WITH_ATTRS)) {
                  Readers.xmlForward(reader, (Appendable) content, "w:p");
               } else if (element == TABLE_TAG || (!emptyTagWithAttrs && element == TABLE_TAG_WITH_ATTRS)) {
                  Readers.xmlForward(reader, (Appendable) content, "w:tbl");
               } else if (element != PARAGRAPH_TAG_WITH_ATTRS && element != TABLE_TAG_WITH_ATTRS && element != PARAGRAPH_TAG_EMPTY && element != TABLE_TAG_EMPTY) {
                  throw new IllegalStateException("Unexpected element returned");
               }

               // Word places proofErr tags in manners discontigous with the standard XML tree so only some
               // of them get picked up causing a misbalance from what Word expects, and effectively corrupting
               // the content as far as Word is concerned, so just remove all of them and let word recompute
               // them if it is really that concerned about our grammar
               content = new StringBuilder(proofErrTagKiller.matcher(content).replaceAll(""));

               // forceBody doesn't reset per paragraph
               forcePrimaryType = false;
               headerNumber = "";
               listIdentifier = "";
               paragraphStyle = null;
               parseContentDetails(content, new Stack<String>());
               handler.processContent(forceBody, forcePrimaryType, headerNumber, listIdentifier, paragraphStyle,
                     content.toString(), element == PARAGRAPH_TAG);
            }
         }
      } finally {
         handler.dispose();
      }

      throwFileFormatError(importFile, "did not find expected end of body tag");
   }

   private void parseContentDetails(CharSequence content, Stack<String> parentElementNames) {

      Matcher matcher = internalAttributeElementsPattern.matcher(content);

      String elementNamespace;
      String elementName;
      String elementAttributes;
      String elementContent;
      while (matcher.find()) {
         elementName = matcher.group(ELEMENT_NAME_GROUP);
         elementNamespace = matcher.group(NAMESPACE_GROUP);
         elementAttributes = matcher.group(ATTRIBUTE_BLOCK_GROUP) == null ? "" : matcher.group(ATTRIBUTE_BLOCK_GROUP);
         elementContent = matcher.group(CONTENT_GROUP) == null ? "" : matcher.group(CONTENT_GROUP);

         if (elementName.equals("forceBodyOn")) {
            forceBody = true;
         } else if (elementName.equals("forceBodyOff")) {
            forceBody = false;
         } else if (elementName.equals("pStyle")) {
            paragraphStyle = getAttributeValue("w:val", elementAttributes);
         } else if (elementName.equals("forcePrimaryType")) {
            forcePrimaryType = true;
         } else if (elementNamespace.startsWith("w") && elementName.equals("t")) {
            String numberCandidate = getAttributeValue("wx:val", elementAttributes);

            reqNumberMatcher.reset(numberCandidate);
            reqListMatcher.reset(numberCandidate);

            if (reqNumberMatcher.matches()) {
               if (WordUtil.isHeadingStyle(paragraphStyle)) {
                  headerNumber = numberCandidate;
                  if (headerNumber.endsWith(".0")) {
                     headerNumber = headerNumber.substring(0, headerNumber.length() - 2);
                  }
               }
            } else if (reqListMatcher.matches()) {
               if (isListStyle(parentElementNames)) {
                  listIdentifier =
                        processListId(Integer.parseInt(getAttributeValue("w:val", elementAttributes)), numberCandidate);
               }
            }
         }

         parentElementNames.push(elementName);
         parseContentDetails(elementContent, parentElementNames);
         parentElementNames.pop();
      }
   }

   private static final String getAttributeValue(String attributeName, String attributeStorage) {

      attributeName += "=\"";

      int index = attributeStorage.indexOf(attributeName);
      if (index == -1) {
         return "";
      } else {
         int startIndex = index + attributeName.length();
         return attributeStorage.substring(startIndex, attributeStorage.indexOf('"', startIndex + 1)).trim();
      }
   }

   private static final void throwFileFormatError(File file, String msg) {
      throw new IllegalArgumentException("File " + file.getName() + " not of expected format: " + msg);
   }

   private boolean isListStyle(Stack<String> parentElementNames) {
      Iterator<String> iter = parentElementNames.iterator();

      return iter.hasNext() && iter.next().equals("ilvl") && iter.hasNext() && iter.next().equals("listPr");
   }

   @SuppressWarnings("unchecked")
   private String processListId(int currentDepthNumber, String numberCandidate) throws IllegalArgumentException {
      String id = "";

      if (currentDepthNumber == 0) {
         currentListStack.clear();

         currentListStack.push(numberCandidate);
         clonedCurrentListStack = (Stack) currentListStack.clone();
         lastDepthNumber = currentDepthNumber;
      } else {

         for (int i = currentDepthNumber; i <= lastDepthNumber; i++) {
            currentListStack.pop();
         }

         lastDepthNumber = currentDepthNumber;
         currentListStack.push(numberCandidate);
         clonedCurrentListStack = (Stack) currentListStack.clone();
      }
      while (!clonedCurrentListStack.empty()) {
         id = clonedCurrentListStack.pop() + id;
      }

      if (currentDepthNumber > maxExtractionDepth) {
         return null;
      }
      return id.replaceAll("\\)", ".");
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.Import.ArtifactExtractor#getFileFilter()
    */
   public FileFilter getFileFilter() {
      return new FileFilter() {
         public boolean accept(File file) {
            return file.isDirectory() || (file.isFile() && file.getName().endsWith(".xml"));
         }
      };
   }
}