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
package org.eclipse.osee.orcs.core.internal.util;

import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.orcs.core.ds.OrcsData;

/**
 * @author Roberto E. Escobar
 */
public class ValueProviderFactory {

   private final BranchCache branchCache;

   public ValueProviderFactory(BranchCache branchCache) {
      super();
      this.branchCache = branchCache;
   }

   public ValueProvider<Branch, OrcsData> createBranchProvider(OrcsData data) {
      return new BranchProvider(branchCache, data);
   }

}