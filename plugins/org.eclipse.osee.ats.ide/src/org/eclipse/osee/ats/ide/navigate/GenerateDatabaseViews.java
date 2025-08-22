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
package org.eclipse.osee.ats.ide.navigate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;

/**
 * @author Donald G. Dunne
 */
public abstract class GenerateDatabaseViews extends XNavigateItemAction {

   protected boolean formatDates = false;

   public GenerateDatabaseViews(String name) {
      super(name, AtsImage.REPORT, XNavigateItem.UTILITY);
   }

   protected void createTableViewSql(String viewName, String attrPrefix, List<AttributeTypeToken> attrTypes,
      XResultData rd, String andQuery, ArtifactTypeToken... artType) {
      int attrTypeCount = attrTypes.size();
      List<String> colNames = new ArrayList<String>();
      // Must have to get the object art_id, so one more col than attrs specified
      colNames.add("uuid");
      for (AttributeTypeToken attrType : attrTypes) {
         colNames.add(getNoSpaceName(attrType, attrPrefix));
      }

      String preSql = getPreSql(viewName, colNames, andQuery, artType);
      rd.addRaw(preSql);

      int count = 1;
      for (AttributeTypeToken attrType : attrTypes) {
         boolean lastAttrType = count == attrTypeCount;
         String attrTypeSql = getAttrTypeSql(attrType, attrPrefix);
         if (lastAttrType) {
            attrTypeSql = attrTypeSql.replace(" ,", " ");
         }
         rd.addRaw(attrTypeSql);
         count++;
      }
   }

   private String getNoSpaceName(AttributeTypeToken attrType, String attrPrefix) {
      String safeName = attrPrefix + attrType.getName().toLowerCase().replaceAll("[\\. ]", "_");
      if (safeName.equals("name")) {
         safeName = safeName.replaceFirst("name", "title");
      }
      return safeName;
   }

   public String getAttrTypeSql(AttributeTypeToken attrType, String attrPrefix) {
      String dateFormat = "then value end)";
      if (isFormatDates() && attrType.isDate()) {
         dateFormat = "then TO_CHAR(( timestamp '1970-01-01 00:00:00.000 UTC' + " //
            + "numtodsinterval(nvl(value,0)/1000,'SECOND') ) at time zone 'MST','MM-DD-YYYY HH:MI:SS AM') end)";
      }
      return String.format("max(case when attr_type_id = %s %s over (partition by art_id) as %s ,\n",
         attrType.getIdString(), dateFormat, getNoSpaceName(attrType, attrPrefix));
   }

   /**
    * @param colNames names of column, NOTE: "name" is not a valid column name
    * @param artType to use to populate the table
    */
   protected String getPreSql(String viewName, Collection<String> colNames, String andQuery,
      ArtifactTypeToken... artTypes) {
      List<Long> ids = new ArrayList<Long>();
      for (ArtifactTypeToken artType : artTypes) {
         ids.add(artType.getId());
      }
      return String.format("create view %s (%s) as \n" + //
         "with prs_raw as ( \n" + //
         "select attr.art_id, attr.value, attr.attr_type_id \n" + //
         "from osee_txs txs, osee_artifact art, osee_txs attrTxs, osee_attribute attr \n" + //
         "where txs.branch_id = 570 and txs.tx_current = 1 and txs.gamma_id = art.gamma_id and \n" + //
         "art.art_type_id in ( %s )\n" + //
         "and attrTxs.branch_id = 570 and attrTxs.tx_current = 1 and attrTxs.gamma_id = attr.gamma_id \n" + //
         "and attr.art_id = art.art_id %s), \n" + //
         "prs_pretty as (select distinct art_id, \n", //
         viewName, //
         Collections.toString(",", colNames), //
         Collections.toString(",", ids), //
         andQuery);
   }

   public String getPostSql() {
      return "from prs_raw) \n" + //
         "select * from prs_pretty;";
   }

   protected List<AttributeTypeToken> getAttrTypesToInclude(Map<AttributeTypeToken, Boolean> attrTypesMap) {
      List<AttributeTypeToken> attrTypes = new ArrayList<AttributeTypeToken>();
      for (Entry<AttributeTypeToken, Boolean> entry : attrTypesMap.entrySet()) {
         if (entry.getValue()) {
            attrTypes.add(entry.getKey());
         } else {
            System.err.println("Skipping: " + entry.getKey().toStringWithId());
         }
      }
      return attrTypes;
   }

   public boolean isFormatDates() {
      return formatDates;
   }

   public void setFormatDates(boolean formatDates) {
      this.formatDates = formatDates;
   }

}
