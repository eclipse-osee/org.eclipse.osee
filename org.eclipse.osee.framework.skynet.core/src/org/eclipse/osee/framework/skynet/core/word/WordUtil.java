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

import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.ATTRIBUTE_VERSION_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.TRANSACTIONS_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.TRANSACTION_DETAIL_TABLE;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.jdk.core.text.change.ChangeSet;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.io.Streams;
import org.eclipse.osee.framework.jdk.core.util.xml.Xml;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;

/**
 * Provides utility methods for parsing wordML.
 * 
 * @author Jeff C. Phillips
 * @author Paul K. Waldfogel
 */
public class WordUtil {
   private static final String SELECT_WORD_VALUES =
         "SELECT " + ATTRIBUTE_VERSION_TABLE.columns("content", "gamma_id") + " FROM " + ATTRIBUTE_VERSION_TABLE + "," + TRANSACTIONS_TABLE + "," + TRANSACTION_DETAIL_TABLE + " WHERE art_id=? AND attr_type_id=? AND " + ATTRIBUTE_VERSION_TABLE.join(
               TRANSACTIONS_TABLE, "gamma_id") + " AND " + TRANSACTIONS_TABLE.join(TRANSACTION_DETAIL_TABLE,
               "transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=? ORDER BY gamma_id DESC";
   private static final Matcher binIdMatcher = Pattern.compile("wordml://(.+?)[.]").matcher("");
   private static final Pattern tagKiller = Pattern.compile("<.*?>", Pattern.DOTALL | Pattern.MULTILINE);
   private static final Pattern paragraphPattern = Pattern.compile("<w:p( .*?)?>");

   public WordUtil() {
      super();
   }

   /**
    * @return Returns the content with the bin data ID being reassigned. Note: The bin data Id needs to be reassigned to
    *         allow multi edits of artifacts with images. Else if 2 images have the same ID the first image will be
    *         printed duplicate times.
    */
   public static String reassignBinDataID(String content) {
      ChangeSet changeSet = new ChangeSet(content);
      Map<String, String> guidMap = new HashMap<String, String>();

      binIdMatcher.reset(content);
      boolean atLeastOneMatch = false;
      while (binIdMatcher.find()) {
         atLeastOneMatch = true;
         String oldName = binIdMatcher.group(1);

         String guid = guidMap.get(oldName);
         if (guid == null) {
            guid = GUID.generateGuidStr();
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
    * @throws OseeTypeDoesNotExist
    * @throws OseeDataStoreException
    * @throws Exception
    */
   public static boolean revertNonusefulWordChanges(int artId, Branch branch, String table) throws OseeDataStoreException, OseeTypeDoesNotExist {
      if (branch == null) throw new IllegalArgumentException("branch can not be null");

      AttributeType attributeDescriptor = AttributeTypeManager.getType(WordAttribute.WORD_TEMPLATE_CONTENT);

      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         chStmt.runPreparedQuery(SELECT_WORD_VALUES, artId, attributeDescriptor.getAttrTypeId(), branch.getBranchId());

         List<Pair<String, Integer>> values = new LinkedList<Pair<String, Integer>>();
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
            Collection<Integer> repeatGammas = new LinkedList<Integer>();
            while (iter.hasNext()) {
               newest = nextNewest;
               nextNewest = iter.next();

               if (WordUtil.textOnly(newest.getKey()).equals(nextNewest.getKey())) {
                  repeatGammas.add(newest.getValue());
               }
            }

            if (repeatGammas.isEmpty()) return false;

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

   public static String textOnly(String str) {
      str = paragraphPattern.matcher(str).replaceAll(" ");
      str = tagKiller.matcher(str).replaceAll("").trim();
      return Xml.unescape(str).toString();
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
         String[] splitsOnSmartTagStart = wordMarkup.split("<[/]{0,1}st\\d{1,22}");// example smart (cough, cough) tags <st1:place>|</st1:place>
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

   public final static String addGUIDToDocument(String myGuid, String wholeDocumentWordMarkup) {
      String adjustedWordContentString = null;
      if (wholeDocumentWordMarkup.indexOf(Artifact.BEFORE_GUID_STRING) > 0) {
         adjustedWordContentString =
               wholeDocumentWordMarkup.replaceAll(Artifact.BEFORE_GUID_STRING + "(.*)" + Artifact.AFTER_GUID_STRING,
                     Artifact.BEFORE_GUID_STRING + myGuid + Artifact.AFTER_GUID_STRING);
      } else {
         adjustedWordContentString =
               wholeDocumentWordMarkup.replaceAll(
                     "w:wordDocument ",
                     "w:wordDocument " + "xmlns:ForGUID='http:/" + Artifact.BEFORE_GUID_STRING + myGuid + Artifact.AFTER_GUID_STRING + "' ");
      }
      return adjustedWordContentString;
   }

   public final static String getGUIDFromFileInputStream(FileInputStream myFileInputStream) throws IOException {
      String guid = null;
      byte[] myBytes = new byte[4096];
      myFileInputStream.read(myBytes);
      String leadingPartOfFile = new String(myBytes);
      myFileInputStream = null;
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

   private static final Matcher spellCheck =
         Pattern.compile("<w:proofErr w:type=\"spell(End|Start)\"/>", Pattern.DOTALL | Pattern.MULTILINE).matcher("");

   public final static String stripSpellCheck(String content) {
      spellCheck.reset(content);
      return spellCheck.replaceAll("");
   }
}
