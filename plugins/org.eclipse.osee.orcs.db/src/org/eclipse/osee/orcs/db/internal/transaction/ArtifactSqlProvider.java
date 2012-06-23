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

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.db.internal.loader.IdFactory;
import org.eclipse.osee.orcs.db.internal.sql.OseeSql;

public class ArtifactSqlProvider extends AbstractSqlProvider implements SqlProvider<ArtifactData> {

   private static final String INSERT_ARTIFACT =
      "INSERT INTO osee_artifact (gamma_id, art_id, art_type_id, guid, human_readable_id) VALUES (?,?,?,?,?)";

   public ArtifactSqlProvider(IdFactory idFactory) {
      super(idFactory);
   }

   @Override
   public void getInsertData(InsertDataCollector collector, ArtifactData data) throws OseeCoreException {
      collector.addInsertToBatch(1, INSERT_ARTIFACT, getGammaId(data), data.getLocalId(), data.getTypeUuid(),
         data.getGuid(), data.getHumanReadableId());
      collector.addTxNotCurrentToBatch(OseeSql.TX_GET_PREVIOUS_TX_NOT_CURRENT_ARTIFACTS, data.getLocalId(),
         data.getModType());
   }
}
