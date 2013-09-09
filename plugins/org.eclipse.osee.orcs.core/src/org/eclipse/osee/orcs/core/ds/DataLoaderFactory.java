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
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.OrcsSession;

/**
 * @author Roberto E. Escobar
 */
public interface DataLoaderFactory {

   int getCount(HasCancellation cancellation, QueryContext queryContext) throws OseeCoreException;

   DataLoader fromQueryContext(QueryContext queryContext) throws OseeCoreException;

   DataLoader fromBranchAndArtifactIds(OrcsSession session, IOseeBranch branch, Collection<Integer> artifactIds) throws OseeCoreException;

   DataLoader fromBranchAndArtifactIds(OrcsSession session, IOseeBranch branch, int... artifactIds) throws OseeCoreException;

   DataLoader fromBranchAndIds(OrcsSession session, IOseeBranch branch, Collection<String> ids) throws OseeCoreException;

   DataLoader fromBranchAndIds(OrcsSession session, IOseeBranch branch, String... ids) throws OseeCoreException;

}
