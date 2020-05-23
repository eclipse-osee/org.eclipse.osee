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

import java.util.Date;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.core.ds.VersionData;
import org.eclipse.osee.orcs.data.AttributeTypes;
import org.eclipse.osee.orcs.db.internal.loader.data.AttributeObjectFactory;

/**
 * @author Roberto E. Escobar
 */
public class AttributeLoadProcessor extends LoadProcessor<AttributeData, AttributeObjectFactory> {

   private final Log logger;
   private final AttributeTypes attributeTypes;
   private final OrcsTokenService tokenService;

   public AttributeLoadProcessor(Log logger, AttributeObjectFactory factory, AttributeTypes attributeTypes, OrcsTokenService tokenService) {
      super(factory);
      this.logger = logger;
      this.attributeTypes = attributeTypes;
      this.tokenService = tokenService;
   }

   @Override
   protected AttributeData createData(Object conditions, AttributeObjectFactory factory, JdbcStatement chStmt, Options options) {
      AttributeData toReturn = null;

      BranchId branch = BranchId.create(chStmt.getLong("branch_id"), OptionsUtil.getFromBranchView(options));
      ArtifactId artId = ArtifactId.valueOf(chStmt.getLong("id2"));
      int attrId = chStmt.getInt("attr_id");
      GammaId gammaId = GammaId.valueOf(chStmt.getLong("gamma_id"));
      ModificationType modType = ModificationType.valueOf(chStmt.getInt("mod_type"));
      ApplicabilityId applicId = ApplicabilityId.valueOf(chStmt.getLong("app_id"));

      boolean historical = OptionsUtil.isHistorical(options);

      CreateConditions condition = asConditions(conditions);
      if (!condition.isSame(branch, artId, attrId)) {
         condition.saveConditions(branch, artId, attrId, gammaId, modType);

         TransactionId txId = TransactionId.valueOf(chStmt.getLong("transaction_id"));

         VersionData version = factory.createVersion(branch, txId, gammaId, historical);
         if (historical) {
            version.setStripeId(TransactionId.valueOf(chStmt.getLong("stripe_transaction_id")));
         }

         AttributeTypeGeneric<?> attributeType = tokenService.getAttributeType(chStmt.getLong("attr_type_id"));

         Object value = null;
         if (attributeType.isBoolean()) {
            value = chStmt.getBoolean("value");
         } else if (attributeType.isDouble()) {
            value = chStmt.getDouble("value");
         } else if (attributeType.isInteger()) {
            value = chStmt.getInt("value");
         } else if (attributeType.isLong()) {
            value = chStmt.getLong("value");
         } else if (attributeType.isArtifactId()) {
            String id = chStmt.getString("value");
            if (Strings.isNumeric(id)) {
               value = ArtifactId.valueOf(id);
            } else {
               logger.error("Inavlid non-numeric value [%s] for ArtRefAttribute [%s] attrId [%s] on artId [%s]", id,
                  attributeType.getIdString(), attrId, artId);
            }
         } else if (attributeType.isBranchId()) {
            value = BranchId.valueOf(chStmt.getString("value"));
         } else if (attributeType.isDate()) {
            value = new Date(chStmt.getLong("value"));
         } else {
            value = chStmt.getString("value");
            if (attributeType.isEnumerated()) {
               value = Strings.intern((String) value);
            }
         }

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
      int previousAttrId = -1;
      GammaId previousGammaId = GammaId.SENTINEL;
      ModificationType previousModType = null;

      boolean isSame(BranchId branch, ArtifactId artifactId, int attrId) {
         return previousBranchId.equals(branch) && previousArtId.equals(artifactId) && previousAttrId == attrId;
      }

      void saveConditions(BranchId branch, ArtifactId artifactId, int attrId, GammaId gammaId, ModificationType modType) {
         previousBranchId = branch;
         previousArtId = artifactId;
         previousAttrId = attrId;
         previousGammaId = gammaId;
         previousModType = modType;
      }
   }
}