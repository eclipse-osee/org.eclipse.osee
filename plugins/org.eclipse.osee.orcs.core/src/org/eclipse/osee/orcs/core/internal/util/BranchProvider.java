/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.util;

import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.OrcsData;

/**
 * @author Roberto E. Escobar
 */
public class BranchProvider extends OrcsLazyObject<Branch, OrcsData>implements ValueProvider<Branch, OrcsData> {

   private final BranchCache branchCache;

   public BranchProvider(BranchCache branchCache, OrcsData data) {
      super(data);
      this.branchCache = branchCache;
   }

   @Override
   protected Branch instance() throws OseeCoreException {
      return branchCache.getByUuid(getOrcsData().getVersion().getBranchId());
   }
}