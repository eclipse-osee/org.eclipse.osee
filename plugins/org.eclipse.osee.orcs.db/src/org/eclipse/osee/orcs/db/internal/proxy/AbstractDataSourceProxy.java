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
package org.eclipse.osee.orcs.db.internal.proxy;

import org.eclipse.osee.orcs.core.ds.DataProxy;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractDataSourceProxy implements DataProxy {
   private final Storage dataStore;

   public AbstractDataSourceProxy(Storage dataStore) {
      super();
      this.dataStore = dataStore;
   }

   protected Storage getDataStore() {
      return dataStore;
   }
}