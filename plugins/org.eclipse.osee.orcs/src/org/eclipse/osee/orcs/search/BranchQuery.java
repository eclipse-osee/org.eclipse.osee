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
package org.eclipse.osee.orcs.search;

import java.util.Collection;
import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.ResultSet;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.data.BranchReadable;

/**
 * @author Ryan D. Brooks
 * @author Roberto E. Escobar
 */
public interface BranchQuery {

   BranchQuery includeDeleted();

   BranchQuery excludeDeleted();

   BranchQuery includeDeleted(boolean enabled);

   boolean areDeletedIncluded();

   BranchQuery includeArchived();

   BranchQuery includeArchived(boolean enabled);

   BranchQuery excludeArchived();

   boolean areArchivedIncluded();

   BranchQuery andLocalId(int... id) throws OseeCoreException;

   BranchQuery andLocalIds(Collection<Integer> ids) throws OseeCoreException;

   BranchQuery andUuids(String... ids) throws OseeCoreException;

   BranchQuery andUuids(Collection<String> ids) throws OseeCoreException;

   BranchQuery andIds(Collection<? extends IOseeBranch> ids) throws OseeCoreException;

   BranchQuery andIds(IOseeBranch... ids) throws OseeCoreException;

   BranchQuery andIsOfType(BranchType... branchType) throws OseeCoreException;

   BranchQuery andStateIs(BranchState... branchState) throws OseeCoreException;

   BranchQuery andNameEquals(String value) throws OseeCoreException;

   BranchQuery andNamePattern(String pattern) throws OseeCoreException;

   BranchQuery andIsChildOf(IOseeBranch branch) throws OseeCoreException;

   BranchQuery andIsAncestorOf(IOseeBranch branch) throws OseeCoreException;

   ResultSet<BranchReadable> getResults() throws OseeCoreException;

   ResultSet<IOseeBranch> getResultsAsId() throws OseeCoreException;

   int getCount() throws OseeCoreException;

   CancellableCallable<Integer> createCount() throws OseeCoreException;

   CancellableCallable<ResultSet<BranchReadable>> createSearch() throws OseeCoreException;

   CancellableCallable<ResultSet<IOseeBranch>> createSearchResultsAsIds() throws OseeCoreException;

}
