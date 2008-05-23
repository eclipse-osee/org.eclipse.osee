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
package org.eclipse.osee.framework.skynet.core.artifact.search;

import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.TAG_ART_MAP_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.TAG_TABLE;
import java.sql.SQLException;
import java.util.List;
import org.eclipse.osee.framework.db.connection.core.schema.LocalAliasTable;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;

/**
 * @author Jeff C. Phillips
 */
public class TagSearch implements ISearchPrimitive {
   private final static String TOKEN = ";";
   private static final LocalAliasTable TAG_ART_MAP_ALIAS = TAG_ART_MAP_TABLE.aliasAs("map");
   private static final LocalAliasTable TAG_ALIAS = TAG_TABLE.aliasAs("tag");
   private static final String SQL = TAG_ART_MAP_ALIAS.column("tag_id") + " = " + TAG_ALIAS.column("tag_id") + " AND ";
   private static final String TAG = "tag";
   private static final String LOWER_CASE_TAG = "lowercase_tag";
   private static final String TABLES = TAG_ART_MAP_ALIAS + ", " + TAG_ALIAS;

   private String tag;
   private boolean caseSensitive;
   private boolean partialMatch;

   public TagSearch(String tag, boolean caseSensitive, boolean partialMatch) {
      super();
      this.tag = tag;
      this.caseSensitive = caseSensitive;
      this.partialMatch = partialMatch;
   }

   public String getCriteriaSql(List<Object> dataList, Branch branch) throws SQLException {
      StringBuffer sql = new StringBuffer(SQL);

      sql.append(TAG_ALIAS.column(getTagColumn()) + getTagOperator() + "?");
      dataList.add(SQL3DataType.VARCHAR);
      dataList.add(getTagParameter());

      sql.append(" AND " + TAG_ART_MAP_ALIAS.column("branch_id") + " IN (");
      Branch branchCursor = branch;
      do {
         if (branchCursor != branch) sql.append(",");
         sql.append("?");
         dataList.add(SQL3DataType.INTEGER);
         dataList.add(branchCursor.getBranchId());

         branchCursor = branchCursor.getParentBranch();
      } while (branchCursor != null);
      sql.append(")");

      return sql.toString();
   }

   private String getTagColumn() {
      if (caseSensitive)
         return TAG;
      else
         return LOWER_CASE_TAG;
   }

   private String getTagOperator() {
      if (partialMatch)
         return " LIKE ";
      else
         return " = ";
   }

   private String getTagParameter() {
      String tagParamater = tag;

      if (!caseSensitive) tagParamater = tagParamater.toLowerCase();
      if (partialMatch) tagParamater = "%" + tagParamater + "%";

      return tagParamater;
   }

   public String getArtIdColName() {
      return TAG_ART_MAP_ALIAS.column("art_id");
   }

   public String getTableSql(List<Object> dataList, Branch branch) {
      return TABLES;
   }

   @Override
   public String toString() {
      return "Tag: " + tag;
   }

   public static TagSearch getPrimitive(String storageString) {
      String[] results = storageString.split(TOKEN);
      if (results.length != 3) throw new IllegalStateException(
            "Value for " + TagSearch.class.getSimpleName() + " not parsable");

      String tag = results[0];
      boolean isCaseSensitive = Boolean.parseBoolean(results[1]);
      boolean isPartial = Boolean.parseBoolean(results[2]);
      TagSearch search = new TagSearch(tag, isCaseSensitive, isPartial);
      return search;
   }

   public String getStorageString() {
      return tag + TOKEN + caseSensitive + TOKEN + partialMatch;
   }
}
