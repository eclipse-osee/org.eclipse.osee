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
package org.eclipse.osee.orcs.db.internal.loader.processor;

import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.core.ds.OrcsDataHandler;
import org.eclipse.osee.orcs.db.internal.loader.data.OrcsDataFactory;

/**
 * @author Roberto E. Escobar
 */
public abstract class LoadProcessor<D, F extends OrcsDataFactory> extends AbstractLoadProcessor<OrcsDataHandler<D>> {

   private final F factory;

   public LoadProcessor(F factory) {
      this.factory = factory;
   }

   @Override
   protected final void onRow(OrcsDataHandler<D> handler, JdbcStatement chStmt, Options options, Object conditions) {
      D data = createData(conditions, factory, chStmt, options);
      if (data != null) {
         handler.onData(data);
      }
   }

   protected abstract D createData(Object conditions, F factory, JdbcStatement chStmt, Options options);

}
