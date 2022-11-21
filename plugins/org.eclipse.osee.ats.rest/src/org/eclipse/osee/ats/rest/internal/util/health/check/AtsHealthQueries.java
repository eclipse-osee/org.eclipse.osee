/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.ats.rest.internal.util.health.check;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;

/**
 * @author Donald G. Dunne
 */
public class AtsHealthQueries {

   public static String getMultipleArtEntriesonCommon(AtsApi atsApi) {
      return "SELECT DISTINCT art1.art_id FROM osee_artifact art1, osee_artifact art2, \n" + //
         "osee_txs txs WHERE art1.ART_ID = art2.ART_ID AND \n" + //
         "art1.art_type_id = art2.art_type_id AND art1.GAMMA_ID <> art2.GAMMA_ID AND \n" + //
         "art2.GAMMA_ID = txs.GAMMA_ID and txs.BRANCH_ID = 570 ORDER BY art1.art_id";
   }

   public static String getArtIdsOfMuiltipleRelsOnSide(AtsApi atsApi, BranchToken branch, RelationTypeSide singleRelTypeSide) {
      String sideName = singleRelTypeSide.getSide().isSideA() ? "a_art_id" : "b_art_id";
      return String.format("select %s as art_id from " //
         + "(SELECT count(*) the_count, %s FROM OSEE_RELATION_LINK lin, OSEE_TXS txs " //
         + "WHERE txs.BRANCH_ID = %s " //
         + "AND lin.REL_LINK_TYPE_ID = %s " //
         + "AND txs.GAMMA_ID = lin.GAMMA_ID AND txs.TX_CURRENT = 1 GROUP BY %s ) " //
         + "where the_count > 1", sideName, sideName, atsApi.getAtsBranch().getId(),
         singleRelTypeSide.getRelationType().getIdString(), sideName);
   }

   public static String getWorkItemsInCurrentStateType(AtsApi atsApi, StateType... stateTypes) {
      String stateTypeStr = "";
      if (stateTypes.length == 3) {
         stateTypeStr = "like '%'";
      } else {
         stateTypeStr = " in (";
         for (StateType sType : stateTypes) {
            stateTypeStr += "'" + sType.name() + "', ";
         }
         stateTypeStr = stateTypeStr.replaceFirst(", ", ")");
      }
      String query =
         String.format("SELECT distinct art.art_id FROM osee_artifact art, osee_txs txs, OSEE_ATTRIBUTE attr " //
            + "WHERE attr.gamma_id = txs.gamma_id AND txs.tx_current = 1 AND txs.branch_id = %s and " //
            + "attr.ART_ID = art.ART_ID and attr.ATTR_TYPE_ID = %s and attr.VALUE %s",
            atsApi.getAtsBranch().getIdString(), AtsAttributeTypes.CurrentStateType.getIdString(), stateTypeStr);
      return query;
   }

   public static String getInWorkWorkItemsWorkDefId(AtsApi atsApi) {
      String query = "SELECT art.art_id, attr.VALUE FROM osee_artifact art, osee_txs txs, OSEE_ATTRIBUTE attr\n" + //
         "WHERE txs.branch_id = 570 AND attr.gamma_id = txs.gamma_id AND txs.tx_current = 1 AND \n" + //
         "attr.ART_ID = art.ART_ID and attr.ATTR_TYPE_ID = 53049621055799825 and art.ART_ID IN (\n" + //
         "   SELECT DISTINCT art.art_id FROM osee_artifact art, osee_txs txs, OSEE_ATTRIBUTE attr\n" + //
         "   WHERE txs.branch_id = 570 AND attr.gamma_id = txs.gamma_id AND txs.tx_current = 1 AND \n" + //
         "   attr.ART_ID = art.ART_ID and attr.ATTR_TYPE_ID = 1152921504606847147 and attr.VALUE = 'Working')";
      return query;

   }

}
