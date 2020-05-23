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

package org.eclipse.osee.orcs.db.internal.sql;

import java.util.List;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.QueryData;

/**
 * @author Roberto E. Escobar
 */
public interface SqlHandlerFactory {

   List<SqlHandler<?>> createHandlers(QueryData queryData);

   SqlHandler<?> createHandler(Criteria criteria);
}