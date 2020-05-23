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

package org.eclipse.osee.orcs.rest.client.internal.search;

import java.util.List;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.orcs.rest.model.search.artifact.Predicate;
import org.eclipse.osee.orcs.rest.model.search.artifact.RequestType;
import org.eclipse.osee.orcs.rest.model.search.artifact.SearchResult;

/**
 * @author John Misinco
 */
public interface QueryExecutor {

   int getCount(BranchId branch, List<Predicate> predicates, QueryOptions options);

   SearchResult getResults(RequestType request, BranchId branch, List<Predicate> predicates, QueryOptions options);
}