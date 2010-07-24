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

import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.message.ChangeBranchStateRequest;
import org.eclipse.osee.framework.core.translation.ITranslator;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Megumi Telles
 */
public final class BranchChangeStateRequestTranslator implements ITranslator<ChangeBranchStateRequest> {

   private static enum Entry {
      BRANCH_ID,
      BRANCH_STATE,
   }

   public ChangeBranchStateRequest convert(PropertyStore propertyStore) throws OseeCoreException {
      int branchId = propertyStore.getInt(Entry.BRANCH_ID.name());
      BranchState state = BranchState.valueOf(propertyStore.get(Entry.BRANCH_STATE.name()));
      ChangeBranchStateRequest data = new ChangeBranchStateRequest(branchId, state);
      return data;
   }

   public PropertyStore convert(ChangeBranchStateRequest data) throws OseeCoreException {
      PropertyStore store = new PropertyStore();
      store.put(Entry.BRANCH_ID.name(), data.getBranchId());
      store.put(Entry.BRANCH_STATE.name(), data.getState().name());
      return store;
   }

}
