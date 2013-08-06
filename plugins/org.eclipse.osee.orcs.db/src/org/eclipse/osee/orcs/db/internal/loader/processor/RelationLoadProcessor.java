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
package org.eclipse.osee.orcs.db.internal.loader.processor;

import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.core.ds.VersionData;
import org.eclipse.osee.orcs.db.internal.loader.data.RelationObjectFactory;

/**
 * @author Ryan D. Brooks
 */
public class RelationLoadProcessor extends LoadProcessor<RelationData, RelationObjectFactory> {

   public RelationLoadProcessor(RelationObjectFactory factory) {
      super(factory);
   }

   @Override
   protected RelationData createData(Object conditions, RelationObjectFactory factory, IOseeStatement chStmt, Options options) throws OseeCoreException {
      int branchId = chStmt.getInt("branch_id");
      int txId = chStmt.getInt("transaction_id");
      long gamma = chStmt.getInt("gamma_id");

      boolean historical = OptionsUtil.isHistorical(options);
      VersionData version = factory.createVersion(branchId, txId, gamma, historical);
      if (historical) {
         version.setStripeId(chStmt.getInt("stripe_transaction_id"));
      }

      int localId = chStmt.getInt("rel_link_id");
      int typeId = chStmt.getInt("rel_link_type_id");
      ModificationType modType = ModificationType.getMod(chStmt.getInt("mod_type"));

      int parentId = chStmt.getInt("art_id");
      int aArtId = chStmt.getInt("a_art_id");
      int bArtId = chStmt.getInt("b_art_id");
      String rationale = chStmt.getString("rationale");

      return factory.createRelationData(version, localId, typeId, modType, parentId, aArtId, bArtId, rationale);
   }
}