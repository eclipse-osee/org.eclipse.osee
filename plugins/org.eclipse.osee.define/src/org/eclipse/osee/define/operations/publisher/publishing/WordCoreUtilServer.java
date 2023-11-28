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

package org.eclipse.osee.define.operations.publisher.publishing;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.eclipse.osee.define.rest.internal.wordupdate.UpdateBookmarkIds;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.publishing.WordCoreUtil;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.orcs.OrcsApi;
import org.w3c.dom.Element;

/**
 * @implNote Methods that are not specific to the OSEE server should be implemented in the class {@link WordCoreUtil}.
 * This class should only implement methods that need OSEE server specific types.
 * @author David W. Miller
 * @author Loren K. Ashley
 */
public class WordCoreUtilServer {

   public static final String LISTNUM_FIELD_HEAD = "<w:pPr><w:rPr><w:vanish/></w:rPr></w:pPr>";
   public static final String BODY_START = "<w:body>";
   public static final String BODY_END = "</w:body>";
   private static final Pattern referencePattern = Pattern.compile("(_Ref[0-9]{9}|Word\\.Bookmark\\.End)");
   private static int bookMarkId = 1000;
   private static UpdateBookmarkIds updateBookmarkIds = new UpdateBookmarkIds(bookMarkId);
   //private static String newLineChar = ">(\\r|\\n|\\r\\n)<";

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

   /**
    * This method returns the class HashMap variable applicabilityTokens to ensure that the map is loaded once needed.
    * The variable will stay null if this method is never called. This is meant to increase efficiency of applicability
    * checks
    */

   public static Map<ApplicabilityId, ApplicabilityToken> getApplicabilityTokens(OrcsApi orcsApi, BranchId branchId) {

      //@formatter:off
      return
         orcsApi
            .getQueryFactory()
            .applicabilityQuery()
            .getApplicabilityTokens( branchId )
            .entrySet()
            .stream()
            .collect
               (
                  Collectors.toMap
                     (
                        ( entrySet ) -> ApplicabilityId.valueOf( entrySet.getKey() ),
                        Entry::getValue
                     )
               );
         //@formatter:on
   }

   /**
    * Gets the page orientation from the <code>ArtifactReadable</code>'s {@link CoreAttributeTypes#PageOrientation}
    * attribute. The {@link WordCoreUtil.pageType#getDefault()} will be returned if unable to read the artifact's
    * attribute or if the artifact is {@link ArtifactReadable#SENTINEL}.
    *
    * @param artifactReadable the artifact to extract the page orientation from.
    * @return the page orientation.
    */

   public static WordCoreUtil.pageType getPageOrientation(ArtifactReadable artifactReadable) {

      var defaultPageType = WordCoreUtil.pageType.getDefault();

      if (artifactReadable.isInvalid()) {
         return defaultPageType;
      }

      if (!artifactReadable.isAttributeTypeValid(CoreAttributeTypes.PageOrientation)) {
         return defaultPageType;
      }

      try {
         var pageTypeString =
            artifactReadable.getSoleAttributeAsString(CoreAttributeTypes.PageOrientation, defaultPageType.name());

         return WordCoreUtil.pageType.fromString(pageTypeString);

      } catch (Exception e) {

         return defaultPageType;
      }
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
    * @return the content with the ending bookmark IDs being reassigned to a unique number. This is done to ensure all
    * versions of MS Word will function correctly.
    */
   public static CharSequence reassignBookMarkID(CharSequence content) {
      return WordCoreUtilServer.updateBookmarkIds.fixTags(content);
   }

   /**
    * Removes all new lines, CRLF, CR, or LF from the content in between 2 tags.
    *
    * @param content
    * @return content
    */
   //   private static String removeNewLines(String content) {
   //      return content.replaceAll(newLineChar, "><");
   //   }

}
