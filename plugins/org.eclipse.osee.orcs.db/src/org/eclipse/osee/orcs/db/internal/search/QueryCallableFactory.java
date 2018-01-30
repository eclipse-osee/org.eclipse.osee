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

import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.LoadDataHandler;
import org.eclipse.osee.orcs.core.ds.QueryData;

/**
 * @author Roberto E. Escobar
 */
public interface QueryCallableFactory {

   CancellableCallable<Integer> createCount(OrcsSession session, QueryData queryData);

   CancellableCallable<Integer> createQuery(OrcsSession session, QueryData queryData, LoadDataHandler handler);

   QuerySqlContextFactory getSqlContextFactory();
}