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
import org.eclipse.osee.framework.database.core.AbstractJoinQuery;
import org.eclipse.osee.orcs.core.ds.DataStoreContext;

/**
 * @author Roberto E. Escobar
 */
public interface SqlContext extends DataStoreContext {

   String getSql();

   void setSql(String sql);

   List<Object> getParameters();

   List<AbstractJoinQuery> getJoins();

   void clear();

}