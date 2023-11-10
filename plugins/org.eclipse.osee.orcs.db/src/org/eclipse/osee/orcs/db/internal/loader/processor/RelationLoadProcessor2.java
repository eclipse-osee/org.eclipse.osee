/*********************************************************************
 * Copyright (c) 2022 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.db.internal.loader.processor;

import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.RelationId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TxCurrent;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.core.ds.VersionData;
import org.eclipse.osee.orcs.db.internal.loader.data.RelationObjectFactory;

/**
 * @author Audrey Denk
 */
public class RelationLoadProcessor2 extends LoadProcessor<RelationData, RelationObjectFactory> {

   private final Log logger;
   private final OrcsTokenService tokenService;

   public RelationLoadProcessor2(Log logger, RelationObjectFactory factory, OrcsTokenService tokenService) {
      super(factory);
      this.logger = logger;
      this.tokenService = tokenService;
   }

   @Override
   protected RelationData createData(Object conditions, RelationObjectFactory factory, JdbcStatement chStmt,
      Options options) {
      RelationData toReturn = null;

      BranchId branch = BranchId.create(chStmt.getLong("branch_id"), OptionsUtil.getFromBranchView(options));
      ArtifactId aArtId = ArtifactId.valueOf(chStmt.getLong("a_art_id"));
      ArtifactId bArtId = ArtifactId.valueOf(chStmt.getLong("b_art_id"));
      RelationTypeToken relationType = tokenService.getRelationTypeOrCreate(chStmt.getLong("rel_type"));
      GammaId gammaId = GammaId.valueOf(chStmt.getLong("gamma_id"));
      TxCurrent txCurrent = TxCurrent.valueOf(chStmt.getInt("tx_current")); 
      ApplicabilityId applicId = ApplicabilityId.valueOf(chStmt.getLong("app_id"));
      int rel_order = chStmt.getInt("rel_order");

      boolean historical = OptionsUtil.isHistorical(options);

      CreateConditions condition = asConditions(conditions);
      if (!condition.isSame(branch, aArtId, bArtId, relationType, rel_order)) {
         condition.saveConditions(branch, aArtId, bArtId, relationType, gammaId, rel_order);

         TransactionId txId = TransactionId.valueOf(chStmt.getLong("transaction_id"));

         VersionData version = factory.createVersion(branch, txId, gammaId, txCurrent, historical);
      
         if (historical) {
            version.setStripeId(TransactionId.valueOf(chStmt.getLong("stripe_transaction_id")));
         }

         ModificationType modType = ModificationType.valueOf(chStmt.getInt("mod_type"));

         ArtifactId relArtId = ArtifactId.valueOf(chStmt.getLong("rel_art_id"));

         toReturn = factory.createRelationData(version, RelationId.valueOf(gammaId.getIdString()), relationType, modType, aArtId, bArtId,
            relArtId, rel_order, applicId);

      } else {
         if (!historical) {
            logger.warn(
               "multiple relation versions for branch[%s] rel_type [%s] a_artId[%s] b_artId[%s] previousGammaId[%s] currentGammaId[%s]",
               branch, relationType, aArtId, bArtId, condition.previousGammaId, gammaId);
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
      BranchId previousBranchId = BranchId.SENTINEL;
      ArtifactId previousArtIdA = ArtifactId.SENTINEL;
      ArtifactId previousArtIdB = ArtifactId.SENTINEL;
      RelationTypeToken previousTypeId = RelationTypeToken.SENTINEL;
      GammaId previousGammaId = GammaId.SENTINEL;
      int previousRelOrder = 0;

      boolean isSame(BranchId branch, ArtifactId aArtId, ArtifactId bArtId, RelationTypeToken typeId, int relOrder) {
         return previousBranchId.equals(branch) && previousArtIdA.equals(aArtId) && previousArtIdB.equals(
            bArtId) && previousTypeId.equals(typeId) && previousRelOrder == relOrder;
      }

      void saveConditions(BranchId branch, ArtifactId aArtId, ArtifactId bArtId, RelationTypeToken typeId,
         GammaId gammaId, int relOrder) {
         previousBranchId = branch;
         previousArtIdA = aArtId;
         previousArtIdB = bArtId;
         previousTypeId = typeId;
         previousGammaId = gammaId;
         previousRelOrder = relOrder;
      }
   }
}