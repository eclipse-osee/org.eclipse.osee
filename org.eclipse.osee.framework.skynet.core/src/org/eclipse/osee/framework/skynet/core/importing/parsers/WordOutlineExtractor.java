/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.importing.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.util.Iterator;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.jdk.core.util.Readers;
import org.eclipse.osee.framework.skynet.core.importing.operations.RoughArtifactCollector;
import org.eclipse.osee.framework.skynet.core.word.WordUtil;

/**
 * @author Andrew M. Finkbeiner
 * @author Robert A. Fisher
 */
public class WordOutlineExtractor extends AbstractArtifactExtractor {
   private static final String PARAGRAPH_TAG_WITH_ATTRS = "<w:p ";
   private static final String PARAGRAPH_TAG_EMPTY = "<w:p/>";
   private static final String PARAGRAPH_TAG = "<w:p>";
   private static final String TABLE_TAG_WITH_ATTRS = "<w:tbl ";
   private static final String TABLE_TAG_EMPTY = "<w:tbl/>";
   private static final String TABLE_TAG = "<w:tbl>";
   private static final CharSequence[] BODY_TAGS =
         new CharSequence[] {PARAGRAPH_TAG, PARAGRAPH_TAG_EMPTY, PARAGRAPH_TAG_WITH_ATTRS, TABLE_TAG, TABLE_TAG_EMPTY,
               TABLE_TAG_WITH_ATTRS, WordUtil.BODY_END};

   // A regex for reading xml elements. Assumes that an element never has a descendant with the same name as itself
   private static final Pattern internalAttributeElementsPattern =
         Pattern.compile("<((\\w+:)?(\\w+))(\\s+.*?)((/>)|(>(.*?)</\\1>))",
               Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
   private static final Pattern proofErrTagKiller = Pattern.compile("</?w:proofErr.*?/?>");
   private static final int NAMESPACE_GROUP = 2;
   private static final int ELEMENT_NAME_GROUP = 3;
   private static final int ATTRIBUTE_BLOCK_GROUP = 4;
   private static final int CONTENT_GROUP = 8;

   private final Matcher reqNumberMatcher;
   private final Matcher reqListMatcher;
   private final Stack<String> currentListStack;
   private final int maxExtractionDepth;

   private Stack<String> clonedCurrentListStack;
   private int lastDepthNumber;
   private String headerNumber;
   private String listIdentifier;
   private boolean forceBody;
   private boolean forcePrimaryType;
   private String paragraphStyle;

   public WordOutlineExtractor() {
      this.headerNumber = "";
      this.listIdentifier = "";
      this.reqNumberMatcher = Pattern.compile("(\\d+\\.)*(\\d+\\.?)\\s*").matcher("");
      this.reqListMatcher = Pattern.compile("\\w+\\)", Pattern.CASE_INSENSITIVE).matcher("");
      this.currentListStack = new Stack<String>();
      this.clonedCurrentListStack = new Stack<String>();
      this.maxExtractionDepth = 0;
   }

   @Override
   public String getName() {
      return "Word Outline";
   }

   @Override
   public String getDescription() {
      return "Extract data from a Word XML file with an outline, making an artifact for each outline numbered section.";
   }

   @Override
   public boolean isDelegateRequired() {
      return true;
   }

   @Override
   public boolean usesTypeList() {
      return true;
   }

   @Override
   public FileFilter getFileFilter() {
      return new FileFilter() {
         public boolean accept(File file) {
            return file.isDirectory() || file.isFile() && file.getName().endsWith(".xml");
         }
      };
   }

   private void handleFormatError(URI source, String message) throws OseeCoreException {
      throw new OseeStateException(String.format("File format error: %s in file [%s]", message, source.getPath()));
   }

   @Override
   protected void extractFromSource(URI source, RoughArtifactCollector collector) throws Exception {
      Reader reader = null;
      try {
         reader = new BufferedReader(new InputStreamReader(source.toURL().openStream(), "UTF-8"));

         if (Readers.forward(reader, WordUtil.BODY_START) == null) {
            handleFormatError(source, "no start of body tag");
         }
         CharSequence element;
         StringBuilder content = new StringBuilder(2000);

         // Process the next available body tag
         while ((element = Readers.forward(reader, BODY_TAGS)) != null) {

            if (element == WordUtil.BODY_END) {
               return;
            } else {
               // Get the next parse-able chunk from the stream. This will throttle the amount of the file read in to
               // memory at one time to the smallest area that will provide all the necessary context.
               content.setLength(0);
               content.append(element);

               boolean emptyTagWithAttrs = false;
               // If the tag had attributes, check that it isn't empty
               if (element == PARAGRAPH_TAG_WITH_ATTRS || element == TABLE_TAG_WITH_ATTRS) {
                  if (Readers.forward(reader, (Appendable) content, ">") == null) {
                     handleFormatError(source, "did not find expected end of tag");
                  }
                  emptyTagWithAttrs = content.toString().endsWith("/>");
               }

               if (element == PARAGRAPH_TAG || !emptyTagWithAttrs && element == PARAGRAPH_TAG_WITH_ATTRS) {
                  Readers.xmlForward(reader, content, "w:p");
               } else if (element == TABLE_TAG || !emptyTagWithAttrs && element == TABLE_TAG_WITH_ATTRS) {
                  Readers.xmlForward(reader, content, "w:tbl");
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
               try {
                  getDelegate().processContent(collector, forceBody, forcePrimaryType, headerNumber, listIdentifier,
                        paragraphStyle, content.toString(), element == PARAGRAPH_TAG);
               } catch (OseeCoreException ex) {
                  throw new OseeWrappedException(String.format("Error processing: [%s]", source.toASCIIString()), ex);
               }
            }
         }

         handleFormatError(source, "did not find expected end of body tag");
      } finally {
         if (reader != null) {
            reader.close();
         }
      }
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
         clonedCurrentListStack = (Stack<String>) currentListStack.clone();
         lastDepthNumber = currentDepthNumber;
      } else {

         for (int i = currentDepthNumber; i <= lastDepthNumber; i++) {
            currentListStack.pop();
         }

         lastDepthNumber = currentDepthNumber;
         currentListStack.push(numberCandidate);
         clonedCurrentListStack = (Stack<String>) currentListStack.clone();
      }
      while (!clonedCurrentListStack.empty()) {
         id = clonedCurrentListStack.pop() + id;
      }

      if (currentDepthNumber > maxExtractionDepth) {
         return null;
      }
      return id.replaceAll("\\)", ".");
   }

}