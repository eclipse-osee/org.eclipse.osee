/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.rest.importing.parsers;

import java.io.File;
import java.io.FileFilter;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import org.eclipse.define.api.importing.RoughArtifact;
import org.eclipse.define.api.importing.RoughArtifactCollector;
import org.eclipse.osee.define.rest.internal.importing.NormalizeHtml;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsTypes;
import org.eclipse.osee.orcs.data.EnumType;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

/**
 * @author Marc A. Potter
 */
public class DoorsArtifactExtractor extends AbstractArtifactExtractor {

   private final Vector<String> postProcessImages = new Vector<>();
   private String uriDirectoryName = "";
   private final static String NAME_TAG = "<a name=";
   private final static String CLOSING_A_TAG = "</a>";
   private final static String BR_TAG = "<br />";
   private final static String BODY_START_TAG = "<body>";
   private final static String BODY_END_TAG = "</body>";
   private final static String LIST_ITEM_TAG = "<li>";
   private final static String LIST_ITEM_END_TAG = "</li>";
   private final static String IMAGE_BASE_NAME = "Image Content_";
   private final String BLANK_HTML_LINE = "<br />";
   private static String VERIFICATION_ACCEPTANCE_CRITERIA = "Verification Acceptance Criteria:";
   private static String CRITERIA = "Criteria:";
   private final static String[] VERIFICATION_KEYWORDS = {
      "Effectivity:",
      "Configuration:",
      "Verf Method:",
      "Verification Method:",
      "Verf Level:",
      "Verf Location:",
      "Verification Environment:",
      "Verf Type:",
      "Verified By:",
      VERIFICATION_ACCEPTANCE_CRITERIA,
      CRITERIA};
   private final static AttributeTypeToken[] FIELD_TYPE = {
      null,
      null,
      CoreAttributeTypes.QualificationMethod,
      CoreAttributeTypes.QualificationMethod,
      CoreAttributeTypes.VerificationLevel,
      CoreAttributeTypes.VerificationEvent,
      CoreAttributeTypes.VerificationEvent,
      CoreAttributeTypes.VerificationEvent,
      null,
      null,
      null}; // Last two is actually a string

   @Override
   public String getDescription() {
      return "Extract artifacts from a DOORS HTML table export file ";
   }

   @Override
   public FileFilter getFileFilter() {
      return new FileFilter() {
         @Override
         public boolean accept(File file) {
            return file.isDirectory() || file.isFile() && file.getName().endsWith(".htm");
         }
      };
   }

   @Override
   public String getName() {
      return "DOORS HTML Table export file";
   }

   @Override
   public boolean usesTypeList() {
      return false;
   }

   @Override
   public XResultData extractFromSource(OrcsApi orcsApi, XResultData results, URI source, RoughArtifactCollector collector) throws Exception {
      doExtraction(orcsApi, results, source, collector, "");
      return results;
   }

   public void doExtraction(OrcsApi orcsApi, XResultData results, URI source, RoughArtifactCollector collector, String documentApplicabilty) {

      InputStream htmlStream = null;
      try {
         // this is used later with the images to set the image URI
         String fileName = "file://" + source.getPath();
         uriDirectoryName = fileName.substring(0, fileName.lastIndexOf('/') + 1);
         // we only need to post process the images, so we will add the uri of each
         // rough artifact that has an image to the list for post processing
         postProcessImages.clear();
         htmlStream = source.toURL().openStream();
         DoorsTableRowCollector rowCollector = new DoorsTableRowCollector(this);
         Document doc = Jsoup.parse(htmlStream, "UTF-8", "");
         Element body = doc.body();

         for (Node cNodes : body.childNodes()) {

            for (Node subNodes : cNodes.childNodes()) {

               if (subNodes.nodeName().compareTo("tbody") == 0) {
                  // for normalized doors input, there is a table that wraps all of the other content
                  // the first row in the table will have all of the column names
                  // the other rows will have the data elements that need to be analyzed and converted to artifacts

                  for (Node tableRow : subNodes.childNodes()) {
                     // jsoup parses the row ends as text nodes (not elements)
                     // each element is one row of the doors output table
                     if (tableRow instanceof Element) {
                        rowCollector.addRawRow(tableRow);
                     }
                  }
               }
            }
         }
         rowCollector.createArtifacts(orcsApi, collector);
      } catch (Exception ex) {
         results.error(ex.toString());
      } finally {
         Lib.close(htmlStream);
      }
   }

