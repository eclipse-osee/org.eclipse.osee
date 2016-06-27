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
package org.eclipse.osee.orcs.db.internal.change;

import java.util.HashMap;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConstants;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.orcs.db.internal.sql.join.IdJoinQuery;

/**
 * @author Roberto E. Escobar
 */
public class ChangeItemLoader {

   private final JdbcClient jdbcClient;
   private final HashMap<Long, Pair<ModificationType, ApplicabilityId>> changeByGammaId;

   public static interface ChangeItemFactory {

      String getItemTableName();

      String getItemIdColumnName();

      String getItemValueColumnName();

      String getLoadByGammaQuery();

      ChangeItem createItem(JdbcStatement statement) throws OseeCoreException;
   }

   public ChangeItemLoader(JdbcClient jdbcClient, HashMap<Long, Pair<ModificationType, ApplicabilityId>> changeByGammaId) {
      this.jdbcClient = jdbcClient;
      this.changeByGammaId = changeByGammaId;
   }

   public ChangeItemFactory createArtifactChangeItemFactory() {
      return new ArtifactChangeItemFactory(changeByGammaId);
   }

   public ChangeItemFactory createAttributeChangeItemFactory() {
      return new AttributeChangeItemFactory(changeByGammaId);
   }

   public ChangeItemFactory createRelationChangeItemFactory() {
      return new RelationChangeItemFactory(changeByGammaId);
   }

   public void loadItemIdsBasedOnGammas(ChangeItemFactory factory, int queryId, HashMap<Integer, ChangeItem> changesByItemId, IdJoinQuery idJoin) throws OseeCoreException {
      JdbcStatement chStmt = jdbcClient.getStatement();
      try {
         chStmt.runPreparedQuery(JdbcConstants.JDBC__MAX_FETCH_SIZE, factory.getLoadByGammaQuery(), queryId);
         while (chStmt.next()) {
            ChangeItem item = factory.createItem(chStmt);
            Integer itemId = item.getItemId();
            changesByItemId.put(itemId, item);
            idJoin.add(itemId);
         }
      } finally {
         chStmt.close();
      }
   }
}
