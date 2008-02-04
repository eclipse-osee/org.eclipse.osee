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

import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ATTRIBUTE_VERSION_TABLE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.TRANSACTIONS_TABLE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.TRANSACTION_DETAIL_TABLE;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.text.change.ChangeSet;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.io.Streams;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.DynamicAttributeDescriptor;
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;
import org.eclipse.osee.framework.ui.plugin.sql.SQL3DataType;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandler;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandlerStatement;
import org.eclipse.osee.framework.ui.plugin.util.db.DbUtil;

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
   private static final Pattern binDataIdPattern = Pattern.compile("wordml://(.+?)[.]");
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

      Matcher binMatcher = binDataIdPattern.matcher(content);
      while (binMatcher.find()) {
         String oldName = binMatcher.group(1);

         String guid = guidMap.get(oldName);
         if (guid == null) {
            guid = GUID.generateGuidStr();
            guidMap.put(oldName, guid);
         }

         changeSet.replace(binMatcher.start(1), binMatcher.start(1), guid);
      }
      return changeSet.toString();
   }

   /**
    * Analyzes all successive versions of 'Word Formatted Content' for useful differences and removes versions that do
    * not provide and difference from the prior version.
    * 
    * @throws SQLException
    * @throws IllegalArgumentException if branch is null
    * @return returns true if some addressing was removed, otherwise false
    */
   public static boolean revertNonusefulWordChanges(int artId, Branch branch, String table) throws SQLException {
      if (branch == null) throw new IllegalArgumentException("branch can not be null");

      ConfigurationPersistenceManager manager = ConfigurationPersistenceManager.getInstance();
      DynamicAttributeDescriptor attributeDescriptor =
            manager.getDynamicAttributeType(WordAttribute.CONTENT_NAME, branch);

      ConnectionHandlerStatement chStmt = null;
      try {
         chStmt =
               ConnectionHandler.runPreparedQuery(SELECT_WORD_VALUES, SQL3DataType.INTEGER, artId,
                     SQL3DataType.INTEGER, attributeDescriptor.getAttrTypeId(), SQL3DataType.INTEGER,
                     branch.getBranchId());

         ResultSet rset = chStmt.getRset();
         List<Pair<String, Integer>> values = new LinkedList<Pair<String, Integer>>();
         while (rset.next()) {
            String content;
            try {
               InputStream stream = rset.getBinaryStream("content");
               if (stream == null) {
                  content = "";
               } else {
                  content = new String(Streams.getByteArray(stream), "UTF-8");
               }
               values.add(new Pair<String, Integer>(content, rset.getInt("gamma_id")));
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
               ConnectionHandler.runPreparedUpdate("INSERT INTO " + table + " (gamma_id) values (?)",
                     SQL3DataType.INTEGER, gamma);
            }

            return true;
         } else {
            return false;
         }
      } finally {
         DbUtil.close(chStmt);
      }
   }

   public static String textOnly(String string) {
      string = paragraphPattern.matcher(string).replaceAll(" ");
      return tagKiller.matcher(string).replaceAll("");
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
      String wordMarkup3 = wordMarkup;
      String[] splitsOnSmartTagStart = wordMarkup.split("<[/]{0,1}st\\d{1,22}");// <st1:place>|</st1:place>
      if (splitsOnSmartTagStart.length > 1) {
         StringBuilder myStringBuilder = new StringBuilder(splitsOnSmartTagStart[0]);
         for (int i = 1; i < splitsOnSmartTagStart.length; i++) {
            int smartTagEndingIndex = splitsOnSmartTagStart[i].indexOf(">");
            myStringBuilder.append(splitsOnSmartTagStart[i].substring(smartTagEndingIndex + 1));
         }
         wordMarkup3 = myStringBuilder.toString();
      }
      return wordMarkup3;
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
}
