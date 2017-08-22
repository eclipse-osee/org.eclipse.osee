/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.internal.state;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogFactory;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateFactory;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateManager;
import org.eclipse.osee.ats.api.workflow.state.IAtsWorkStateFactory;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class AtsStateFactory implements IAtsStateFactory {

   private final IAtsWorkStateFactory workStateFactory;
   private final IAtsLogFactory logFactory;
   private final IAtsServices services;
   Map<Id, IAtsStateManager> idToStateManager = new HashMap<>();
   Map<Id, TransactionId> idToTransactionId = new HashMap<>();

   public AtsStateFactory(IAtsServices services, IAtsWorkStateFactory workStateFactory, IAtsLogFactory logFactory) {
      this.services = services;
      this.workStateFactory = workStateFactory;
      this.logFactory = logFactory;
   }

   @Override
   public IAtsStateManager getStateManager(IAtsWorkItem workItem) {
      IAtsStateManager stateMgr = idToStateManager.get(workItem);
      TransactionId transId = idToTransactionId.get(workItem);
      TransactionId workItemTransaction = services.getStoreService().getTransactionId(workItem);
      if (stateMgr == null || (workItemTransaction.isValid() && workItemTransaction.notEqual(transId))) {
         stateMgr = new StateManager(workItem, logFactory, services);
         idToStateManager.put(workItem, stateMgr);
         idToTransactionId.put(workItem, services.getStoreService().getTransactionId(workItem));
         StateManagerStore.load(workItem, stateMgr, services.getAttributeResolver(), workStateFactory);
      }
      return stateMgr;
   }

   @Override
   public void writeToStore(IAtsUser asUser, IAtsWorkItem workItem, IAtsChangeSet changes) throws OseeCoreException {
      StateManagerStore.writeToStore(asUser, workItem, (StateManager) workItem.getStateMgr(),
         services.getAttributeResolver(), changes, workStateFactory);
   }

   @Override
   public void load(IAtsWorkItem workItem, IAtsStateManager stateMgr) {
      StateManagerStore.load(workItem, stateMgr, services.getAttributeResolver(), workStateFactory);
   }

   @Override
   public void clearStateManager(Id id) {
      idToStateManager.put(id, null);
   }

   @Override
   public void setStateMgr(IAtsWorkItem workItem, IAtsStateManager stateMgr) {
      idToStateManager.put(workItem, stateMgr);
      idToTransactionId.put(workItem, services.getStoreService().getTransactionId(workItem));
   }

}