   @Override
   public boolean artifactCreated(TransactionBuilder transaction, ArtifactId theArtifact, RoughArtifact source) {
      boolean toReturn = false;
      String content = "";
      Collection<URI> imageURIs = source.getURIAttributes();
      if (imageURIs.size() > 0) {
         URI uri = source.getURIAttributes().iterator().next();
         if (uri != null) {
            content = uri.toASCIIString();
         }
         if (postProcessImages.contains(content)) {
            /**********************************************************
             * need to modify the HTML so the image references the data stored in the artifact.
             **************************************/
            try {
               List<?> Ids = getAttributes(source.getOrcsApi(), transaction.getBranch(), theArtifact,
                  CoreAttributeTypes.ImageContent).getList();
               List<?> HTML = getAttributes(source.getOrcsApi(), transaction.getBranch(), theArtifact,
                  CoreAttributeTypes.HtmlContent).getList();
               transaction.deleteAttributes(theArtifact, CoreAttributeTypes.HtmlContent);
               for (Object htmlValObj : HTML) {
                  if (htmlValObj instanceof String) {
                     String htmlVal = (String) htmlValObj;
                     int iCount = 0;
                     for (Object imageNumberObj : Ids) {
                        if (imageNumberObj instanceof Integer) {
                           Integer imageNumber = (Integer) imageNumberObj;
                           htmlVal =
                              htmlVal.replaceAll(IMAGE_BASE_NAME + Integer.toString(iCount), imageNumber.toString());
                           iCount++;
                           toReturn = true;
                        }
                     }
                     transaction.createAttribute(theArtifact, CoreAttributeTypes.HtmlContent, htmlVal);
                  }
               }
            } catch (OseeCoreException ex) {
               source.getResults().error(ex.toString());

            }
         }
      }
      return toReturn;
   }

   private ResultSet<?> getAttributes(OrcsApi orcsApi, BranchId branch, ArtifactId theArtifact, AttributeTypeToken attr) {
      return orcsApi.getQueryFactory().fromBranch(branch).andId(theArtifact).getArtifact().getAttributes(attr);
   }

   public void foundStartOfWorksheet(String sheetName) {
      // Nothing to do in DOORS file Leave in in case this changes (it is
      // called at start)
   }

