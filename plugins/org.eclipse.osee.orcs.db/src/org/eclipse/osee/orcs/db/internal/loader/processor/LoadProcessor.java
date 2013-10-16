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

import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.core.ds.OrcsDataHandler;
import org.eclipse.osee.orcs.data.HasLocalId;
import org.eclipse.osee.orcs.db.internal.loader.data.OrcsDataFactory;

/**
 * @author Roberto E. Escobar
 */
public abstract class LoadProcessor<D extends HasLocalId, F extends OrcsDataFactory> {

   private final F factory;

   public LoadProcessor(F factory) {
      this.factory = factory;
   }

   public final int processResultSet(OrcsDataHandler<D> handler, IOseeStatement chStmt, Options options) throws OseeCoreException {
      int rowCount = 0;
      Object conditions = createPreConditions();
      while (chStmt.next()) {
         rowCount++;
         D data = createData(conditions, factory, chStmt, options);
         if (data != null) {
            handler.onData(data);
         }
      }
      return rowCount;
   }

   protected Object createPreConditions() {
      return null;
   }

   protected abstract D createData(Object conditions, F factory, IOseeStatement chStmt, Options options) throws OseeCoreException;

}
