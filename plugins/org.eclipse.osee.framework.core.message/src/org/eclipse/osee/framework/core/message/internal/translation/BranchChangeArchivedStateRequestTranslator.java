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
package org.eclipse.osee.framework.core.message.internal.translation;

import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.message.ChangeBranchArchiveStateRequest;
import org.eclipse.osee.framework.core.translation.ITranslator;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Megumi Telles
 */
public final class BranchChangeArchivedStateRequestTranslator implements ITranslator<ChangeBranchArchiveStateRequest> {

   private static enum Entry {
      BRANCH_ID,
      BRANCH_ARCHIVED_STATE,
   }

   public ChangeBranchArchiveStateRequest convert(PropertyStore propertyStore) throws OseeCoreException {
      int branchId = propertyStore.getInt(Entry.BRANCH_ID.name());
      BranchArchivedState state = BranchArchivedState.valueOf(propertyStore.get(Entry.BRANCH_ARCHIVED_STATE.name()));
      ChangeBranchArchiveStateRequest data = new ChangeBranchArchiveStateRequest(branchId, state);
      return data;
   }

   public PropertyStore convert(ChangeBranchArchiveStateRequest data) throws OseeCoreException {
      PropertyStore store = new PropertyStore();
      store.put(Entry.BRANCH_ID.name(), data.getBranchId());
      store.put(Entry.BRANCH_ARCHIVED_STATE.name(), data.getState().name());
      return store;
   }

}
