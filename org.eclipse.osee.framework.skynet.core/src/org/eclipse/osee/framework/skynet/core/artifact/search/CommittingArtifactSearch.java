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

import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.ARTIFACT_VERSION_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.TRANSACTIONS_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.TRANSACTION_DETAIL_TABLE;
import java.util.List;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;

/**
 * Searches for artifacts used as the Control Management artifact of any commit involving a supplied artifact.
 * 
 * @author Robert A. Fisher
 */
public class CommittingArtifactSearch implements ISearchPrimitive {
   private Integer artId;
   private static final String tables =
         TRANSACTIONS_TABLE + "," + TRANSACTION_DETAIL_TABLE + "," + ARTIFACT_VERSION_TABLE;

   public CommittingArtifactSearch(Integer artId) {
      if (artId == null) throw new IllegalArgumentException("artId can not be null");

      this.artId = artId;
   }

   public String getArtIdColName() {
      return "commit_art_id";
   }

   public String getCriteriaSql(List<Object> dataList, Branch branch) {
      String whereConditions =
            TRANSACTION_DETAIL_TABLE.join(TRANSACTIONS_TABLE, "transaction_id") + " AND " + TRANSACTIONS_TABLE.join(
                  ARTIFACT_VERSION_TABLE, "gamma_id") + " AND " + ARTIFACT_VERSION_TABLE.column("art_id") + "=?";

      dataList.add(SQL3DataType.INTEGER);
      dataList.add(artId);

      return whereConditions;
   }

   public String getTableSql(List<Object> dataList, Branch branch) {
      return tables;
   }

   @Override
   public String toString() {
      return "Committed: " + artId;
   }

   public String getStorageString() {
      return artId.toString();
   }

   public static CommittingArtifactSearch getPrimitive(String storageString) throws NumberFormatException {
      return new CommittingArtifactSearch(Integer.parseInt(storageString));
   }
}