   public String processList(String inputValue) {
      inputValue = normalizeHtml(inputValue);
      inputValue = inputValue.replaceAll("\\s+", " ");
      /**************************************************************************************
       * The way Doors export works with lists is that there is badly spaced <div> statements -- remove them
       */
      inputValue = inputValue.replaceAll("<div>", "");
      inputValue = inputValue.replaceAll("</div>", "");
      /*********************************************************************************
       * Remove extra blank lines too
       */
      inputValue = inputValue.replaceAll(BLANK_HTML_LINE + "\\s+" + BLANK_HTML_LINE, BLANK_HTML_LINE);
      StringBuilder returnString = new StringBuilder(inputValue.trim());
      //@formatter:off
      /********************************************************************************
       * The Doors export outputs a list as pure text (e.g. a. list item). Convert this to an HTML list
       *
       * Assumptions:
       * 1) The format of the list is either a. or 1.
       * 2) There is no embedded 1. or a. in the text of the list.
       *    That is if 1. shows up in a alpha list it means there is a new list starting or if b.
       *    shows up after a. then it the next item
       */
      //@formatter:on
      // find first text char
      char[] theChars = stringBuilderToChars(returnString);
      int[] startEnd = findEndOfList(theChars, 0);
      int iPos = startEnd[0];
      int endOfList = startEnd[1];
      int startOfNextList = startEnd[2];
      boolean isNumeric = Character.isDigit(theChars[iPos]);
      boolean isLowerCase = Character.isLowerCase(theChars[iPos]);
      int currentNumber = 0;
      String currentLetter = "";
      if (isNumeric) {
         int startPos = iPos;
         while (theChars[iPos] != '.' && theChars[iPos] != ')') {
            iPos++;
         }
         String theNumber = returnString.substring(startPos, iPos);
         currentNumber = Integer.parseInt(theNumber);
      } else {
         int startPos = iPos;
         while (theChars[iPos] != '.' && theChars[iPos] != ')') {
            iPos++;
         }
         currentLetter = returnString.substring(startPos, iPos);
      }
      int nextItem = 0;
      returnString.delete(iPos - 1, iPos + 1);
      endOfList -= 2;
      startOfNextList -= 2;
      String insertValue = null;

      if (isNumeric) {
         insertValue = "<ol>";
      } else if (isLowerCase) {
         insertValue = "<ol type = \"a\">";
      } else {
         insertValue = "<ol type = \"A\">";
      }
      returnString.insert(iPos - 1, insertValue);
      if (iPos < endOfList) {
         endOfList = endOfList + insertValue.length();
         startOfNextList = startOfNextList + insertValue.length();
      }
      iPos += insertValue.length();
      int adjust = removeForcedSpaces(returnString, iPos - 1, false);
      startOfNextList -= adjust;
      endOfList -= adjust;
      listData theListData = new listData();
      boolean lastWasSublist = false;
      while (nextItem != -1) {
         if (theListData.getNewList()) {
            lastWasSublist = true;
         } else {
            lastWasSublist = false;
            returnString.insert(iPos - 1, LIST_ITEM_TAG);
            if (iPos < endOfList) {
               endOfList = endOfList + LIST_ITEM_TAG.length();
               startOfNextList = startOfNextList + LIST_ITEM_TAG.length();
            }
            iPos += LIST_ITEM_TAG.length() - 1;
         }
         adjust = removeForcedSpaces(returnString, nextItem + LIST_ITEM_TAG.length(), false);
         startOfNextList -= adjust;
         endOfList -= adjust;
         theChars = stringBuilderToChars(returnString);
         nextItem = findNextListItem(theChars, iPos, isNumeric, isLowerCase, currentNumber, currentLetter, theListData);
         if (nextItem == -1) {
            break;
         }

         if (theListData.getNewList()) {
            int startPoint = nextItem < startOfNextList ? nextItem : startOfNextList;
            int delta = removeForcedSpaces(returnString, startPoint - 1, true);
            if (delta > 0) {
               theChars = stringBuilderToChars(returnString);
               startPoint -= delta;
               endOfList -= delta;
            }
            String theSublist = returnString.substring(0, startPoint);
            int end = theListData.getNextItem() - delta;
            if (theListData.getNextItem() != -1) {
               theListData.setNextItem(end);
            }
            if (end >= returnString.length()) {
               end = returnString.length() - 1;
            }
            String theRawSublist = new String(theChars, startPoint, end - startPoint + 1);
            int initialLen = theRawSublist.length();
            theRawSublist = processList(theRawSublist);
            theSublist += theRawSublist;
            theSublist += LIST_ITEM_END_TAG;
            delta = theRawSublist.length() - initialLen + LIST_ITEM_END_TAG.length();
            endOfList += delta;
            startOfNextList += delta;
            if (theListData.getNextItem() != -1 && theListData.getNextItem() < returnString.length() - 1) {
               theSublist += returnString.substring(theListData.getNextItem() + 1);
            }
            returnString.delete(0, returnString.length());
            returnString.append(theSublist);
         } else {
            if (isNumeric) {
               currentNumber =
                  Integer.valueOf(returnString.substring(nextItem, nextItem + theListData.getItemLength() - 1));
            } else {
               currentLetter = returnString.substring(nextItem, nextItem + theListData.getItemLength() - 1);
            }
            returnString.delete(nextItem, nextItem + theListData.getItemLength());
            endOfList -= theListData.getItemLength();
            startOfNextList -= theListData.getItemLength();
            /*************************************************************
             * Since we are converting a line of text, there is a blank line after it. Delete the <BR>
             * </BR>
             */
            if (!lastWasSublist) {
               int end = nextItem;
               if (end > returnString.length()) {
                  end = returnString.length();
               }
               String test = returnString.substring(0, end);
               int lastPoint = test.lastIndexOf(BLANK_HTML_LINE);
               if (lastPoint != -1) {
                  returnString.delete(lastPoint, end);
                  int delta = test.length() - lastPoint;
                  endOfList -= delta;
                  nextItem -= delta;
                  startOfNextList -= delta;
               }
            }
            if (!lastWasSublist) {
               returnString.insert(nextItem, LIST_ITEM_END_TAG);
               if (nextItem < endOfList) {
                  endOfList = endOfList + LIST_ITEM_END_TAG.length();
                  startOfNextList = startOfNextList + LIST_ITEM_END_TAG.length();
               }
               nextItem = nextItem + LIST_ITEM_END_TAG.length();
            }
            iPos = nextItem + 1;
         }
         theChars = stringBuilderToChars(returnString);
      }
      // find the insertion point for list end
      String tokenToInsert = LIST_ITEM_END_TAG + "</ol>";
      if (theListData.getNewList()) {
         tokenToInsert = "</ol>";
      }

      if (endOfList < returnString.length()) {
         returnString.insert(endOfList, tokenToInsert);
      } else {
         // verify the list doesn't end with <BR></BR>
         String test = returnString.toString();
         int lastPoint = test.lastIndexOf(BLANK_HTML_LINE);
         if (lastPoint == test.length() - BLANK_HTML_LINE.length()) {
            returnString.delete(lastPoint, returnString.length());
         }
         returnString.append(tokenToInsert);
      }

      return returnString.toString();
   }

