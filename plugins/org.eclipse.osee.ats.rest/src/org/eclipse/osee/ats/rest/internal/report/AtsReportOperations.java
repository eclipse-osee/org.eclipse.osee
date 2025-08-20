/*********************************************************************
 * Copyright (c) 2025 Boeing
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
package org.eclipse.osee.ats.rest.internal.report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Collections;

/**
 * @author Donald G. Dunne
 */
public class AtsReportOperations {

   private final AtsApi atsApi;

   public AtsReportOperations(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   /**
    * Generates a table given art type, attr types of last transaction(s) since given date
    *
    * @param date like 2025-07-15
    */
   public String getAttrDiffReport(String date, ArtifactTypeToken artType, List<AttributeTypeToken> attrTypes) {
      List<Map<String, String>> query = atsApi.getQueryService().query(getAttrFieldQuery(date, artType, attrTypes));
      XResultData rd = new XResultData();
      rd.logf("Attr Diff Report - Run: %s", new Date());
      rd.logf("<br/><br/>Begin Date - %s", date);
      rd.logf("<br/>Artifact Type: %s", artType.toStringWithId());
      rd.logf("<br/>Attr Type(s): %s<br/><br/>", attrTypes);
      rd.log(AHTML.beginMultiColumnTable(98, 2));
      rd.log(AHTML.addHeaderRowMultiColumnTable(
         Arrays.asList("ART_ID", "ATTR_TYPE", "AUTHOR_NAME", "DATE", "CURRENT VALUE", "PREVIOUS VALUE").toArray(
            new String[6])));
      for (Map<String, String> entry : query) {
         rd.addRaw(AHTML.addRowMultiColumnTable(entry.get("ART_ID"), entry.get("ATTR_TYPE"), entry.get("AUTHOR_NAME"),
            entry.get("LAST_CHANGED_DATE"), entry.get("CURRENT_VALUE"), entry.get("PREVIOUS_VALUE")));
      }
      rd.addRaw(AHTML.endMultiColumnTable());
      return rd.toString();
   }

   /**
    * @param date like 2025-07-15
    */
   private String getAttrFieldQuery(String date, ArtifactTypeToken artType, List<AttributeTypeToken> attrTypes) {

      StringBuilder sb = new StringBuilder();
      sb.append("WITH users(author_name,art_id) AS " + //
         "( SELECT attr.value, attr.ART_ID " + //
         "  FROM osee_txs txs, osee_artifact art, osee_txs attrTxs, osee_attribute attr " + //
         "  WHERE txs.branch_id = 570 AND txs.tx_current = 1 AND txs.gamma_id = art.gamma_id AND " + //
         "        art.art_type_id = 5 AND attrTxs.branch_id = 570 AND attrTxs.tx_current = 1 AND attrTxs.gamma_id = attr.GAMMA_ID " + //
         "        AND attr.art_id = art.art_id AND attr.attr_type_id = " + CoreAttributeTypes.Name.getIdString() + "), " + //
         "attrs AS ( " + //
         "    select art.art_id, attr.attr_id, attr.attr_type_id, attr.value, attr.gamma_id, txd.time, txd.transaction_id, txd.author, " + //
         "           row_number() over (partition by art.art_id, attr.attr_id order by txd.time desc) rn ," + //
         "           lead(attr.value) over (partition by attr.art_id, attr.attr_id order by txd.time desc) previous_value," + //
         "           max(time) OVER (PARTITION BY art.art_id, attr.attr_id) max_time " + //
         "           from osee_txs txs, osee_artifact art, osee_txs attrTxs, osee_attribute attr , osee_tx_details txd " + //
         "           where txs.branch_id = 570 and txs.gamma_id = art.gamma_id and art.art_type_id = " + artType.getIdString() + "  " + //
         "            and art.art_id = attr.art_id and attrTxs.branch_id = 570 and attrTxs.gamma_id = attr.gamma_id " + //
         "            and attrTxs.transaction_id = txd.transaction_id " + //
         "            AND attr.ATTR_TYPE_ID in (");

      // Add in () attr type id
      List<String> ids = new ArrayList<String>();
      for (AttributeTypeToken attrType : attrTypes) {
         ids.add(attrType.getIdString());
      }
      sb.append(Collections.toString(",", ids));

      sb.append(")) ");

      sb.append("SELECT attrs.art_id, ");

      // Change attr type id to attr type name for user reading convenience
      sb.append("case attrs.attr_type_id ");
      for (AttributeTypeToken attrType : attrTypes) {
         sb.append(String.format("when %s then '%s'", attrType.getIdString(), attrType.getUnqualifiedName()));
      }
      sb.append("END AS attr_type, ");

      sb.append(String.format(" value current_value, previous_value, author_name, max_time last_changed_date " + //
         "FROM attrs, users " + //
         "WHERE " + //
         "  attrs.max_time > to_date('%s  01:01:00','YYYY-MM-DD HH24:MI:SS') " + // changed since this date
         "    AND attrs.time = attrs.max_time AND attrs.author = users.art_id AND attrs.value != attrs.previous_value " + //
         "order by attrs.art_id, attrs.attr_id, time", date));

      String query = sb.toString();
      return query;
   }

}