/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.transaction;

import java.util.Collection;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.db.internal.sql.OseeSql;

public class RelationInsertProvider extends BaseInsertProvider implements InsertDataProvider<RelationData> {

   private static final String INSERT_INTO_RELATION_TABLE =
      "INSERT INTO osee_relation_link (rel_link_id, rel_link_type_id, a_art_id, b_art_id, rationale, gamma_id) VALUES (?,?,?,?,?,?)";

   @Override
   public void getInsertData(InsertDataCollector collector, Collection<RelationData> datas) throws OseeCoreException {
      for (RelationData data : datas) {
         collector.addInsertToBatch(4, INSERT_INTO_RELATION_TABLE, data.getLocalId(), data.getTypeUuid(),
            data.getArtIdA(), data.getArtIdB(), data.getRationale(), getGammaId(data));
         collector.addTxNotCurrentToBatch(OseeSql.TX_GET_PREVIOUS_TX_NOT_CURRENT_RELATIONS, data.getLocalId(),
            data.getModType());
      }
   }
}