   private int[] findEndOfList(char[] theChars, int startPoint) {
      int iPos = startPoint;
      int[] iReturn = {0, theChars.length, theChars.length};
      int tagCount = 0;
      boolean notFirst = false;
      boolean foundNonTagItem = false;
      while (iPos < theChars.length) {
         while (iPos < theChars.length && (theChars[iPos] == '\t' || theChars[iPos] == '\n' || Character.isWhitespace(
            theChars[iPos]))) {
            iPos++;
         }
         if (iPos >= theChars.length) {
            iReturn[1] = theChars.length;
            break;
         }
         if (theChars[iPos] == '<') {
            int startofCloseTag = iPos;
            iPos++;
            if (theChars[iPos] == '/') {
               tagCount--;
               while (iPos < theChars.length && theChars[iPos] != '>') {
                  iPos++;
               }
               if (tagCount == 0 && foundNonTagItem || tagCount < 0) {
                  iReturn[1] = startofCloseTag;
                  iReturn[2] = iPos;
                  while (iReturn[2] < theChars.length && theChars[iReturn[2]] != '<') {
                     iReturn[2] = iReturn[2] + 1;
                  }
                  break;
               }
            } else {
               tagCount++;
            }
            while (iPos < theChars.length && theChars[iPos] != '>') {
               iPos++;
            }
            iPos++;
         } else if (notFirst) {
            if (!foundNonTagItem) {
               iReturn[0] = iPos;
               foundNonTagItem = true;
            }
            if (tagCount == 0) {
               break;
            } else {
               // find next tag
               while (iPos < theChars.length && theChars[iPos] != '<') {
                  iPos++;
               }
               iReturn[1] = iPos - 1;
               // find the end of the tag
               iReturn[2] = iPos;
               while (iReturn[2] < theChars.length && theChars[iReturn[2]] != '>') {
                  iReturn[2] = iReturn[2] + 1;
               }
               iReturn[2] = iReturn[2] + 1;
            }
         } else {
            // no opening tags, therefore list not enclosed in tags.
            iPos = theChars.length;
         }
         notFirst = true;
      }
      return iReturn;
   }

   static char[] stringBuilderToChars(StringBuilder sb) {
      char[] returnArray = new char[sb.length()];
      sb.getChars(0, sb.length(), returnArray, 0);
      return returnArray;
   }

   private class listData {
      private boolean newList;
      private int itemLength;
      private int nextItem;

      public listData() {
         this.newList = false;
         this.itemLength = 0;
      }

      public int getItemLength() {
         return itemLength;
      }

      public int getNextItem() {
         return nextItem;
      }

      public boolean getNewList() {
         return newList;
      }

      public void setNextItem(int nextItem) {
         this.nextItem = nextItem;
      }

      public void setItemLength(int itemLength) {
         this.itemLength = itemLength;
      }

      public void setNewList(boolean newList) {
         this.newList = newList;
      }
   }

