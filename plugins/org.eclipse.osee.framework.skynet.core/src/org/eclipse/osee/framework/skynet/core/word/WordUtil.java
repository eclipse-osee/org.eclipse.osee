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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchViewData;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.text.change.ChangeSet;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.io.Streams;
import org.eclipse.osee.framework.jdk.core.util.xml.Xml;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;
import org.eclipse.osee.jdbc.JdbcStatement;

/**
 * Provides utility methods for parsing wordML.
 *
 * @author Jeff C. Phillips
 * @author Paul K. Waldfogel
 */
public class WordUtil {

   public static final String BODY_START = "<w:body>";
   public static final String BODY_END = "</w:body>";
   private static final String[] NUMBER =
      new String[] {"Zero", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine"};

   private static final String SELECT_WORD_VALUES =
      "SELECT attr.content, attr.gamma_id FROM osee_attribute attr, osee_txs txs WHERE attr.art_id=? AND attr.attr_type_id=? AND attr.gamma_id = txs.gamma_id AND txs.branch_id=? ORDER BY attr.gamma_id DESC";
   private static final Matcher binIdMatcher = Pattern.compile("wordml://(.+?)[.]").matcher("");
   private static final Pattern tagKiller = Pattern.compile("<.*?>", Pattern.DOTALL | Pattern.MULTILINE);
   private static final Pattern paragraphPattern = Pattern.compile("<w:p( .*?)?>");
   private static final Pattern referencePattern = Pattern.compile("(_Ref[0-9]{9}|Word\\.Bookmark\\.End)");
   private static int bookMarkId = 1000;
   private static UpdateBookmarkIds updateBookmarkIds = new UpdateBookmarkIds(bookMarkId);

   public WordUtil() {
      super();
   }

   /**
    * @return - Returns the content with the ending bookmark IDs being reassigned to a unique number. This is done to
    * ensure all versions of MS Word will function correctly.
    */

   public static String reassignBookMarkID(String content) throws OseeCoreException {
      return updateBookmarkIds.fixTags(content);
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
    * Analyzes all successive versions of 'Word Formatted Content' for useful differences and removes versions that do
    * not provide and difference from the prior version.
    *
    * @throws IllegalArgumentException if branch is null
    * @return returns true if some addressing was removed, otherwise false
    */
   public static boolean revertNonusefulWordChanges(int artId, BranchId branch, String table) throws OseeCoreException {
      if (branch == null) {
         throw new IllegalArgumentException("branch can not be null");
      }

      JdbcStatement chStmt = ConnectionHandler.getStatement();
      try {
         chStmt.runPreparedQuery(SELECT_WORD_VALUES, artId, CoreAttributeTypes.WordTemplateContent, branch);

         List<Pair<String, Integer>> values = new LinkedList<>();
         while (chStmt.next()) {
            String content;
            try {
               InputStream stream = chStmt.getBinaryStream("content");
               if (stream == null) {
                  content = "";
               } else {
                  content = new String(Streams.getByteArray(stream), "UTF-8");
               }
               values.add(new Pair<String, Integer>(content, chStmt.getInt("gamma_id")));
            } catch (UnsupportedEncodingException ex) {
               // should never ever ever occur
               throw new IllegalStateException("Must support UTF-8 format");
            }
         }

         Iterator<Pair<String, Integer>> iter = values.iterator();
         if (iter.hasNext()) {
            Pair<String, Integer> newest;
            Pair<String, Integer> nextNewest = iter.next();
            Collection<Integer> repeatGammas = new LinkedList<>();
            while (iter.hasNext()) {
               newest = nextNewest;
               nextNewest = iter.next();

               if (WordUtil.textOnly(newest.getFirst()).equals(nextNewest.getFirst())) {
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

            for (Integer gamma : repeatGammas) {
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

   public static String textOnly(String str) {
      str = paragraphPattern.matcher(str).replaceAll(" ");
      str = tagKiller.matcher(str).replaceAll("").trim();
      return Xml.unescape(str).toString();
   }

   public static String referencesOnly(String content) {
      List<String> references = new ArrayList<>();

      Matcher referenceMatcher = referencePattern.matcher(content);
      while (referenceMatcher.find()) {
         referenceMatcher.toString();
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

   public static boolean isHeadingStyle(String paragraphStyle) {
      if (paragraphStyle == null) {
         return false;
      } else {
         String style = paragraphStyle.toLowerCase();
         // TODO get this list of styles from the Extension Point
         return style.startsWith("heading") || style.startsWith("toc") || style.startsWith("outline");
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

   public final static String removeGUIDFromTemplate(String template) {
      String newTemplate = "";

      String[] splitsBeforeAndAfter = template.split(Artifact.BEFORE_GUID_STRING + "|" + Artifact.AFTER_GUID_STRING);

      if (splitsBeforeAndAfter.length == 3) {
         newTemplate = splitsBeforeAndAfter[0] + " " + splitsBeforeAndAfter[2];
      } else {
         newTemplate = template;
      }
      return newTemplate;
   }

   public static HashSet<String> getValidConfigurations(BranchId branch) {
      HashSet<String> validConfigurations = new HashSet<>();

      List<BranchViewData> branchViews = ServiceUtil.getOseeClient().getApplicabilityEndpoint(branch).getViews();
      for (BranchViewData branchView : branchViews) {
         List<Artifact> artifacts = ArtifactQuery.getArtifactListFrom(branchView.getBranchViews(), branch);

         for (Artifact artifact : artifacts) {
            validConfigurations.add(artifact.getName());
         }
      }

      return validConfigurations;
   }
}
