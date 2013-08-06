/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.search;

import org.eclipse.osee.executor.admin.HasCancellation;
import org.eclipse.osee.orcs.core.ds.LoadDataHandler;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.db.internal.search.util.ArtifactDataCountHandler;

/**
 * @author Roberto E. Escobar
 */
public interface QueryFilterFactory {

   boolean isFilterRequired(QueryData queryData);

   ArtifactDataCountHandler createHandler(HasCancellation cancellation, QueryData queryData, QuerySqlContext queryContext, LoadDataHandler handler) throws Exception;

}