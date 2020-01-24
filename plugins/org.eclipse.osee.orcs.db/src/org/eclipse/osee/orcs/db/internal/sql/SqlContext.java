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
package org.eclipse.osee.orcs.db.internal.sql;

import java.util.List;
import org.eclipse.osee.jdbc.JdbcConstants;
import org.eclipse.osee.orcs.core.ds.DataStoreContext;
import org.eclipse.osee.orcs.core.ds.ResultObjectDescription;
import org.eclipse.osee.orcs.db.internal.sql.join.AbstractJoinQuery;

/**
 * @author Roberto E. Escobar
 */
public interface SqlContext extends DataStoreContext {

   String getSql();

   void setSql(String sql);

   List<Object> getParameters();

   List<AbstractJoinQuery> getJoins();

   ResultObjectDescription getObjectDescription();

   void setObjectDescription(ResultObjectDescription data);

   void clear();

   /**
    * Gives the JDBC driver a hint as to the number of rows that should be fetched from the database when more rows are
    * needed. If the value specified is zero, then the hint is ignored. The default implementation returns
    * {@link org.eclipse.osee.jdbc.JdbcConstants#JDBC__MAX_FETCH_SIZE}
    */
   default int getFetchSize() {
      return JdbcConstants.JDBC__MAX_FETCH_SIZE;
   }
}