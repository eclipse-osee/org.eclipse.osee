/*******************************************************************************
 * Copyright (c) 2022 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.util.health.operations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;

/**
 * @author Donald G. Dunne
 */
public class HealthOperations {

   private final AtsApi atsApi;

   public HealthOperations(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   public XResultData getDuplicateArtifactReport(ArtifactId id, String newArtId) {
      XResultData rd = new XResultData();
      rd.logf("Duplicate Artifact Analysis Report\n\n");

      ArtifactToken art = atsApi.getQueryService().getArtifact(id);
      rd.logf("Artifact [%s]\n", art.getIdString());
      rd.logf("New Artifact Id [%s]\n", newArtId);

      List<Map<String, String>> artRows =
         atsApi.getQueryService().query("select * from osee_artifact where art_id = ?", id.getIdString());

      // Artifact data
      int x = 1;
      for (Map<String, String> rowMap : artRows) {
         rd.log("\nArtifact " + x++ + "\n============");
         rd.log("ARTIFACT: " + rowMap + "");

         String artTypeStr = rowMap.get("ART_TYPE_ID");
         Long artTypeId = Long.valueOf(artTypeStr);
         ArtifactTypeToken artType = atsApi.tokenService().getArtifactType(artTypeId);
         rd.logf("ART_TYPE: %s\n", artType.toStringWithId());

         String gammaStr = rowMap.get("GAMMA_ID");
         Long gammaId = Long.valueOf(gammaStr);

         List<Map<String, String>> txsRows =
            atsApi.getQueryService().query("select * from osee_txs where branch_id = 570 and gamma_id = ?", gammaId);
         rd.logf("TXS: %s\n", txsRows);

         for (Map<String, String> txRow : txsRows) {
            String transId = txRow.get("TRANSACTION_ID");
            List<Map<String, String>> txDetailsRows = atsApi.getQueryService().query(
               "select * from osee_tx_details where branch_id = 570 and transaction_id = ?", transId);
            rd.logf("TX_DETAILS: %s\n", txDetailsRows);
         }

      }
      rd.logf("\nACTION: Run ActionFactory.getNextArtifactId to get clean art_id;\n");
      rd.logf("ACTION: select * from osee_artifact_where art_id = %s;\n", id.getIdString());
      rd.logf("ACTION: run query and update desired row to new artifact id: %s;\n", newArtId);

      HashCollection<String, String> transToGamma = new HashCollection<String, String>();
      Map<String, Map<String, String>> gammaIdToAttrRow = new HashMap<String, Map<String, String>>();
      Map<String, Map<String, String>> gammaIdToRelRow = new HashMap<String, Map<String, String>>();
      Map<String, Map<String, String>> transIdToTxsRow = new HashMap<String, Map<String, String>>();
      Map<String, Map<String, String>> transIdToTxDetailsRow = new HashMap<String, Map<String, String>>();
      Map<String, String> transDateTotransId = new HashMap<String, String>();

      // Attribute data
      rd.log("\nAttributes \n============");
      List<Map<String, String>> attrRows =
         atsApi.getQueryService().query("select * from osee_attribute where art_id = ?", id.getIdString());
      for (Map<String, String> attrRow : attrRows) {
         rd.logf("ATTR: %s\n", attrRow);
         String gammaStr = attrRow.get("GAMMA_ID");

         gammaIdToAttrRow.put(gammaStr, attrRow);

         Map<String, String> txsRow = getTxsRow(gammaStr, rd);
         String transStr = txsRow.get("TRANSACTION_ID");
         transIdToTxsRow.put(transStr, txsRow);
         transToGamma.put(transStr, gammaStr);
      }

      // Relation data
      rd.log("\nRelation \n============");
      List<Map<String, String>> relRows = atsApi.getQueryService().query(
         "select * from osee_relation_link where a_art_id = ? or b_art_id = ?", id.getIdString(), id.getIdString());
      for (Map<String, String> relRow : relRows) {
         rd.logf("REL: %s\n", relRow);
         String gammaStr = relRow.get("GAMMA_ID");

         gammaIdToRelRow.put(gammaStr, relRow);

         Map<String, String> txsRow = getTxsRow(gammaStr, rd);
         String transStr = txsRow.get("TRANSACTION_ID");
         transIdToTxsRow.put(transStr, txsRow);
         transToGamma.put(transStr, gammaStr);
      }

      for (String transId : transToGamma.keySet()) {

         List<Map<String, String>> txDetailsRows = atsApi.getQueryService().query(
            "select * from osee_tx_details where branch_id = 570 and transaction_id = ?", transId);

         // Store transId to details for use later
         transIdToTxDetailsRow.put(transId, txDetailsRows.iterator().next());

         // Store transaction date so can sort by it for transactions section
         String txTime = txDetailsRows.iterator().next().get("TIME");
         transDateTotransId.put(txTime, transId);

      }

      List<String> dates = new ArrayList<String>();
      dates.addAll(transDateTotransId.keySet());
      Collections.sort(dates);

      rd.log("\nTransactions \n============");
      for (String transDate : dates) {
         String transId = transDateTotransId.get(transDate);

         Map<String, String> txsRow = transIdToTxsRow.get(transId);
         rd.logf("\nTRANS: %s\n", txsRow);

         Map<String, String> txDetails = transIdToTxDetailsRow.get(transId);
         rd.logf("DETAILS: %s\n", txDetails);

         // process attrs
         List<String> attrGammas = new ArrayList<String>();
         for (String gamma : transToGamma.getValues(transId)) {
            Map<String, String> attrRow = gammaIdToAttrRow.get(gamma);
            if (attrRow != null) {
               attrGammas.add(gamma);
               rd.logf("ATTR: %s\n", attrRow);
            }
         }

         if (!attrGammas.isEmpty()) {
            rd.logf("\nACTION: IF this transaction belongs to new art_id, Update attrs with new art_id;\n");
            rd.logf("ACTION: update osee_attribute set art_id = %s  where gamma_id in (%s);\n\n", newArtId,
               org.eclipse.osee.framework.jdk.core.util.Collections.toString(",", attrGammas));
         }

         // process rels
         List<String> relGammas = new ArrayList<String>();
         for (String gamma : transToGamma.getValues(transId)) {
            Map<String, String> relRow = gammaIdToRelRow.get(gamma);
            if (relRow != null) {
               relGammas.add(gamma);
               rd.logf("REL: %s\n", relRow);
            }
         }

         if (!relGammas.isEmpty()) {
            rd.logf("\n\nACTION: IF this transaction belongs to new art_id, Update rels with new art_id;\n");
            rd.logf("ACTION: update osee_relation_link set a_art_id = %s  where gamma_id in (%s);\n", newArtId,
               org.eclipse.osee.framework.jdk.core.util.Collections.toString(",", relGammas));
            rd.logf("ACTION: update osee_relation_link set b_art_id = %s  where gamma_id in (%s);\n\n", newArtId,
               org.eclipse.osee.framework.jdk.core.util.Collections.toString(",", relGammas));
         }

      }

      return rd;
   }

   public Map<String, String> getTxDetailsRow(String transId, XResultData rd) {
      List<Map<String, String>> txDetailsRows =
         atsApi.getQueryService().query("select * from osee_tx_details where transaction_id = ?", transId);
      if (txDetailsRows.size() != 1) {
         rd.errorf("Unexpected %s txDetailsRows returned for transId %s\n", txDetailsRows.size(), transId);
      }
      return txDetailsRows.iterator().next();
   }

   public Map<String, String> getTxsRow(String gammaStr, XResultData rd) {
      List<Map<String, String>> txsRows =
         atsApi.getQueryService().query("select * from osee_txs where branch_id = 570 and gamma_id = ?", gammaStr);
      if (txsRows.size() != 1) {
         rd.errorf("Unexpected %s txRows returned for gamma %s\n", txsRows.size(), gammaStr);
      }
      return txsRows.iterator().next();
   }
}
