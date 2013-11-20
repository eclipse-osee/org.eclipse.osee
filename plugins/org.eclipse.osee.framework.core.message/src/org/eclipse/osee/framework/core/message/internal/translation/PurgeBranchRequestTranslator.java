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

import org.eclipse.osee.framework.core.message.PurgeBranchRequest;
import org.eclipse.osee.framework.core.translation.ITranslator;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Megumi Telles
 * @author Jeff C. Phillips
 */
public class PurgeBranchRequestTranslator implements ITranslator<PurgeBranchRequest> {
   private static enum Entry {
      BRANCH_ID,
      RECURSIVE
   }

   @Override
   public PurgeBranchRequest convert(PropertyStore propertyStore) {
      long branchId = propertyStore.getLong(Entry.BRANCH_ID.name());
      boolean recursive = propertyStore.getBoolean(Entry.RECURSIVE.name());
      PurgeBranchRequest request = new PurgeBranchRequest(branchId, recursive);
      return request;
   }

   @Override
   public PropertyStore convert(PurgeBranchRequest data) {
      PropertyStore store = new PropertyStore();
      store.put(Entry.BRANCH_ID.name(), data.getBranchId());
      store.put(Entry.RECURSIVE.name(), data.isRecursive());
      return store;
   }
}
