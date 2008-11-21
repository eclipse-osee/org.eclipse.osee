/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.transaction;

import org.eclipse.osee.framework.core.data.OseeSql;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;

/**
 * @author Jeff C. Phillips
 */
public class RelationTransactionData extends BaseTransactionData {
   private static final String INSERT_INTO_RELATION_TABLE =
         "INSERT INTO osee_relation_link (rel_link_id, rel_link_type_id, a_art_id, b_art_id, rationale, a_order, b_order, gamma_id, modification_id) VALUES (?,?,?,?,?,?,?,?,?)";

   private final RelationLink link;

   public RelationTransactionData(RelationLink link, int gammaId, TransactionId transactionId, ModificationType modificationType) {
      super(link.getRelationId(), gammaId, transactionId, modificationType);
      this.link = link;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.transaction.BaseTransactionData#getSelectTxNotCurrentSql()
    */
   @Override
   public String getSelectTxNotCurrentSql() {
      return OseeSql.Transaction.SELECT_PREVIOUS_TX_NOT_CURRENT_RELATIONS;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.transaction.BaseTransactionData#getInsertData()
    */
   @Override
   public Object[] getInsertData() {
      return new Object[] {link.getRelationId(), link.getRelationType().getRelationTypeId(), link.getAArtifactId(),
            link.getBArtifactId(), link.getRationale(), link.getAOrder(), link.getBOrder(), getGammaId(),
            getModificationType().getValue()};
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.transaction.BaseTransactionData#getInsertSql()
    */
   @Override
   public String getInsertSql() {
      return INSERT_INTO_RELATION_TABLE;
   }
}