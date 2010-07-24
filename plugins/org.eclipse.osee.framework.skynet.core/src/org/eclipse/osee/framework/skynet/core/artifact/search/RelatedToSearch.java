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

import java.util.List;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.model.Branch;

/**
 * @author Robert A. Fisher
 */
public class RelatedToSearch implements ISearchPrimitive {
   private static final String TABLES = "osee_relation_link rel_1,osee_txs txs";
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

   @Override
   public String getArtIdColName() {
      return (sideA ? "a" : "b") + "_art_id";
   }

   @Override
   public String getCriteriaSql(List<Object> dataList, Branch branch) {
      String sideName = "rel_1." + getArtIdColName();
      String sql =
         sideName + " = ? AND rel_1.gamma_id = txs.gamma_id AND txs.transaction_id = (SELECT max(txs.transaction_id) FROM osee_relation_link rel_2, osee_txs txs WHERE rel_2.rel_link_id = rel_1.rel_link_id AND rel_2.gamma_id = txs.gamma_id AND txs.branch_id = ?) AND txs.mod_type <>?";

      dataList.add(artId);
      dataList.add(branch.getId());
      dataList.add(ModificationType.DELETED.getValue());

      return sql;
   }

   @Override
   public String getTableSql(List<Object> dataList, Branch branch) {
      return TABLES;
   }

   @Override
   public String toString() {
      return "Related to art_id: " + artId + " on side: " + (sideA ? "A" : "B");
   }

   public static RelatedToSearch getPrimitive(String storageString) {
      String[] values = storageString.split(TOKEN);
      if (values.length != 2) {
         throw new IllegalStateException("Value for " + RelatedToSearch.class.getSimpleName() + " not parsable");
      }

      return new RelatedToSearch(Integer.parseInt(values[0]), Boolean.parseBoolean(values[1]));
   }

   @Override
   public String getStorageString() {
      return Integer.toString(artId) + TOKEN + Boolean.toString(sideA);
   }
}
