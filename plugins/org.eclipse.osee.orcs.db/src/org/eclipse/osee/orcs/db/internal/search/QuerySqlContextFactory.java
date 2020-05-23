/*********************************************************************
 * Copyright (c) 2013 Boeing
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
import org.eclipse.osee.orcs.QueryType;
import org.eclipse.osee.orcs.core.ds.QueryData;

/**
 * @author Roberto E. Escobar
 */
public interface QuerySqlContextFactory {
   QuerySqlContext createQueryContext(OrcsSession session, QueryData queryData, QueryType queryType);
}