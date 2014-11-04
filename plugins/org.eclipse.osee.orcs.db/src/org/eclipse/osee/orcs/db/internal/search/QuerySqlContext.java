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
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.core.ds.QueryContext;
import org.eclipse.osee.orcs.db.internal.sql.SqlContextImpl;

/**
 * @author Roberto E. Escobar
 */
public class QuerySqlContext extends SqlContextImpl implements QueryContext {

   public static enum ObjectQueryType {
      ARTIFACT,
      BRANCH,
      TX,
      DYNAMIC_OBJECT,
      UNKNOWN;
   }

   private final ObjectQueryType objectType;

   public QuerySqlContext(OrcsSession session, Options options, ObjectQueryType objectType) {
      super(session, options);
      this.objectType = objectType;
   }

   public ObjectQueryType getOrcsObjectType() {
      return objectType;
   }
}
