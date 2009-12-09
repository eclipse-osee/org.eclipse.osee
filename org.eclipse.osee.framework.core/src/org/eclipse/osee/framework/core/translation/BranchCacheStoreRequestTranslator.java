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

import org.eclipse.osee.framework.core.data.BranchCacheStoreRequest;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.BranchCacheUpdateUtil;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Roberto E. Escobar
 * @author Jeff C. Phillips
 */
public class BranchCacheStoreRequestTranslator implements ITranslator<BranchCacheStoreRequest> {

   private enum Field {
      IS_SERVER_MESSAGE;
   }

   @Override
   public BranchCacheStoreRequest convert(PropertyStore store) throws OseeCoreException {
      BranchCacheStoreRequest request = new BranchCacheStoreRequest();
      BranchCacheUpdateUtil.loadMessage(request, store);
      request.setServerUpdateMessage(store.getBoolean(Field.IS_SERVER_MESSAGE.name()));
      return request;
   }

   @Override
   public PropertyStore convert(BranchCacheStoreRequest object) throws OseeCoreException {
      PropertyStore store = new PropertyStore();
      BranchCacheUpdateUtil.loadStore(store, object);
      store.put(Field.IS_SERVER_MESSAGE.name(), object.isServerUpdateMessage());
      return store;
   }
}
