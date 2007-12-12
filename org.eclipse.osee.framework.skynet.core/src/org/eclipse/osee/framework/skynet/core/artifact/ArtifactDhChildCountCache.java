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
package org.eclipse.osee.framework.skynet.core.artifact;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.osee.framework.jdk.core.type.DoubleKeyHashMap;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.ui.plugin.sql.SQL3DataType;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandler;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandlerStatement;
import org.eclipse.osee.framework.ui.plugin.util.db.DbUtil;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ModificationType;

/**
 * @author Robert A. Fisher
 */
public class ArtifactDhChildCountCache {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(ArtifactDhChildCountCache.class);
   // This has one prepared value, the branch_id
   private static final String sql =
         "SELECT COUNT(t1.rel_link_id) AS children, t2.art_id FROM " + SkynetDatabase.RELATION_LINK_VERSION_TABLE + " t1, " + SkynetDatabase.ARTIFACT_TABLE + " t2, " + SkynetDatabase.RELATION_LINK_TYPE_TABLE + " t3, " + SkynetDatabase.TRANSACTIONS_TABLE + " t4 WHERE t3.TYPE_NAME = ? AND t3.rel_link_type_id = t1.rel_link_type_id AND t1.a_art_id = t2.art_id AND t1.gamma_id = t4.gamma_id AND t4.transaction_id = (SELECT MAX(t8.transaction_id) FROM " + SkynetDatabase.RELATION_LINK_VERSION_TABLE + " t6, " + SkynetDatabase.TRANSACTIONS_TABLE + " t7, " + SkynetDatabase.TRANSACTION_DETAIL_TABLE + " t8 WHERE t1.rel_link_id = t6.rel_link_id AND t6.gamma_id = t7.gamma_id AND t7.transaction_id = t8.transaction_id AND t8.branch_id = ?) AND t1.modification_id <> ? GROUP BY t2.art_id";
   private static final Object MARKER = new Object();

   private final DoubleKeyHashMap<Branch, Integer, Integer> childCount;
   private final Map<Branch, Object> populated;

   /**
    * 
    */
   public ArtifactDhChildCountCache() {
      this.childCount = new DoubleKeyHashMap<Branch, Integer, Integer>();
      this.populated = new HashMap<Branch, Object>();
   }

   public int getChildCount(int artId, Branch branch) {
      ensurePopulated(branch);

      if (childCount.containsKey(branch, artId))
         return childCount.get(branch, artId);
      else
         return 0;
   }

   private synchronized void ensurePopulated(Branch branch) {
      if (!populated.containsKey(branch)) {
         ConnectionHandlerStatement chStmt = null;
         try {
            chStmt =
                  ConnectionHandler.runPreparedQuery(sql, SQL3DataType.VARCHAR, "Default Hierarchical",
                        SQL3DataType.INTEGER, branch.getBranchId(), SQL3DataType.INTEGER,
                        ModificationType.DELETE.getValue());
            ResultSet rset = chStmt.getRset();

            while (rset.next()) {
               childCount.put(branch, rset.getInt("art_id"), rset.getInt("children"));
            }

            populated.put(branch, MARKER);
         } catch (SQLException e) {
            logger.log(Level.SEVERE, e.toString(), e);
         } finally {
            DbUtil.close(chStmt);
         }
      }
   }
}
