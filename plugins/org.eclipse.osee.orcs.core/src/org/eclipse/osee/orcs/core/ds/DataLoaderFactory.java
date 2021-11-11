/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.orcs.core.ds;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.executor.HasCancellation;
import org.eclipse.osee.orcs.OrcsSession;

/**
 * @author Roberto E. Escobar
 */
public interface DataLoaderFactory {

   int getCount(HasCancellation cancellation, QueryContext queryContext);

   DataLoader newDataLoader(QueryContext queryContext);

   DataLoader newDataLoader(OrcsSession session, BranchId branch, Collection<ArtifactId> artifactIds);

   DataLoader newDataLoaderFromIds(OrcsSession session, BranchId branch, Collection<Integer> artifactIds);

}