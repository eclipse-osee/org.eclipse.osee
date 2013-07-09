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
package org.eclipse.osee.orcs.db.internal.loader;

import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.DataPostProcessor;
import org.eclipse.osee.orcs.core.ds.LoadContext;
import org.eclipse.osee.orcs.core.ds.LoadOptions;
import org.eclipse.osee.orcs.db.internal.sql.SqlContextImpl;

/**
 * @author Roberto E. Escobar
 */
public class LoadSqlContext extends SqlContextImpl<LoadOptions, DataPostProcessor<?>> implements LoadContext {

   public LoadSqlContext(OrcsSession session, LoadOptions options) {
      super(session, options);
   }

}