   private int findNextListItem(char[] theChars, int iPos, boolean isNumeric, boolean isLowerCase, int currentNumber, String currentLetter, listData listData) {
      //@formatter:off
      /****************************************************************************
       * Now the tricky part.  We are looking for
       * 1) <space><next value>.<space or &nbsp; or &#something>
       * 2) <space><next level value>.
       */
      //@formatter:on

      iPos++;
      if (iPos >= theChars.length) {
         return -1;
      }
      StringBuilder asString = new StringBuilder();
      asString.append(theChars, iPos, theChars.length - iPos);
      int aListDot = asString.toString().toLowerCase().indexOf("a.");
      int aListParen = asString.toString().toLowerCase().indexOf("a.");
      int aList = -1;
      if (aListDot == -1) {
         aList = aListParen;
      } else if (aListParen == -1) {
         aList = aListDot;
      } else {
         aList = aListDot < aListParen ? aListDot : aListParen;
      }
      int oneListDot = asString.indexOf("1.");
      int oneListParen = asString.indexOf("1)");
      int oneList = -1;
      if (oneListDot == -1) {
         oneList = oneListParen;
      } else if (aListParen == -1) {
         oneList = oneListDot;
      } else {
         oneList = oneListDot < oneListParen ? oneListDot : oneListParen;
      }

      int nextListItem = -1;
      String nextItem = "";
      if (isNumeric) {
         nextItem = Integer.toString(currentNumber + 1) + ".";
      } else {
         // assume Ascii -- that is, that the letters are contiguous
         byte[] theLetters = null;
         try {
            theLetters = currentLetter.getBytes("UTF-8");
         } catch (UnsupportedEncodingException e) {
            theLetters = currentLetter.getBytes();
         }
         int theCharToChange = theLetters.length - 1;
         if (currentLetter.toLowerCase().charAt(theCharToChange) == 'z') {
            if (theCharToChange > 0) {
               theLetters[theCharToChange - 1]++;
               if (isLowerCase) {
                  theLetters[theCharToChange] = "a".getBytes()[0];
               } else {
                  theLetters[theCharToChange] = "A".getBytes()[0];
               }
            } else {
               byte[] newLetterArray = new byte[theLetters.length + 1];
               for (int i = 0; i < newLetterArray.length; i++) {
                  if (isLowerCase) {
                     newLetterArray[i] = "a".getBytes()[0];
                  } else {
                     newLetterArray[i] = "A".getBytes()[0];
                  }
               }
               theLetters = newLetterArray;
            }
         } else {
            theLetters[0]++;
         }
         nextItem = new String(theLetters) + ".";
      }
      nextListItem = asString.indexOf(nextItem);
      if (nextListItem != -1) {
         // verify this is not just a char and period
         char prev = asString.charAt(nextListItem - 1);
         while (!(Character.isWhitespace(prev) || prev == ';' || prev == '>')) {
            nextListItem = asString.indexOf(nextItem, nextListItem + 1);
            if (nextListItem == -1) {
               break;
            }
            prev = asString.charAt(nextListItem - 1);
         }
      }
      if (aList == -1 && oneList == -1 && nextListItem == -1) {
         return -1;
      }
      aList = aList != -1 ? aList + iPos : theChars.length + 1;
      oneList = oneList != -1 ? oneList + iPos : theChars.length + 1;
      nextListItem = nextListItem != -1 ? nextListItem + iPos : theChars.length + 1;
      int iReturn = aList < oneList ? aList : oneList;
      iReturn = iReturn < nextListItem ? iReturn : nextListItem;
      if (iReturn == nextListItem) {
         listData.setNewList(false);
         listData.setItemLength(nextItem.length());
         listData.setNextItem(nextListItem);
      } else {
         listData.setNewList(true);
         listData.setItemLength(2);
         listData.setNextItem(nextListItem - 1);
      }
      return iReturn;
   }

