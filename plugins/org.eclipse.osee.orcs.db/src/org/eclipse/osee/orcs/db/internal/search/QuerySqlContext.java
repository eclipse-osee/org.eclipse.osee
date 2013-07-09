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
package org.eclipse.osee.orcs.db.internal.search;

import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.QueryContext;
import org.eclipse.osee.orcs.core.ds.QueryOptions;
import org.eclipse.osee.orcs.core.ds.QueryPostProcessor;
import org.eclipse.osee.orcs.db.internal.sql.SqlContextImpl;

/**
 * @author Roberto E. Escobar
 */
public class QuerySqlContext extends SqlContextImpl<QueryOptions, QueryPostProcessor> implements QueryContext {

   public QuerySqlContext(OrcsSession session, QueryOptions options) {
      super(session, options);
   }

}
