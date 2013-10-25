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
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
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
   protected RelationData createData(Object conditions, RelationObjectFactory factory, IOseeStatement chStmt, Options options) throws OseeCoreException {
      RelationData toReturn = null;

      int branchId = chStmt.getInt("branch_id");
      int aArtId = chStmt.getInt("a_art_id");
      int bArtId = chStmt.getInt("b_art_id");
      int typeId = chStmt.getInt("rel_link_type_id");
      long gammaId = chStmt.getInt("gamma_id");

      boolean historical = OptionsUtil.isHistorical(options);

      CreateConditions condition = asConditions(conditions);
      if (!condition.isSame(branchId, aArtId, bArtId, typeId)) {
         condition.saveConditions(branchId, aArtId, bArtId, typeId, gammaId);

         int txId = chStmt.getInt("transaction_id");

         VersionData version = factory.createVersion(branchId, txId, gammaId, historical);
         if (historical) {
            version.setStripeId(chStmt.getInt("stripe_transaction_id"));
         }

         int localId = chStmt.getInt("rel_link_id");
         ModificationType modType = ModificationType.getMod(chStmt.getInt("mod_type"));

         String rationale = chStmt.getString("rationale");

         toReturn = factory.createRelationData(version, localId, typeId, modType, aArtId, bArtId, rationale);

      } else {
         if (!historical) {
            logger.warn(
               "multiple relation versions for branch[%d] rel_type [%d] a_artId[%d] b_artId[%s] previousGammaId[%s] currentGammaId[%s]",
               branchId, typeId, aArtId, bArtId, condition.previousGammaId, gammaId);
         }
      }
      return toReturn;
   }

   @Override
   protected Object createPreConditions() {
      return new CreateConditions();
   }

   private CreateConditions asConditions(Object conditions) {
      return (CreateConditions) conditions;
   }

   private static final class CreateConditions {
      int previousBranchId = -1;
      int previousArtIdA = -1;
      int previousArtIdB = -1;
      int previousTypeId = -1;
      long previousGammaId = -1;

      boolean isSame(int branchId, int aArtId, int bArtId, int typeId) {
         return previousBranchId == branchId && previousArtIdA == aArtId && previousArtIdB == bArtId && previousTypeId == typeId;
      }

      void saveConditions(int branchId, int aArtId, int bArtId, int typeId, long gammaId) {
         previousBranchId = branchId;
         previousArtIdA = aArtId;
         previousArtIdB = bArtId;
         previousTypeId = typeId;
         previousGammaId = gammaId;
      }
   }
}