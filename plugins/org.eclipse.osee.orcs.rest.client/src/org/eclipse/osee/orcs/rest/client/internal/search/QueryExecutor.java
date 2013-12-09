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
package org.eclipse.osee.orcs.rest.client.internal.search;

import java.util.List;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.rest.model.search.Predicate;
import org.eclipse.osee.orcs.rest.model.search.RequestType;
import org.eclipse.osee.orcs.rest.model.search.SearchResult;

/**
 * @author John Misinco
 */
public interface QueryExecutor {

   int getCount(IOseeBranch branch, List<Predicate> predicates, QueryOptions options) throws OseeCoreException;

   SearchResult getResults(RequestType request, IOseeBranch branch, List<Predicate> predicates, QueryOptions options) throws OseeCoreException;
}