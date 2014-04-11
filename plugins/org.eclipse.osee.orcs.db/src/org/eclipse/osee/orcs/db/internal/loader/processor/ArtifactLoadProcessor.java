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
   protected ArtifactData createData(Object conditions, ArtifactObjectFactory factory, IOseeStatement chStmt, Options options) throws OseeCoreException {
      ArtifactData toReturn = null;

      int artifactId = chStmt.getInt("art_id");
      long branchUuid = chStmt.getLong("branch_id");

      CreateConditions onCreate = asConditions(conditions);
      if (!onCreate.isSame(branchUuid, artifactId)) {

         ModificationType modType = ModificationType.getMod(chStmt.getInt("mod_type"));
         // assumption: SQL is returning unwanted deleted artifacts only in the historical case
         boolean historical = OptionsUtil.isHistorical(options);
         if (!historical || OptionsUtil.areDeletedArtifactsIncluded(options) || modType != ModificationType.DELETED) {
            long gamma = chStmt.getInt("gamma_id");
            int txId = chStmt.getInt("transaction_id");

            VersionData version = factory.createVersion(branchUuid, txId, gamma, historical);

            if (historical) {
               version.setStripeId(chStmt.getInt("stripe_transaction_id"));
            }

            long typeId = chStmt.getLong("art_type_id");
            String guid = chStmt.getString("guid");
            toReturn = factory.createArtifactData(version, artifactId, typeId, modType, guid);
         }
         onCreate.saveConditions(branchUuid, artifactId);
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
      int previousArtId = -1;
      long previousBranchId = -1;

      boolean isSame(long branchUuid, int artifactId) {
         return previousBranchId == branchUuid && previousArtId == artifactId;
      }

      void saveConditions(long branchUuid, int artifactId) {
         previousBranchId = branchUuid;
         previousArtId = artifactId;
      }
   }
}