   public void handleRequirement(String rowValue, RoughArtifact roughArtifact) {
      StringBuffer imageFileList = new StringBuffer("");
      appendToImageList(rowValue, imageFileList);
      rowValue = normalizeHtml(rowValue);
      String imageFile = imageFileList.toString();
      if (!imageFile.isEmpty()) {
         String theImage;
         int comma = 0;
         int imageNumber = 0;
         do {
            String replaceName = "";
            comma = imageFile.indexOf(',');
            if (comma == -1) {
               theImage = uriDirectoryName + imageFile;
               replaceName = imageFile;
               imageFile = " ";
            } else {
               theImage = uriDirectoryName + imageFile.substring(0, comma);
               replaceName = imageFile.substring(0, comma);
               imageFile = imageFile.substring(comma + 1);
            }
            try {
               URI imageURI = new URI(theImage);
               if (roughArtifact != null) {
                  roughArtifact.addAttribute(CoreAttributeTypes.ImageContent.getName(), imageURI);
               }

               rowValue = rowValue.replace(replaceName, IMAGE_BASE_NAME + Integer.toString(imageNumber));
               imageNumber++;
               // put it into the post processing list
               postProcessImages.add(imageURI.toASCIIString());
            } catch (URISyntaxException ex) {
               if (roughArtifact != null) {
                  roughArtifact.getResults().error(ex.toString());
               }
            }
         } while (comma != -1);
      }
      if (Strings.isValid(rowValue) && roughArtifact != null) {
         roughArtifact.addAttribute(CoreAttributeTypes.HtmlContent, rowValue);
      }
   }

   /**********************************************************************
    * @param column value from the Verification type cell
    * @param roughArtifact the artifact being populated
    */
   public void processVerification(OrcsApi orcsApi, String column, RoughArtifact roughArtifact) {
      /**************************************************************
       * The followings possibilities exist for this field 1) Field empty 2) Some/all keywords The keywords may not be
       * filled in. In other words a keyword may be followed by a keyword instead of data.
       */

      String trimmed = clearHTML(column);
      if (trimmed.trim().isEmpty()) {
         // empty
         return;
      }
      /*****************************************************************
       * There are some keywords that do not map-- need them in the list to check if there is data
       */
      for (int i = 0; i < VERIFICATION_KEYWORDS.length; i++) {
         // special case Criteria is a string attribute
         if (FIELD_TYPE[i] == null && !(VERIFICATION_KEYWORDS[i].equals(CRITERIA) || VERIFICATION_KEYWORDS[i].equals(
            VERIFICATION_ACCEPTANCE_CRITERIA))) {
            continue;
         }
         int iStart = trimmed.indexOf(VERIFICATION_KEYWORDS[i]);
         if (iStart != -1) {
            boolean dataFound = true;
            // any data?
            String rest = trimmed.substring(iStart + VERIFICATION_KEYWORDS[i].length());
            rest = rest.trim();
            // is it empty?
            dataFound = !rest.isEmpty();
            for (int j = 0; j < VERIFICATION_KEYWORDS.length && dataFound; j++) {
               dataFound = !rest.startsWith(VERIFICATION_KEYWORDS[j]);
            }
            if (dataFound) {
               // find the data
               int colon = rest.indexOf(':');
               if (colon == -1) {
                  if (VERIFICATION_KEYWORDS[i].equals(CRITERIA) || VERIFICATION_KEYWORDS[i].equals(
                     VERIFICATION_ACCEPTANCE_CRITERIA)) {
                     // special case Criteria is a string attribute
                     roughArtifact.addAttribute("Verification Acceptance Criteria", rest);
                  } else if (FIELD_TYPE[i].equals(CoreAttributeTypes.QualificationMethod)) {
                     parseAndStoreEnum(orcsApi, roughArtifact, rest, CoreAttributeTypes.QualificationMethod);
                  } else if (FIELD_TYPE[i].equals(CoreAttributeTypes.VerificationEvent)) {
                     parseAndStoreEnum(orcsApi, roughArtifact, rest, CoreAttributeTypes.VerificationEvent);
                  } else {
                     roughArtifact.addAttribute(FIELD_TYPE[i], rest);
                  }
               } else {
                  // find the start of the keyword
                  boolean foundKeyword = false;
                  for (int j = 0; j < VERIFICATION_KEYWORDS.length; j++) {
                     int theIndex = rest.indexOf(VERIFICATION_KEYWORDS[j]);
                     if (theIndex != -1 && theIndex == colon - VERIFICATION_KEYWORDS[j].length() + 1) {
                        int index = rest.indexOf(VERIFICATION_KEYWORDS[j]);
                        if (index >= 0) {
                           if (VERIFICATION_KEYWORDS[i].equals(CRITERIA) || VERIFICATION_KEYWORDS[i].equals(
                              VERIFICATION_ACCEPTANCE_CRITERIA)) {
                              // special case Criteria is a string attribute
                              roughArtifact.addAttribute("Verification Acceptance Criteria", rest.substring(0, index));
                           } else if (FIELD_TYPE[i].equals(CoreAttributeTypes.QualificationMethod)) {
                              parseAndStoreEnum(orcsApi, roughArtifact, rest.substring(0, index),
                                 CoreAttributeTypes.QualificationMethod);
                           } else if (FIELD_TYPE[i].equals(CoreAttributeTypes.VerificationEvent)) {
                              parseAndStoreEnum(orcsApi, roughArtifact, rest.substring(0, index),
                                 CoreAttributeTypes.VerificationEvent);
                           } else {
                              roughArtifact.addAttribute(FIELD_TYPE[i], rest.substring(0, index));
                           }
                           foundKeyword = true;
                           break;
                        }
                     }
                  }
                  if (!foundKeyword) {
                     if (VERIFICATION_KEYWORDS[i].equals("Criteria:") || VERIFICATION_KEYWORDS[i].equals(
                        "Verification Acceptance Criteria:")) {
                        // special case Criteria is a string attribute
                        roughArtifact.addAttribute("Verification Acceptance Criteria", rest);
                     } else {
                        roughArtifact.addAttribute(FIELD_TYPE[i], rest);
                     }
                  }
               }
            }
         }
      }
   }

