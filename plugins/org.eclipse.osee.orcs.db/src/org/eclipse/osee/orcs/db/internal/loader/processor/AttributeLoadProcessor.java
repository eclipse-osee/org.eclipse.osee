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
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.core.ds.VersionData;
import org.eclipse.osee.orcs.db.internal.loader.data.AttributeObjectFactory;

/**
 * @author Roberto E. Escobar
 */
public class AttributeLoadProcessor extends LoadProcessor<AttributeData, AttributeObjectFactory> {

   private Log logger;

   public AttributeLoadProcessor(Log logger, AttributeObjectFactory factory) {
      super(factory);
   }

   @Override
   protected AttributeData createData(Object conditions, AttributeObjectFactory factory, IOseeStatement chStmt, Options options) throws OseeCoreException {
      AttributeData toReturn = null;

      long branchId = chStmt.getLong("branch_id");
      int artId = chStmt.getInt("art_id");
      int attrId = chStmt.getInt("attr_id");
      long gammaId = chStmt.getInt("gamma_id");
      ModificationType modType = ModificationType.getMod(chStmt.getInt("mod_type"));

      boolean historical = OptionsUtil.isHistorical(options);

      CreateConditions condition = asConditions(conditions);
      if (!condition.isSame(branchId, artId, attrId)) {
         condition.saveConditions(branchId, artId, attrId, gammaId, modType);

         int txId = chStmt.getInt("transaction_id");

         VersionData version = factory.createVersion(branchId, txId, gammaId, historical);
         if (historical) {
            version.setStripeId(chStmt.getInt("stripe_transaction_id"));
         }

         long typeId = chStmt.getLong("attr_type_id");

         String value = chStmt.getString("value");
         String uri = chStmt.getString("uri");
         if (typeId == 167) {
            System.err.println(String.format("Loading artId[%s] attrTypeId[%s]  value[%s]", artId, typeId, value));
            System.out.println("here");
         }

         toReturn = factory.createAttributeData(version, attrId, typeId, modType, artId, value, uri);

      } else {
         if (!historical) {
            logger.warn(
               "multiple attribute versions for attribute id [%d] artifact id[%d] branch[%d] previousGammaId[%s] currentGammaId[%s] previousModType[%s] currentModType[%s]",
               attrId, artId, branchId, condition.previousGammaId, gammaId, condition.previousModType, modType);
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
      int previousArtId = -1;
      long previousBranchId = -1;
      int previousAttrId = -1;
      long previousGammaId = -1;
      ModificationType previousModType = null;

      boolean isSame(long branchId, int artifactId, int attrId) {
         return previousBranchId == branchId && previousArtId == artifactId && previousAttrId == attrId;
      }

      void saveConditions(long branchId, int artifactId, int attrId, long gammaId, ModificationType modType) {
         previousBranchId = branchId;
         previousArtId = artifactId;
         previousAttrId = attrId;
         previousGammaId = gammaId;
         previousModType = modType;
      }

   }
}