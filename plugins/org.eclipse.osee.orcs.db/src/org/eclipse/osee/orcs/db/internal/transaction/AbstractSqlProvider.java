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
package org.eclipse.osee.orcs.db.internal.transaction;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.OrcsData;
import org.eclipse.osee.orcs.db.internal.loader.IdFactory;

public abstract class AbstractSqlProvider {

   private final IdFactory idFactory;

   protected AbstractSqlProvider(IdFactory idFactory) {
      super();
      this.idFactory = idFactory;
   }

   long getGammaId(OrcsData data) throws OseeCoreException {
      return idFactory.getNextGammaId(data);
   }
}
