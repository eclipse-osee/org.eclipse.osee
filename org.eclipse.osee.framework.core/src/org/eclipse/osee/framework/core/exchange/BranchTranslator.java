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
package org.eclipse.osee.framework.core.exchange;

import org.eclipse.osee.framework.core.IDataTranslationService;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.TransactionRecord;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Roberto E. Escobar
 */
public final class BranchTranslator implements IDataTranslator<Branch> {

   private enum Entry {
      BRANCH_GUID,
      BRANCH_NAME,
      BRANCH_ID,
      BRANCH_TYPE,
      BRANCH_STATE,
      BRANCH_ARCHIVED_STATE,
      PARENT_BRANCH,
      BASE_TRANSACTION,
      SOURCE_TRANSACTION;
   }

   private final IDataTranslationService service;

   public BranchTranslator(IDataTranslationService service) {
      this.service = service;
   }

   private IDataTranslationService getService() {
      return service;
   }

   public Branch convert(PropertyStore store) throws OseeCoreException {
      int branchId = store.getInt(Entry.BRANCH_ID.name());
      String branchGuid = store.get(Entry.BRANCH_GUID.name());
      String branchName = store.get(Entry.BRANCH_NAME.name());
      BranchType branchType = BranchType.valueOf(store.get(Entry.BRANCH_TYPE.name()));
      BranchState branchState = BranchState.valueOf(store.get(Entry.BRANCH_STATE.name()));
      BranchArchivedState branchArchived = BranchArchivedState.valueOf(store.get(Entry.BRANCH_ARCHIVED_STATE.name()));

      TransactionRecord baseTransaction =
            getService().convert(store.getPropertyStore(Entry.BASE_TRANSACTION.name()), TransactionRecord.class);

      Branch parentBranch = null;
      PropertyStore parentStore = store.getPropertyStore(Entry.PARENT_BRANCH.name());
      if (parentStore != null) {
         parentBranch = getService().convert(parentStore, Branch.class);
      }

      // TODO need a basicBranch object similar to basicArtifact - name, id, guid, parentId;
      return null;
   }

   public PropertyStore convert(Branch data) throws OseeCoreException {
      IDataTranslationService service = getService();

      PropertyStore store = new PropertyStore();

      store.put(Entry.BRANCH_ID.name(), data.getId());
      store.put(Entry.BRANCH_GUID.name(), data.getGuid());
      store.put(Entry.BRANCH_NAME.name(), data.getName());
      store.put(Entry.BRANCH_TYPE.name(), data.getBranchType().name());
      store.put(Entry.BRANCH_STATE.name(), data.getBranchState().name());
      store.put(Entry.BRANCH_ARCHIVED_STATE.name(), data.getArchiveState().name());

      TransactionRecord record = data.getBaseTransaction();
      if (record != null) {
         store.put(Entry.BASE_TRANSACTION.name(), getService().convert(record));
      }
      record = data.getSourceTransaction();
      if (record != null) {
         store.put(Entry.SOURCE_TRANSACTION.name(), getService().convert(record));
      }
      if (data.hasParentBranch()) {
         store.put(Entry.PARENT_BRANCH.name(), getService().convert(data.getParentBranch()));
      }
      return store;
   }
}
