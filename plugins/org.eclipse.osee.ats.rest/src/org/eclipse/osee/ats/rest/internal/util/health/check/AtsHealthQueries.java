/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.util.health.check;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.RelationTypeSide;

/**
 * @author Donald G. Dunne
 */
public class AtsHealthQueries {

   public static String getArtIdsOfMuiltipleRelsOnSide(AtsApi atsApi, IOseeBranch branch, RelationTypeSide singleRelTypeSide) {
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

}
