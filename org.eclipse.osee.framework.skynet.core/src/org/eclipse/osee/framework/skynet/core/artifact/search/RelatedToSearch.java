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

import static org.eclipse.osee.framework.skynet.core.artifact.search.SkynetDatabase.RELATION_LINK_VERSION_TABLE;
import static org.eclipse.osee.framework.skynet.core.artifact.search.SkynetDatabase.TRANSACTIONS_TABLE;
import static org.eclipse.osee.framework.skynet.core.artifact.search.SkynetDatabase.TRANSACTION_DETAIL_TABLE;
import java.util.List;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.model.Branch;

/**
 * @author Robert A. Fisher
 */
public class RelatedToSearch implements ISearchPrimitive {
   private static final LocalAliasTable LINK_ALIAS_1 = new LocalAliasTable(RELATION_LINK_VERSION_TABLE, "rel_1");
   private static final LocalAliasTable LINK_ALIAS_2 = new LocalAliasTable(RELATION_LINK_VERSION_TABLE, "rel_2");
   private static final String TABLES = LINK_ALIAS_1 + "," + TRANSACTIONS_TABLE;
   private static final String TOKEN = ";";
   private final int artId;
   private final boolean sideA;

   /**
    * @param artId The type of relation for the artifact to be in
    * @param sideA True if you want artifacts that are on the A side of the relation from the given artId
    */
   public RelatedToSearch(int artId, boolean sideA) {
      super();
      this.artId = artId;
      this.sideA = sideA;
   }

   public String getArtIdColName() {
      return (sideA ? "a" : "b") + "_art_id";
   }

   public String getCriteriaSql(List<Object> dataList, Branch branch) {
      String sql =
            LINK_ALIAS_1.column((sideA ? "b" : "a") + "_art_id") + "=?" + " AND " + LINK_ALIAS_1.column("gamma_id") + "=" + TRANSACTIONS_TABLE.column("gamma_id") + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + "=" + "(SELECT max(osee_tx_details.transaction_id) FROM " + LINK_ALIAS_2 + "," + TRANSACTIONS_TABLE + "," + TRANSACTION_DETAIL_TABLE + " WHERE " + LINK_ALIAS_2.column("rel_link_id") + "=" + LINK_ALIAS_1.column("rel_link_id") + " AND " + LINK_ALIAS_2.column("gamma_id") + "=" + TRANSACTIONS_TABLE.column("gamma_id") + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + "=" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=?)" + " AND " + TRANSACTIONS_TABLE.column("mod_type") + "<>?";

      dataList.add(artId);
      dataList.add(branch.getId());
      dataList.add(ModificationType.DELETED.getValue());

      return sql;
   }

   public String getTableSql(List<Object> dataList, Branch branch) {
      return TABLES;
   }

   public String toString() {
      return "Related to art_id: " + artId + " on side: " + (sideA ? "A" : "B");
   }

   public static RelatedToSearch getPrimitive(String storageString) {
      String[] values = storageString.split(TOKEN);
      if (values.length != 2) throw new IllegalStateException(
            "Value for " + RelatedToSearch.class.getSimpleName() + " not parsable");

      return new RelatedToSearch(Integer.parseInt(values[0]), Boolean.parseBoolean(values[1]));
   }

   public String getStorageString() {
      return Integer.toString(artId) + TOKEN + Boolean.toString(sideA);
   }
}
