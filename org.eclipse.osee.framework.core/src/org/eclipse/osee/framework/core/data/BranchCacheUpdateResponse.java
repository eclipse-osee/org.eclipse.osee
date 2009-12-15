/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

import java.util.Collection;
import org.eclipse.osee.framework.core.cache.IOseeCache;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.util.BranchCacheUpdateUtil;

/**
 * @author Roberto E. Escobar
 */
public class BranchCacheUpdateResponse extends AbstractBranchCacheMessage {

   public BranchCacheUpdateResponse() {
      super();
   }

   public static BranchCacheUpdateResponse fromCache(IOseeCache<Branch> cache, Collection<Branch> types) throws OseeCoreException {
      BranchCacheUpdateResponse request = new BranchCacheUpdateResponse();
      BranchCacheUpdateUtil.loadFromCache(request, types);
      return request;
   }
}
