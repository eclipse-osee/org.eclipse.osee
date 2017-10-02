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
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.RelationTypeId;
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
   protected RelationData createData(Object conditions, RelationObjectFactory factory, JdbcStatement chStmt, Options options)  {
      RelationData toReturn = null;

      BranchId branch = BranchId.create(chStmt.getLong("branch_id"), OptionsUtil.getFromBranchView(options));
      ArtifactId aArtId = ArtifactId.valueOf(chStmt.getLong("a_art_id"));
      ArtifactId bArtId = ArtifactId.valueOf(chStmt.getLong("b_art_id"));
      RelationTypeId relationType = RelationTypeId.valueOf(chStmt.getLong("rel_link_type_id"));
      GammaId gammaId = GammaId.valueOf(chStmt.getInt("gamma_id"));
      ApplicabilityId applicId = ApplicabilityId.valueOf(chStmt.getLong("app_id"));

      boolean historical = OptionsUtil.isHistorical(options);

      CreateConditions condition = asConditions(conditions);
      if (!condition.isSame(branch, aArtId, bArtId, relationType)) {
         condition.saveConditions(branch, aArtId, bArtId, relationType, gammaId);

         TransactionId txId = TransactionId.valueOf(chStmt.getLong("transaction_id"));

         VersionData version = factory.createVersion(branch, txId, gammaId, historical);
         if (historical) {
            version.setStripeId(TransactionId.valueOf(chStmt.getLong("stripe_transaction_id")));
         }

         int localId = chStmt.getInt("rel_link_id");
         ModificationType modType = ModificationType.valueOf(chStmt.getInt("mod_type"));

         String rationale = chStmt.getString("rationale");
         // Oracle returns nulls as null. HSQLDB returns as "".  Rationale can not be null.
         if (rationale == null) {
            rationale = "";
         }

         toReturn =
            factory.createRelationData(version, localId, relationType, modType, aArtId, bArtId, rationale, applicId);

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
      RelationTypeId previousTypeId = RelationTypeId.SENTINEL;
      GammaId previousGammaId = GammaId.SENTINEL;

      boolean isSame(BranchId branch, ArtifactId aArtId, ArtifactId bArtId, RelationTypeId typeId) {
         return previousBranchId.equals(branch) && previousArtIdA.equals(aArtId) && previousArtIdB.equals(
            bArtId) && previousTypeId.equals(typeId);
      }

      void saveConditions(BranchId branch, ArtifactId aArtId, ArtifactId bArtId, RelationTypeId typeId, GammaId gammaId) {
         previousBranchId = branch;
         previousArtIdA = aArtId;
         previousArtIdB = bArtId;
         previousTypeId = typeId;
         previousGammaId = gammaId;
      }
   }
}