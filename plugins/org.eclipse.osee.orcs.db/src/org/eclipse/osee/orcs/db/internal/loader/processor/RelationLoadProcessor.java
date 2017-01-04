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

import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.core.ds.VersionData;
import org.eclipse.osee.orcs.db.internal.loader.data.RelationObjectFactory;

/**
 * @author Ryan D. Brooks
 */
public class RelationLoadProcessor extends LoadProcessor<RelationData, RelationObjectFactory> {

   private final Log logger;

   public RelationLoadProcessor(Log logger, RelationObjectFactory factory) {
      super(factory);
      this.logger = logger;
   }

   @Override
   protected RelationData createData(Object conditions, RelationObjectFactory factory, JdbcStatement chStmt, Options options) throws OseeCoreException {
      RelationData toReturn = null;

      long branchUuid = chStmt.getLong("branch_id");
      int aArtId = chStmt.getInt("a_art_id");
      int bArtId = chStmt.getInt("b_art_id");
      long typeId = chStmt.getLong("rel_link_type_id");
      long gammaId = chStmt.getInt("gamma_id");
      ApplicabilityId applicId = ApplicabilityId.valueOf(chStmt.getLong("app_id"));

      boolean historical = OptionsUtil.isHistorical(options);

      CreateConditions condition = asConditions(conditions);
      if (!condition.isSame(branchUuid, aArtId, bArtId, typeId)) {
         condition.saveConditions(branchUuid, aArtId, bArtId, typeId, gammaId);

         TransactionId txId = TransactionId.valueOf(chStmt.getLong("transaction_id"));

         VersionData version = factory.createVersion(branchUuid, txId, gammaId, historical);
         if (historical) {
            version.setStripeId(TransactionId.valueOf(chStmt.getLong("stripe_transaction_id")));
         }

         int localId = chStmt.getInt("rel_link_id");
         ModificationType modType = ModificationType.getMod(chStmt.getInt("mod_type"));

         String rationale = chStmt.getString("rationale");
         // Oracle returns nulls as null. HSQLDB returns as "".  Rationale can not be null.
         if (rationale == null) {
            rationale = "";
         }

         toReturn = factory.createRelationData(version, localId, typeId, modType, aArtId, bArtId, rationale, applicId);

      } else {
         if (!historical) {
            logger.warn(
               "multiple relation versions for branch[%d] rel_type [%d] a_artId[%d] b_artId[%s] previousGammaId[%s] currentGammaId[%s]",
               branchUuid, typeId, aArtId, bArtId, condition.previousGammaId, gammaId);
         }
      }
      return toReturn;
   }

   @Override
   protected Object createPreConditions(Options options) {
      return new CreateConditions();
   }

   private CreateConditions asConditions(Object conditions) {
      return (CreateConditions) conditions;
   }

   private static final class CreateConditions {
      long previousBranchId = -1;
      int previousArtIdA = -1;
      int previousArtIdB = -1;
      long previousTypeId = -1;
      long previousGammaId = -1;

      boolean isSame(long branchUuid, int aArtId, int bArtId, long typeId) {
         return previousBranchId == branchUuid && previousArtIdA == aArtId && previousArtIdB == bArtId && previousTypeId == typeId;
      }

      void saveConditions(long branchUuid, int aArtId, int bArtId, long typeId, long gammaId) {
         previousBranchId = branchUuid;
         previousArtIdA = aArtId;
         previousArtIdB = bArtId;
         previousTypeId = typeId;
         previousGammaId = gammaId;
      }
   }
}