   private void parseAndStoreEnum(OrcsApi orcsApi, RoughArtifact roughArtifact, String data, AttributeTypeToken type) {
      StringTokenizer theTokens = new StringTokenizer(data, " ");
      OrcsTypes orcsTypes = orcsApi.getOrcsTypes();
      EnumType enumType = orcsTypes.getAttributeTypes().getEnumType(type);
      Set<String> theValues = enumType.valuesAsOrderedStringSet();
      String singleItem = "";
      while (theTokens.hasMoreTokens()) {
         singleItem += theTokens.nextToken();
         for (String item : theValues) {
            if (item.equals(singleItem)) {
               roughArtifact.addAttribute(type, singleItem);
               singleItem = "";
               break;
            }
         }
         if (Strings.isValid(singleItem)) {
            singleItem += " ";
         }
      }
      if (Strings.isValid(singleItem)) {
         roughArtifact.addAttribute(type, singleItem);
      }
   }

   /*************************************************************
    * @param inputHTML Input value of the requirements field (as exported from DOORS)
    * @param imageFileList is a comma separated list of image file names in the HTML If there is an <img tag, add the
    * file name of the image file in imageFile
    */
   private void appendToImageList(String inputHTML, StringBuffer imageFileList) {
      String outputHtml = inputHTML;
      String Lower = outputHtml.toLowerCase();
      int img = Lower.indexOf("img ");
      imageFileList.setLength(0);
      boolean first = true;
      while (img != -1) {
         int src = Lower.indexOf("src=", img);
         if (src != -1) {
            src += 4;
            char qte = Lower.charAt(src);
            int iEnd = Lower.indexOf(qte, src + 1);
            if (first) {
               imageFileList.append(inputHTML.substring(src + 1, iEnd));
               first = false;
            } else {
               imageFileList.append("," + inputHTML.substring(src + 1, iEnd));
            }
            img = Lower.indexOf("img ", src);
         } else {
            img = -1;
         }
      }
   }

   /************************************************************
    * @param input input string with HTML tags
    * @return a string with the HTML tags removed
    */
   private String clearHTML(String input) {
      String returnValue = "", processString = input;
      int openBracket = processString.indexOf('<'), closeBracket;
      if (openBracket == -1) {
         returnValue = input;
      }
      while (openBracket >= 0) {
         /************************************************************
          * if the bracket doesn't start the string, copy the start to the return (plus a space). Find the close bracket
          * and continue until all tags are removed
          */
         if (openBracket != 0) {
            returnValue += processString.substring(0, openBracket) + " ";
         }
         closeBracket = processString.indexOf('>');
         if (closeBracket > 0) {
            processString = processString.substring(closeBracket + 1);
         }
         openBracket = processString.indexOf('<');
      }
      returnValue += processString;
      return returnValue;
   }

