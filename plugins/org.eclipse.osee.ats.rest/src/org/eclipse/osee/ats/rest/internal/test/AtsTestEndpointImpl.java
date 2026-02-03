/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.ats.rest.internal.test;

import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.AtsTestEndpointApi;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Donald G. Dunne
 */
public class AtsTestEndpointImpl implements AtsTestEndpointApi {
   private final AtsApi atsApi;
   private final OrcsApi orcsApi;

   public AtsTestEndpointImpl(AtsApi atsApi, OrcsApi orcsApi) {
      this.atsApi = atsApi;
      this.orcsApi = orcsApi;
   }

   @Override
   public XResultData testVersions() {
      return (new VersionRelationToggleServerTest(atsApi, new XResultData())).run();
   }

   @Override
   public XResultData testTransactions() {
      return (new TransactionsServerTest(atsApi, orcsApi, new XResultData())).run();
   }

   @Override
   public XResultData testSearchCriteria() {
      return (new ServerQueryTest(atsApi, orcsApi, new XResultData())).run();
   }

   @Override
   public XResultData testTransactionAuthors() {
      XResultData rd = new XResultData();
      List<Map<String, String>> query =
         atsApi.getQueryServiceServer().query("select * from osee_tx_details where author < 1");
      rd.log(query.toString());
      if (!query.isEmpty()) {
         rd.errorf("All transactions MUST have valid author.  Errors: " + query.toString());
      }
      return rd;
   }

   @Override
   public XResultData validateWorkDefReferences() {
      XResultData rd = new XResultData();
      rd.log("\n\nValidating (tx_current==1) Work Definition Reference attrs...");
      List<Map<String, String>> query = atsApi.getQueryServiceServer().query( //
         "SELECT DISTINCT(attr.value) FROM osee_attribute attr, osee_txs txs WHERE \n" + //
            "txs.branch_id = 570 and attr.gamma_id = txs.gamma_id \n" + //
            "AND txs.tx_current = 1 AND attr.ATTR_TYPE_ID = " + //
            AtsAttributeTypes.WorkflowDefinitionReference.getIdString());
      for (Map<String, String> entry : query) {
         String idStr = entry.values().iterator().next();
         Long id = Long.valueOf(idStr);
         WorkDefinition workDef = atsApi.getWorkDefinitionService().getWorkDefinition(id);
         if (workDef == null) {
            rd.errorf("Work Def Id = %s - InValid\n", idStr);
         } else {
            rd.logf("Work Def Id = %s - Valid\n", idStr);
         }
      }
      return rd;
   }

}