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
package org.eclipse.osee.framework.core.translation;

import org.eclipse.osee.framework.core.data.PurgeBranchRequest;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Megumi Telles
 * @author Jeff C. Phillips
 */
public class PurgeBranchRequestTranslator implements ITranslator<PurgeBranchRequest> {
   private enum Entry {
      BRANCH_ID
  }

  @Override
  public PurgeBranchRequest convert(PropertyStore propertyStore) throws OseeCoreException {
      int branchId = propertyStore.getInt(Entry.BRANCH_ID.name());
      PurgeBranchRequest request = new PurgeBranchRequest(branchId);
      return request;
  }

  @Override
  public PropertyStore convert(PurgeBranchRequest data) throws OseeCoreException {
      PropertyStore store = new PropertyStore();
      store.put(Entry.BRANCH_ID.name(), data.getBranchId());
      return store;
  }
}
