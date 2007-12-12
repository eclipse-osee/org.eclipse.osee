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

import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ARTIFACT_TABLE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ARTIFACT_TYPE_TABLE;
import java.util.List;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.plugin.sql.SQL3DataType;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase;

/**
 * @author Jeff C. Phillips
 */
public class OrphanArtifactSearch implements ISearchPrimitive {

   private static final String tables = ARTIFACT_TABLE + ", " + ARTIFACT_TYPE_TABLE;
   private final static String TOKEN = ";";
   private static final String sql =
         "" + SkynetDatabase.ARTIFACT_TYPE_TABLE + ".art_type_id = " + SkynetDatabase.ARTIFACT_TABLE + ".art_type_id AND " + SkynetDatabase.ARTIFACT_TYPE_TABLE + ".NAME = ? AND art_id NOT in (SELECT t2.art_id FROM " + SkynetDatabase.RELATION_LINK_VERSION_TABLE + " t1, " + SkynetDatabase.ARTIFACT_TABLE + " t2, " + SkynetDatabase.RELATION_LINK_TYPE_TABLE + " t3, " + SkynetDatabase.TRANSACTIONS_TABLE + " t4, " + SkynetDatabase.TRANSACTION_DETAIL_TABLE + " t5, (SELECT Max(t1.gamma_id) AS gamma_id, t1.rel_link_id, t3.branch_id FROM " + SkynetDatabase.RELATION_LINK_VERSION_TABLE + " t1, " + SkynetDatabase.TRANSACTIONS_TABLE + " t2, " + SkynetDatabase.TRANSACTION_DETAIL_TABLE + " t3 WHERE t1.gamma_id = t2.gamma_id AND t2.transaction_id = t3.transaction_id AND t3.branch_id = ? GROUP BY t1.rel_link_id, t3.branch_id) t6 WHERE t3.type_name = 'Default Hierarchical' AND t3.rel_link_type_id = t1.rel_link_type_id AND t1.b_art_id = t2.art_id AND t1.gamma_id = t4.gamma_id AND t4.transaction_id = t5.transaction_id AND t1.rel_link_id = t6.rel_link_id AND t5.branch_id = t6.branch_id AND t1.gamma_id = t6.gamma_id AND t1.modification_id <> 3 GROUP BY t2.art_id)";
   private String descriptorName;

   public OrphanArtifactSearch(String descriptor) {
      this.descriptorName = descriptor;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive#getSql(java.util.List, org.eclipse.osee.framework.skynet.core.artifact.Branch)
    */
   public String getCriteriaSql(List<Object> dataList, Branch branch) {
      dataList.add(SQL3DataType.VARCHAR);
      dataList.add(descriptorName);
      dataList.add(SQL3DataType.INTEGER);
      dataList.add(branch.getBranchId());
      return sql;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive#getArtIdColName()
    */
   public String getArtIdColName() {
      return "art_id";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive#getTables(java.util.List)
    */
   public String getTableSql(List<Object> dataList, Branch branch) {
      return tables;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive#getStorageString()
    */
   public String getStorageString() {
      return "Orphan Search: " + descriptorName + TOKEN;
   }

   @Override
   public String toString() {
      return "Orphan Search: " + descriptorName;
   }

}
