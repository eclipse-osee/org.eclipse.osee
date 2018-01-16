/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.convert;

import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.util.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.BranchReadable;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * See description below
 *
 * @author Donald G Dunne
 */
public class ConvertBaselineGuidToBaselineId extends AbstractConvertGuidToId {

   // Leave this attribute definition and conversion for other OSEE sites to convert
   private static final AttributeTypeToken BaselineBranchGuid =
      AttributeTypeToken.valueOf(1152921504606847145L, "ats.Baseline Branch Guid");

   public ConvertBaselineGuidToBaselineId(Log logger, JdbcClient jdbcClient, OrcsApi orcsApi, IAtsServer atsServer) {
      super(logger, jdbcClient, orcsApi, atsServer);
   }

   @Override
   public void run(XResultData data, boolean reportOnly, AtsApi atsApi) {
      if (reportOnly) {
         data.log("REPORT ONLY - Changes not persisted\n");
      }
      if (!getOrcsApi().getOrcsTypes().getAttributeTypes().exists(AtsAttributeTypes.BaselineBranchId)) {
         data.error("ats.BaselineBranchId is not configured for this database");
         return;
      }
      TransactionBuilder tx = createTransactionBuilder();
      int numChanges = 0;
      for (ArtifactReadable art : atsServer.getQuery().andTypeEquals(AtsArtifactTypes.Version,
         AtsArtifactTypes.TeamDefinition).andExists(BaselineBranchGuid).getResults()) {
         List<String> attributeValues = art.getAttributeValues(BaselineBranchGuid);
         for (String guid : attributeValues) {
            if (!guid.isEmpty()) {
               BranchReadable branch = null;
               try {
                  branch = getBranch(guid);
               } catch (Exception ex) {
                  // do nothing
               }
               if (branch == null) {
                  data.errorf("Branch with guid %s can't be found", guid);
               } else {
                  String baseLine = art.getSoleAttributeAsString(AtsAttributeTypes.BaselineBranchId, null);
                  if (!Strings.isValid(baseLine) || isIdDifferent(baseLine, branch)) {
                     if (!Strings.isValid(baseLine)) {
                        data.logf("Adding id attribute of value %s to artifact type [%s] name [%s] id [%s]\n", branch,
                           art.getArtifactType(), art.getName(), art.getGuid());
                     } else if (isIdDifferent(baseLine, branch)) {
                        data.logf("Updating id attribute of value %s to artifact type [%s] name [%s] id [%s]\n", branch,
                           art.getArtifactType(), art.getName(), art.getGuid());
                     }
                     numChanges++;
                     if (!reportOnly) {
                        tx.setSoleAttributeValue(art, AtsAttributeTypes.BaselineBranchId, branch.getIdString());
                     }
                  }
               }
            }
         }
      }
      if (!reportOnly) {
         data.log("\n" + numChanges + " Changes Persisted");
         tx.commit();
      } else {
         data.log("\n" + numChanges + " Need to be Changed");
      }
   }

   @Override
   public String getDescription() {
      StringBuffer data = new StringBuffer();
      data.append("ConvertBaselineGuidToBaselineId (required conversion)\n\n");
      data.append("Necessary for upgrading from OSEE 0.16.2 to 0.17.0");
      data.append("- Verify that ats.BaselineBranchId is a valid attribute type\n");
      data.append("- Verify Add id attribute for every ats.BaselineBranchGuid attribute on Version artifacts\n");
      data.append(
         "- Verify Add id attribute for every ats.BaselineBranchGuid attribute on Team Definition artifacts\n\n");
      data.append("NOTE: This operation can be run multiple times\n");
      data.append(
         "Manual Cleanup (optional): Use Purge Attribute Type BLAM to remove the ats.BaselineBranchGuid attributes.");
      return data.toString();
   }

   @Override
   public String getName() {
      return "ConvertBaselineGuidToBaselineId";
   }

   private boolean isIdDifferent(String id, BranchId branch) {
      return Strings.isValid(id) && branch.notEqual(Long.valueOf(id));
   }
}