   private String normalizeHtml(String inputHtml) {

      String returnValue = preprocessHTML(inputHtml);
      returnValue = NormalizeHtml.convertToNormalizedHTML(returnValue, true, true, true);
      int bodyStart = returnValue.indexOf(BODY_START_TAG);
      int bodyEnd = returnValue.indexOf(BODY_END_TAG);
      if (bodyStart != -1) {
         bodyStart += BODY_START_TAG.length();
         if (bodyEnd == -1) {
            bodyEnd = returnValue.length() - 1;
         } else {
            bodyEnd--;
         }
         if (bodyEnd <= bodyStart) {
            returnValue = ""; // no body
         } else {
            returnValue = returnValue.substring(bodyStart, bodyEnd);
         }
      }
      /************************************************************************
       * The DOORS HTML starts with a tag "<a name="something"> -- remove this tag and the closing </a>
       */
      int nameTag = returnValue.indexOf(NAME_TAG);
      while (nameTag != -1) {
         int endTag = returnValue.substring(nameTag).indexOf('>') + nameTag;
         if (endTag > nameTag) {
            endTag = returnValue.indexOf(CLOSING_A_TAG);
            returnValue =
               returnValue.substring(0, nameTag - 1) + returnValue.substring(endTag + CLOSING_A_TAG.length() + 1);
            nameTag = returnValue.indexOf(NAME_TAG);
         } else {
            nameTag = -1;
         }
      }
      /******************************************************************
       * remove any closing <br />
       * tags these are not meaningful
       */
      returnValue = returnValue.trim();
      int brTag = returnValue.toLowerCase().lastIndexOf(BR_TAG);
      while (brTag != -1 && brTag == returnValue.length() - BR_TAG.length()) {
         returnValue = returnValue.substring(0, brTag).trim();
         brTag = returnValue.toLowerCase().lastIndexOf(BR_TAG);
      }

      //@formatter:off
      /************************************************************************************
       * change <br />spacespace
       * to <br />space
       */
      //@formatter:on
      returnValue = returnValue.replaceAll("<br />  ", "<br /> ");
      return returnValue;
   }

   private String preprocessHTML(String inputHTML) {
      String toReturn = inputHTML;
      toReturn = toReturn.replaceAll("\t", " ");
      toReturn = toReturn.replaceAll("<BR></BR>", "<BR />");
      toReturn = toReturn.replaceAll("<br></br>", "<br />");
      return toReturn;
   }

   private int removeForcedSpaces(StringBuilder returnString, int iPos, boolean reverse) {
      /***********************************************************************
       * remove any &nbsp; or &#9 after the start or end of the list -- the HTML list takes care of spacing
       */
      int adjust = 0;
      String nbsp = "&nbsp;", tab = "&#9;";
      if (iPos > 0) {
         if (reverse) {
            char[] theChars = stringBuilderToChars(returnString);
            while (Character.isWhitespace(theChars[iPos])) {
               returnString.delete(iPos, iPos + 1);
               iPos--;
               adjust++;
            }
            int nbspPos = returnString.lastIndexOf(nbsp, iPos);
            int tabPos = returnString.lastIndexOf(tab, iPos);
            while (nbspPos == iPos - nbsp.length() + 1 || tabPos == iPos - tab.length() + 1) {
               if (nbspPos == iPos - nbsp.length() + 1) {
                  returnString.replace(nbspPos, nbspPos + nbsp.length(), "");
                  adjust += nbsp.length();
                  iPos -= nbsp.length();
               } else {
                  returnString.replace(tabPos, tabPos + tab.length(), "");
                  adjust += tab.length();
                  iPos -= tab.length();
               }
               nbspPos = returnString.lastIndexOf(nbsp, iPos);
               tabPos = returnString.lastIndexOf(tab, iPos);
            }
         } else {
            int nbspPos = returnString.indexOf(nbsp, iPos);
            int tabPos = returnString.indexOf(tab, iPos);
            while (nbspPos == iPos || tabPos == iPos) {
               if (nbspPos == iPos) {
                  returnString.replace(nbspPos, nbspPos + nbsp.length(), "");
                  adjust += nbsp.length();
               } else {
                  returnString.replace(tabPos, tabPos + tab.length(), "");
                  adjust += tab.length();
               }
               nbspPos = returnString.indexOf(nbsp, iPos);
               tabPos = returnString.indexOf(tab, iPos);
            }
         }
      }
      return adjust;
   }
}
