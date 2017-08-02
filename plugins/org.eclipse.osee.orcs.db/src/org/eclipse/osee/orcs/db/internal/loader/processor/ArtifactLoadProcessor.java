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
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.core.ds.VersionData;
import org.eclipse.osee.orcs.db.internal.loader.data.ArtifactObjectFactory;

/**
 * @author Ryan D. Brooks
 */
public class ArtifactLoadProcessor extends LoadProcessor<ArtifactData, ArtifactObjectFactory> {

   public ArtifactLoadProcessor(ArtifactObjectFactory factory) {
      super(factory);
   }

   @Override
   protected ArtifactData createData(Object conditions, ArtifactObjectFactory factory, JdbcStatement chStmt, Options options) throws OseeCoreException {
      ArtifactData toReturn = null;

      int artifactId = chStmt.getInt("id2");
      BranchId branch = BranchId.create(chStmt.getLong("branch_id"), OptionsUtil.getFromBranchView(options));

      CreateConditions onCreate = asConditions(conditions);
      if (!onCreate.isSame(branch, artifactId)) {

         ModificationType modType = ModificationType.getMod(chStmt.getInt("mod_type"));
         ApplicabilityId applicId = ApplicabilityId.valueOf(chStmt.getLong("app_id"));
         // assumption: SQL is returning unwanted deleted artifacts only in the historical case
         boolean historical = OptionsUtil.isHistorical(options);
         if (!historical || OptionsUtil.areDeletedArtifactsIncluded(options) || modType != ModificationType.DELETED) {
            GammaId gamma = GammaId.valueOf(chStmt.getLong("gamma_id"));
            TransactionId txId = TransactionId.valueOf(chStmt.getLong("transaction_id"));

            VersionData version = factory.createVersion(branch, txId, gamma, historical);

            if (historical) {
               version.setStripeId(TransactionId.valueOf(chStmt.getLong("stripe_transaction_id")));
            }

            long typeId = chStmt.getLong("art_type_id");
            String guid = chStmt.getString("guid");
            toReturn = factory.createArtifactData(version, artifactId, typeId, modType, guid, applicId);
         }
         onCreate.saveConditions(branch, artifactId);
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
      int previousArtId = -1;
      BranchId previousBranchId = BranchId.SENTINEL;

      boolean isSame(BranchId branch, int artifactId) {
         return previousBranchId.equals(branch) && previousArtId == artifactId;
      }

      void saveConditions(BranchId branch, int artifactId) {
         previousBranchId = branch;
         previousArtId = artifactId;
      }
   }
}