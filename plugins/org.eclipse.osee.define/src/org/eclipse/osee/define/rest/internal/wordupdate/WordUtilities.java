/*********************************************************************
 * Copyright (c) 2015 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.define.rest.internal.wordupdate;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.eclipse.osee.framework.jdk.core.text.change.ChangeSet;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.jdk.core.util.xml.Xml;
import org.w3c.dom.Element;

/**
 * @author David W. Miller
 */
public class WordUtilities {

   public static final String CHANGE_TAG = "[*] ";
   public static final String CHANGE_TAG_WORDML =
      "<w:r><w:rPr><w:color w:val=\"#FF0000\"/></w:rPr><w:t>" + CHANGE_TAG + "</w:t></w:r>";
   public static final String LISTNUM_FIELD_HEAD = "<w:pPr><w:rPr><w:vanish/></w:rPr></w:pPr>";
   public static final String BODY_START = "<w:body>";
   public static final String BODY_END = "</w:body>";
   private static final Pattern binIdPattern = Pattern.compile("wordml://(.+?)[.]");
   private static final Pattern tagKiller = Pattern.compile("<.*?>", Pattern.DOTALL | Pattern.MULTILINE);
   private static final Pattern paragraphPattern = Pattern.compile("<w:p( .*?)?>");
   private static final Pattern referencePattern = Pattern.compile("(_Ref[0-9]{9}|Word\\.Bookmark\\.End)");
   private static int bookMarkId = 1000;
   private static UpdateBookmarkIds updateBookmarkIds = new UpdateBookmarkIds(bookMarkId);
   private static String newLineChar = ">(\\r|\\n|\\r\\n)<";

   public static byte[] getFormattedContent(Element formattedItemElement) throws XMLStreamException {
      ByteArrayOutputStream data = new ByteArrayOutputStream(1024);
      XMLStreamWriter xmlWriter = null;
      try {
         xmlWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(data, "UTF-8");
         for (Element e : Jaxp.getChildDirects(formattedItemElement)) {
            Jaxp.writeNode(xmlWriter, e, false);
         }
      } finally {
         if (xmlWriter != null) {
            xmlWriter.flush();
            xmlWriter.close();
         }
      }
      return data.toByteArray();
   }

   public static String textOnly(String str) {
      str = paragraphPattern.matcher(str).replaceAll(" ");
      str = tagKiller.matcher(str).replaceAll("").trim();
      return Xml.unescape(str).toString();
   }

   public static String referencesOnly(String content) {
      List<String> references = new ArrayList<>();

      Matcher referenceMatcher = referencePattern.matcher(content);
      while (referenceMatcher.find()) {
         String reference = referenceMatcher.group(1);
         references.add(reference);
      }

      StringBuilder sb = new StringBuilder();
      for (String reference : references) {
         sb.append(reference);
         sb.append("\n");
      }

      return sb.toString();
   }

   public static boolean isHeadingStyle(String paragraphStyle) {
      if (paragraphStyle == null) {
         return false;
      } else {
         String style = paragraphStyle.toLowerCase();
         return style.startsWith("heading") || style.startsWith("toc") || style.startsWith("outline");
      }
   }

   /**
    * @return the content with the bin data ID being reassigned. Note: The bin data Id needs to be reassigned to allow
    * multi edits of artifacts with images. Else if 2 images have the same ID the first image will be printed duplicate
    * times.
    */
   public static String reassignBinDataID(String content) {
      ChangeSet changeSet = new ChangeSet(content);
      Map<String, String> guidMap = new HashMap<>();

      Matcher binIdMatcher = binIdPattern.matcher(content);
      boolean atLeastOneMatch = false;
      while (binIdMatcher.find()) {
         atLeastOneMatch = true;
         String oldName = binIdMatcher.group(1);

         String guid = guidMap.get(oldName);
         if (guid == null) {
            guid = GUID.create();
            guidMap.put(oldName, guid);
         }

         changeSet.replace(binIdMatcher.start(1), binIdMatcher.end(1), guid);
      }

      if (atLeastOneMatch) {
         return changeSet.toString();
      }
      return content;
   }

   /**
    * @return the content with the ending bookmark IDs being reassigned to a unique number. This is done to ensure all
    * versions of MS Word will function correctly.
    */
   public static String reassignBookMarkID(String content) {
      return updateBookmarkIds.fixTags(content);
   }

   /**
    * Removes all new lines, CRLF, CR, or LF from the content in between 2 tags.
    *
    * @param content
    * @return content
    */
   public static String removeNewLines(String content) {
      return content.replaceAll(newLineChar, "><");
   }

   /**
    * Currently OSEE is using the above change tag to signify changes on that content. This method is used to append
    * that tag along with red text formatting. The WordML must be added inside the first paragraph tag in the content in
    * order to achieve proper formatting.
    */
   public static String appendInlineChangeTag(String content) {
      Matcher paragraphMatcher = paragraphPattern.matcher(content);
      if (paragraphMatcher.find()) {
         StringBuilder strB = new StringBuilder();
         strB.append(content.substring(0, paragraphMatcher.end()));
         strB.append(CHANGE_TAG_WORDML);
         strB.append(content.substring(paragraphMatcher.end(), content.length()));
         content = strB.toString();
      }
      return content;
   }
}
