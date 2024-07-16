/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.skynet.core.word;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IStatusHandler;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.publishing.WordCoreUtil;
import org.eclipse.osee.framework.jdk.core.text.change.ChangeSet;
import org.eclipse.osee.framework.jdk.core.type.MutableBoolean;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.io.Streams;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoader;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;
import org.eclipse.osee.jdbc.JdbcStatement;

/**
 * Provides utility methods for parsing wordML.
 *
 * @implNote Methods that are not specific to the OSEE client should be implemented in the class {@link WordCoreUtil}.
 * This class should only implement methods that need OSEE client specific types.
 * @author Jeff C. Phillips
 * @author Paul K. Waldfogel
 * @author Loren K. Ashley
 */
public class WordCoreUtilClient {

   private static final Matcher binIdMatcher = Pattern.compile("wordml://(.+?)[.]").matcher("");
   public static final String BODY_END = "</w:body>";
   public static final String BODY_START = "<w:body>";

   private static int bookMarkId = 1000;
   private static final String[] NUMBER =
      new String[] {"Zero", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine"};
   private static final IStatus promptStatus = new Status(IStatus.WARNING, Activator.PLUGIN_ID, 256, "", null);
   private static final Pattern referencePattern = Pattern.compile("(_Ref[0-9]{9}|Word\\.Bookmark\\.End)");

   private static final String SELECT_WORD_VALUES =
      "SELECT attr.content, attr.gamma_id FROM osee_attribute attr, osee_txs txs WHERE attr.art_id=? AND attr.attr_type_id=? AND attr.gamma_id = txs.gamma_id AND txs.branch_id=? ORDER BY attr.gamma_id DESC";
   private static UpdateBookmarkIds updateBookmarkIds = new UpdateBookmarkIds(bookMarkId);
   private static final String wordBody = WordCoreUtilClient.BODY_START + WordCoreUtilClient.BODY_END;
   private static final String wordLeader1 =
      "<?xml version='1.0' encoding='UTF-8' standalone='yes'?>" + "<?mso-application progid='Word.Document'?>";
   private static final String wordLeader2 =
      "<w:wordDocument xmlns:w='http://schemas.microsoft.com/office/word/2003/wordml' xmlns:v='urn:schemas-microsoft-com:vml' xmlns:w10='urn:schemas-microsoft-com:office:word' xmlns:sl='http://schemas.microsoft.com/schemaLibrary/2003/core' xmlns:aml='http://schemas.microsoft.com/aml/2001/core' xmlns:wx='http://schemas.microsoft.com/office/word/2003/auxHint' xmlns:o='urn:schemas-microsoft-com:office:office' xmlns:dt='uuid:C2F41010-65B3-11d1-A29F-00AA00C14882' xmlns:wsp='http://schemas.microsoft.com/office/word/2003/wordml/sp2' xmlns:ns0='http://www.w3.org/2001/XMLSchema' xmlns:ns1='http://eclipse.org/artifact.xsd' xmlns:st1='urn:schemas-microsoft-com:office:smarttags' w:macrosPresent='no' w:embeddedObjPresent='no' w:ocxPresent='no' xml:space='preserve'>";
   private static final String wordTrailer = "</w:wordDocument> ";
   private static final String emptyDocumentContent = wordLeader1 + wordLeader2 + wordBody + wordTrailer;

   private static final String LOAD_EXCLUDED_ARTIFACTIDS =
      "select art_id from osee_artifact art, osee_txs txs where art.gamma_id = txs.gamma_id and txs.branch_id = ? and txs.tx_current = 1 and not exists (select null from osee_tuple2 t2, osee_txs txsP where tuple_type = 2 and e1 = ? and t2.gamma_id = txsP.gamma_id and txsP.branch_id = ? and txsP.tx_current = 1 and e2 = txs.app_id)";

   public static Set<ArtifactId> getNonApplicableArtifacts(BranchId branchId, ArtifactId viewId) {

      if (viewId.isInvalid()) {
         return Set.of();
      }

      Object[] objs = {branchId, viewId, branchId};

      //@formatter:off
      final var result =
         ArtifactLoader
            .selectArtifactIds(LOAD_EXCLUDED_ARTIFACTIDS, objs, 300)
            .stream()
            .collect( Collectors.toSet() );
      //@formatter:on

      return result;
   }

   //For Word Documents
   public static String checkForTrackedChanges(String value, Artifact art) {
      String returnValue = value;

      BranchId branch = art.getBranch();

      if (WordCoreUtil.containsWordAnnotations(value) && !BranchManager.getType(branch).isMergeBranch()) {
         try {
            String message =
               "This document contains track changes and cannot be saved with them. Do you want OSEE to remove them?" + "\n\nNote:You will need to reopen this artifact in OSEE to see the final result.";
            IStatusHandler handler = DebugPlugin.getDefault().getStatusHandler(promptStatus);
            @SuppressWarnings("unchecked")
            Pair<MutableBoolean, Integer> answer =
               (Pair<MutableBoolean, Integer>) handler.handleStatus(promptStatus, message);
            MutableBoolean first = answer.getFirst();
            boolean isOkToRemove = first.getValue();
            if (isOkToRemove) {
               returnValue = WordCoreUtil.removeAnnotations(value).toString();
            } else {
               throw new OseeCoreException(
                  "Artifact [%s], Branch[%s] contains track changes. Please remove them and save again.", art, branch);
            }
         } catch (CoreException ex) {
            OseeCoreException.wrapAndThrow(ex);
         } catch (ClassCastException ex) {
            OseeCoreException.wrapAndThrow(ex);
         }
      }
      return returnValue;
   }

   public static String elementNameFor(String artifactName) {
      // Since artifact names are free text it is important to reformat the name
      // to ensure it is suitable as an element name
      // NOTE: The current program.launch has a tokenizing bug that causes an error if consecutive
      // spaces are in the name
      String elementName = artifactName.trim().replaceAll("[^A-Za-z0-9]", "_");

      // Ensure the name did not end up empty
      if (elementName.equals("")) {
         elementName = "nameless";
      }

      // Fix the first character if it is a number by replacing it with its name
      char firstChar = elementName.charAt(0);
      if (firstChar >= '0' && firstChar <= '9') {
         elementName = NUMBER[firstChar - '0'] + elementName.substring(1);
      }

      return elementName;
   }

   //For Whole Word Documents
   public static String getEmptyDocumentContent() {
      return emptyDocumentContent;
   }

   public final static String getGUIDFromFile(File file) throws IOException {
      String guid = null;
      byte[] myBytes = new byte[4096];

      InputStream stream = null;
      try {
         stream = new BufferedInputStream(new FileInputStream(file));
         if (stream.read(myBytes) == -1) {
            throw new IOException("Buffer underrun");
         }
      } finally {
         Lib.close(stream);
      }

      String leadingPartOfFile = new String(myBytes, "UTF-8");
      String[] splitsBeforeAndAfter =
         leadingPartOfFile.split(Artifact.BEFORE_GUID_STRING + "|" + Artifact.AFTER_GUID_STRING);
      if (splitsBeforeAndAfter.length == 3) {
         guid = splitsBeforeAndAfter[1];
      }
      return guid;
   }

   /**
    * Gets the page orientation from the <code>Artifact</code>'s {@link CoreAttributeTypes#pageOrientation} attribute.
    * The {@link WordCoreUtil.pageType#getDefault()} will be used if unable to read the artifact's attribute or if the
    * artifact is {@link Artifact#SENTINEL}.
    *
    * @param artifact the artifact to extract the page orientation from.
    * @return the page orientation.
    */

   public static WordCoreUtil.pageType getPageOrientation(Artifact artifact) {

      var defaultPageType = WordCoreUtil.pageType.getDefault();

      if (artifact.isInvalid()) {
         return defaultPageType;
      }

      if (!artifact.isAttributeTypeValid(CoreAttributeTypes.PageOrientation)) {
         return defaultPageType;
      }

      try {
         var pageTypeString =
            artifact.getSoleAttributeValue(CoreAttributeTypes.PageOrientation, defaultPageType.name());

         return WordCoreUtil.pageType.fromString(pageTypeString);

      } catch (Exception e) {
         return defaultPageType;
      }
   }

   public static boolean isHeadingStyle(String paragraphStyle) {
      if (paragraphStyle == null) {
         return false;
      } else {
         String style = paragraphStyle.toLowerCase();
         // TODO get this list of styles from the Extension Point
         return style.startsWith("heading") || style.startsWith("toc") || style.startsWith("outline");
      }
   }

   /**
    * @return Returns the content with the bin data ID being reassigned. Note: The bin data Id needs to be reassigned to
    * allow multi edits of artifacts with images. Else if 2 images have the same ID the first image will be printed
    * duplicate times.
    */
   public static String reassignBinDataID(String content) {
      ChangeSet changeSet = new ChangeSet(content);
      Map<String, String> guidMap = new HashMap<>();

      binIdMatcher.reset(content);
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
    * @return - Returns the content with the ending bookmark IDs being reassigned to a unique number. This is done to
    * ensure all versions of MS Word will function correctly.
    */

   public static String reassignBookMarkID(String content) {
      return updateBookmarkIds.fixTags(content);
   }

   public static String referencesOnly(String content) {
      List<String> references = new ArrayList<>();

      Matcher referenceMatcher = referencePattern.matcher(content);
      while (referenceMatcher.find()) {
         String reference = referenceMatcher.group(1);
         references.add(reference);
      }

      //Collections.sort(references);
      StringBuilder sb = new StringBuilder();
      for (String reference : references) {
         sb.append(reference);
         sb.append("\n");
      }

      return sb.toString();
   }

   public final static CharSequence removeGUIDFromTemplate(CharSequence template) {

      String[] splitsBeforeAndAfter =
         template.toString().split(Artifact.BEFORE_GUID_STRING + "|" + Artifact.AFTER_GUID_STRING);

      if (splitsBeforeAndAfter.length == 3) {
         return splitsBeforeAndAfter[0] + " " + splitsBeforeAndAfter[2];
      } else {
         return template;
      }
   }

   public final static String removeWordMarkupSmartTags(String wordMarkup) {
      if (wordMarkup != null) {
         String[] splitsOnSmartTagStart = wordMarkup.split("<[/]{0,1}st\\d{1,22}");// example smart
         // (cough, cough)
         // tags
         // <st1:place>|</st1:place>
         if (splitsOnSmartTagStart.length > 1) {
            StringBuilder myStringBuilder = new StringBuilder(splitsOnSmartTagStart[0]);
            for (int i = 1; i < splitsOnSmartTagStart.length; i++) {
               int smartTagEndingIndex = splitsOnSmartTagStart[i].indexOf(">");
               myStringBuilder.append(splitsOnSmartTagStart[i].substring(smartTagEndingIndex + 1));
            }
            wordMarkup = myStringBuilder.toString();
         }
      }

      return wordMarkup;
   }

   /**
    * Analyzes all successive versions of 'Word Formatted Content' for useful differences and removes versions that do
    * not provide and difference from the prior version.
    *
    * @throws IllegalArgumentException if branch is null
    * @return returns true if some addressing was removed, otherwise false
    */
   public static boolean revertNonusefulWordChanges(ArtifactToken artifact, String table) {
      JdbcStatement chStmt = ConnectionHandler.getStatement();
      try {
         chStmt.runPreparedQuery(SELECT_WORD_VALUES, artifact, CoreAttributeTypes.WordTemplateContent,
            artifact.getBranch());

         List<Pair<String, GammaId>> values = new LinkedList<>();
         while (chStmt.next()) {
            String content;
            try {
               InputStream stream = chStmt.getBinaryStream("content");
               if (stream == null) {
                  content = "";
               } else {
                  content = new String(Streams.getByteArray(stream), "UTF-8");
               }
               values.add(new Pair<>(content, GammaId.valueOf(chStmt.getLong("gamma_id"))));
            } catch (UnsupportedEncodingException ex) {
               // should never ever ever occur
               throw new IllegalStateException("Must support UTF-8 format");
            }
         }

         Iterator<Pair<String, GammaId>> iter = values.iterator();
         if (iter.hasNext()) {
            Pair<String, GammaId> newest;
            Pair<String, GammaId> nextNewest = iter.next();
            Collection<GammaId> repeatGammas = new LinkedList<>();
            while (iter.hasNext()) {
               newest = nextNewest;
               nextNewest = iter.next();

               if (WordCoreUtil.textOnly(newest.getFirst()).equals(nextNewest.getFirst())) {
                  repeatGammas.add(newest.getSecond());
               }
            }

            if (repeatGammas.isEmpty()) {
               return false;
            }

            // For now expect that later we will remove all unaddressed attributes, this is safer
            // since addressing can be 'easily' reestablished, but not the actual data
            // ConnectionHandler.runUpdate(DELETE_ATTRIBUTE_PREFIX +
            // Collections.toString(repeatGammas, "(", ",", ")"));

            // Uncomment this to go live ... for now it will just give back true/false status
            // ConnectionHandler.runUpdate(DELETE_ADDRESSING_PREFIX +
            // Collections.toString(repeatGammas, "(", ",", ")"));

            for (GammaId gamma : repeatGammas) {
               ConnectionHandler.runPreparedUpdate("INSERT INTO " + table + " (gamma_id) values (?)", gamma);
            }

            return true;
         } else {
            return false;
         }
      } finally {
         chStmt.close();
      }
   }

   public WordCoreUtilClient() {
      super();
   }
}