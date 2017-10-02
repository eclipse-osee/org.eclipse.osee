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
package org.eclipse.osee.orcs.core.ds;

import java.util.Collection;
import org.eclipse.osee.executor.admin.HasCancellation;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.orcs.OrcsSession;

/**
 * @author Roberto E. Escobar
 */
public interface DataLoaderFactory {

   int getCount(HasCancellation cancellation, QueryContext queryContext) ;

   DataLoader newDataLoader(QueryContext queryContext) ;

   DataLoader newDataLoader(OrcsSession session, BranchId branch, Collection<ArtifactId> artifactIds);

   DataLoader newDataLoaderFromIds(OrcsSession session, BranchId branch, Collection<Integer> artifactIds) ;

   DataLoader newDataLoaderFromGuids(OrcsSession session, BranchId branch, String... artifactGuids) ;

   DataLoader newDataLoaderFromGuids(OrcsSession session, BranchId branch, Collection<String> artifactGuids) ;

}