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
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.database.core.OseeSql;
import org.eclipse.osee.framework.database.core.SequenceManager;
import org.eclipse.osee.framework.skynet.core.event.ArtifactTransactionModifiedEvent;
import org.eclipse.osee.framework.skynet.core.event.RelationModifiedEvent;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.transaction.BaseTransactionData;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

/**
 * @author Jeff C. Phillips
 * @author Roberto E. Escobar
 */
public class RelationTransactionData extends BaseTransactionData {
   private static final String INSERT_INTO_RELATION_TABLE =
         "INSERT INTO osee_relation_link (rel_link_id, rel_link_type_id, a_art_id, b_art_id, rationale, gamma_id) VALUES (?,?,?,?,?,?)";

   private final RelationLink relation;

   public RelationTransactionData(RelationLink relation, ModificationType modificationType) throws OseeDataStoreException {
      super(relation.getRelationId(), modificationType);
      this.relation = relation;
   }

   @Override
   public OseeSql getSelectTxNotCurrentSql() {
      return OseeSql.TX_GET_PREVIOUS_TX_NOT_CURRENT_RELATIONS;
   }

   @Override
   protected void addInsertToBatch(SkynetTransaction transaction) throws OseeCoreException {
      super.addInsertToBatch(transaction);
      if (!useExistingBackingData()) {
         internalAddInsertToBatch(transaction, 4, INSERT_INTO_RELATION_TABLE, relation.getRelationId(),
               relation.getRelationType().getId(), relation.getAArtifactId(), relation.getBArtifactId(),
               relation.getRationale(), getGammaId());
      }
   }

   @Override
   protected void internalUpdate(TransactionRecord transactionId) throws OseeCoreException {
      relation.internalSetGammaId(getGammaId());
   }

   @Override
   protected void internalClearDirtyState() {
      relation.setNotDirty();
   }

   @Override
   protected void internalOnRollBack() throws OseeCoreException {
   }

   @Override
   protected int createGammaId() throws OseeCoreException {
      int newGammaId = 0;
      if (useExistingBackingData()) {
         newGammaId = relation.getGammaId();
      } else {
         newGammaId = SequenceManager.getNextGammaId();
      }
      return newGammaId;
   }

   @Override
   protected void internalAddToEvents(Collection<ArtifactTransactionModifiedEvent> events) throws OseeCoreException {
      RelationEventType relationEventType =
            getModificationType().isDeleted() ? RelationEventType.Deleted : RelationEventType.Added;
      events.add(new RelationModifiedEvent(new Sender(this.getClass().getName()), relationEventType, relation,
            relation.getBranch(), relation.getRelationType().getName()));
   }
}