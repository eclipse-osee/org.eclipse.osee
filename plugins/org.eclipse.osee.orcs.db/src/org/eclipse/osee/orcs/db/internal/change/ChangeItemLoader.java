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

import static org.eclipse.osee.framework.database.core.IOseeStatement.MAX_FETCH;
import java.util.HashMap;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.IdJoinQuery;

/**
 * @author Roberto E. Escobar
 */
public class ChangeItemLoader {

   private final IOseeDatabaseService dbService;
   private final HashMap<Long, ModificationType> changeByGammaId;

   public static interface ChangeItemFactory {

      String getItemTableName();

      String getItemIdColumnName();

      String getItemValueColumnName();

      String getLoadByGammaQuery();

      ChangeItem createItem(IOseeStatement statement) throws OseeCoreException;
   }

   public ChangeItemLoader(IOseeDatabaseService dbService, HashMap<Long, ModificationType> changeByGammaId) {
      this.dbService = dbService;
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
      IOseeStatement chStmt = dbService.getStatement();
      try {
         chStmt.runPreparedQuery(MAX_FETCH, factory.getLoadByGammaQuery(), queryId);
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
