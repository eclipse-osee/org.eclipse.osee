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

package org.eclipse.osee.orcs.search;

import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;

/**
 * @author Ryan D. Brooks
 * @author Roberto E. Escobar
 */
public interface QueryFactory {

   QueryBuilder fromBranch(BranchId branch);

   QueryBuilder fromBranch(BranchId branch, ArtifactId view);

   QueryBuilder fromBranch(BranchId branch, ApplicabilityId appId);

   BranchQuery branchQuery();

   TransactionQuery transactionQuery();

   TupleQuery tupleQuery();

   ApplicabilityQuery applicabilityQuery();

}