/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.rest.internal.convert;

import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * See description below
 *
 * @author Donald G Dunne
 */
public class ConvertBaselineGuidToBaselineId extends AbstractConvertGuidToId {

   public ConvertBaselineGuidToBaselineId(Log logger, JdbcClient jdbcClient, OrcsApi orcsApi, AtsApi atsApi) {
      super(logger, jdbcClient, orcsApi, atsApi);
   }

   @Override
   public void run(XResultData data, boolean reportOnly, AtsApi atsApi) {
      if (reportOnly) {
         data.log("REPORT ONLY - Changes not persisted\n");
      }
      TransactionBuilder tx = createTransactionBuilder();
      int numChanges = 0;
      for (ArtifactReadable art : orcsApi.getQueryFactory().fromBranch(atsApi.getAtsBranch()).andTypeEquals(
         AtsArtifactTypes.Version, AtsArtifactTypes.TeamDefinition).andExists(
            AtsAttributeTypes.BaselineBranchGuid).getResults()) {
         List<String> attributeValues = art.getAttributeValues(AtsAttributeTypes.BaselineBranchGuid);
         for (String guid : attributeValues) {
            if (!guid.isEmpty()) {
               BranchId branch = null;
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
                           art.getArtifactType(), art.getName(), art.getIdString());
                     } else if (isIdDifferent(baseLine, branch)) {
                        data.logf("Updating id attribute of value %s to artifact type [%s] name [%s] id [%s]\n", branch,
                           art.getArtifactType(), art.getName(), art.getIdString());
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
