/*********************************************************************
 * Copyright (c) 2012 Boeing
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
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TxCurrent;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.core.ds.VersionData;
import org.eclipse.osee.orcs.db.internal.loader.data.AttributeObjectFactory;

/**
 * @author Roberto E. Escobar
 */
public class AttributeLoadProcessor extends LoadProcessor<AttributeData, AttributeObjectFactory> {

   private final Log logger;
   private final OrcsTokenService tokenService;

   public AttributeLoadProcessor(Log logger, AttributeObjectFactory factory, OrcsTokenService tokenService) {
      super(factory);
      this.logger = logger;
      this.tokenService = tokenService;
   }

   @Override
   protected AttributeData createData(Object conditions, AttributeObjectFactory factory, JdbcStatement chStmt, Options options) {
      AttributeData toReturn = null;

      BranchId branch = BranchId.create(chStmt.getLong("branch_id"), OptionsUtil.getFromBranchView(options));
      ArtifactId artId = ArtifactId.valueOf(chStmt.getLong("id2"));
      AttributeId attrId = AttributeId.valueOf(chStmt.getLong("attr_id"));
      GammaId gammaId = GammaId.valueOf(chStmt.getLong("gamma_id"));
      TxCurrent txCurrent = TxCurrent.valueOf(chStmt.getInt("tx_current"));
      ModificationType modType = ModificationType.valueOf(chStmt.getInt("mod_type"));
      ApplicabilityId applicId = ApplicabilityId.valueOf(chStmt.getLong("app_id"));

      boolean historical = OptionsUtil.isHistorical(options);

      CreateConditions condition = asConditions(conditions);
      if (!condition.isSame(branch, artId, attrId)) {
         condition.saveConditions(branch, artId, attrId, gammaId, modType);

         TransactionId txId = TransactionId.valueOf(chStmt.getLong("transaction_id"));

         VersionData version = factory.createVersion(branch, txId, gammaId, txCurrent, historical);
         if (historical) {
            version.setStripeId(TransactionId.valueOf(chStmt.getLong("stripe_transaction_id")));
         }

         AttributeTypeGeneric<?> attributeType = tokenService.getAttributeTypeOrCreate(chStmt.getLong("attr_type_id"));

         Object value = chStmt.loadAttributeValue(attributeType);
         String uri = chStmt.getString("uri");

         toReturn = factory.createAttributeData(version, attrId, attributeType, modType, artId, value, uri, applicId);

      } else {
         if (!historical) {
            logger.warn(
               "multiple attribute versions for attribute id [%s] artifact id[%s] branch[%s] previousGammaId[%s] currentGammaId[%s] previousModType[%s] currentModType[%s]",
               attrId, artId, branch, condition.previousGammaId, gammaId, condition.previousModType, modType);
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
      ArtifactId previousArtId = ArtifactId.SENTINEL;
      BranchId previousBranchId = BranchId.SENTINEL;
      AttributeId previousAttrId = AttributeId.SENTINEL;
      GammaId previousGammaId = GammaId.SENTINEL;
      ModificationType previousModType = null;

      boolean isSame(BranchId branch, ArtifactId artifactId, AttributeId attrId) {
         return previousBranchId.equals(branch) && previousArtId.equals(artifactId) && previousAttrId.equals(attrId);
      }

      void saveConditions(BranchId branch, ArtifactId artifactId, AttributeId attrId, GammaId gammaId, ModificationType modType) {
         previousBranchId = branch;
         previousArtId = artifactId;
         previousAttrId = attrId;
         previousGammaId = gammaId;
         previousModType = modType;
      }
   }
}