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
package org.eclipse.osee.framework.skynet.core.tagging;

import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.TAG_ID_SEQ;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.TAG_TABLE;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.eclipse.osee.framework.ui.plugin.sql.SQL3DataType;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandler;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandlerStatement;
import org.eclipse.osee.framework.ui.plugin.util.db.DbUtil;
import org.eclipse.osee.framework.ui.plugin.util.db.Query;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.LocalAliasTable;

/**
 * Controls tags and their tagId's.
 * 
 * @author Robert A. Fisher
 */
class TagFactory {

   private static final LocalAliasTable TAG_ALIAS_1 = TAG_TABLE.aliasAs("tag1");
   private static final String SELECT_TAG =
         "SELECT " + TAG_ALIAS_1.column("tag_id") + " FROM " + TAG_ALIAS_1 + " WHERE " + TAG_ALIAS_1.column("tag") + "=? AND " + TAG_ALIAS_1.column("tag_type_id") + "=?";
   private static final String INSERT_TAG =
         "INSERT INTO " + TAG_TABLE + "(LOWERCASE_TAG, TAG, HIT_COUNT, TAG_TYPE_ID, TAG_ID) VALUES (?,?,?,?,?)";

   /**
    * 
    */
   public TagFactory() {
      super();
   }

   public int getTagId(String tag, TagDescriptor tagDescriptor) throws SQLException {
      ConnectionHandlerStatement chStmt = null;

      try {
         chStmt =
               ConnectionHandler.runPreparedQuery(1, SELECT_TAG, SQL3DataType.VARCHAR, tag, SQL3DataType.INTEGER,
                     tagDescriptor.getTagTypeId());

         ResultSet rset = chStmt.getRset();

         if (rset.next()) {
            return rset.getInt("tag_id");
         } else {
            int tagId = Query.getNextSeqVal(null, TAG_ID_SEQ);
            ConnectionHandler.runPreparedUpdate(true, INSERT_TAG, SQL3DataType.VARCHAR, tag.toLowerCase(),
                  SQL3DataType.VARCHAR, tag, SQL3DataType.INTEGER, 0, SQL3DataType.INTEGER,
                  tagDescriptor.getTagTypeId(), SQL3DataType.INTEGER, tagId);

            return tagId;
         }
      } finally {
         DbUtil.close(chStmt);
      }
   }
}
