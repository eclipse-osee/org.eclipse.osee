/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

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
