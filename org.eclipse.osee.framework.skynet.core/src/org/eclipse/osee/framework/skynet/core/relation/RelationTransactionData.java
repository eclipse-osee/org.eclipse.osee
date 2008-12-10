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
package org.eclipse.osee.framework.skynet.core.relation;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.OseeSql;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.db.connection.core.SequenceManager;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.skynet.core.event.ArtifactTransactionModifiedEvent;
import org.eclipse.osee.framework.skynet.core.event.RelationModifiedEvent;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.transaction.BaseTransactionData;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;

/**
 * @author Jeff C. Phillips
 * @author Roberto E. Escobar
 */
public class RelationTransactionData extends BaseTransactionData {
   private static final String INSERT_INTO_RELATION_TABLE =
         "INSERT INTO osee_relation_link (rel_link_id, rel_link_type_id, a_art_id, b_art_id, rationale, a_order, b_order, gamma_id, modification_id) VALUES (?,?,?,?,?,?,?,?,?)";

   private final RelationLink relation;

   public RelationTransactionData(RelationLink relation, ModificationType modificationType) throws OseeDataStoreException {
      super(relation.getRelationId(), modificationType);
      this.relation = relation;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.transaction.BaseTransactionData#getSelectTxNotCurrentSql()
    */
   @Override
   public String getSelectTxNotCurrentSql() {
      return OseeSql.Transaction.SELECT_PREVIOUS_TX_NOT_CURRENT_RELATIONS;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.transaction.BaseTransactionData#addInsertToBatch(org.eclipse.osee.framework.jdk.core.type.HashCollection)
    */
   @Override
   protected void addInsertToBatch(SkynetTransaction transaction) throws OseeCoreException {
      super.addInsertToBatch(transaction);
      if (getModificationType() != ModificationType.ARTIFACT_DELETED) {
         internalAddInsertToBatch(transaction, 4, INSERT_INTO_RELATION_TABLE, relation.getRelationId(),
               relation.getRelationType().getRelationTypeId(), relation.getAArtifactId(), relation.getBArtifactId(),
               relation.getRationale(), relation.getAOrder(), relation.getBOrder(), getGammaId(),
               getModificationType().getValue());
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.transaction.BaseTransactionData#internalUpdate()
    */
   @Override
   protected void internalUpdate(TransactionId transactionId) throws OseeCoreException {
      relation.internalSetGammaId(getGammaId());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.transaction.BaseTransactionData#internalClearDirtyState()
    */
   @Override
   protected void internalClearDirtyState() {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.transaction.BaseTransactionData#internalOnRollBack()
    */
   @Override
   protected void internalOnRollBack() throws OseeCoreException {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.transaction.BaseTransactionData#createGammaId()
    */
   @Override
   protected int createGammaId() throws OseeCoreException {
      int newGammaId = 0;
      if (getModificationType() == ModificationType.ARTIFACT_DELETED) {
         newGammaId = relation.getGammaId();
      } else {
         newGammaId = SequenceManager.getNextGammaId();
      }
      return newGammaId;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.transaction.BaseTransactionData#internalUpdateEvents(org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction)
    */
   @Override
   protected void internalAddToEvents(Collection<ArtifactTransactionModifiedEvent> events) throws OseeCoreException {
      RelationModType relationModType =
            getModificationType().isDeleted() ? RelationModType.Deleted : RelationModType.Added;
      events.add(new RelationModifiedEvent(new Sender(this.getClass().getName()), relationModType, relation,
            relation.getBranch(), relation.getRelationType().getTypeName()));
   }
}