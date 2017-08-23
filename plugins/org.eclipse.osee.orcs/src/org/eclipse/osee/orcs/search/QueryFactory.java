/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
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
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Ryan D. Brooks
 * @author Roberto E. Escobar
 */
public interface QueryFactory {

   QueryBuilder fromBranch(BranchId branch) throws OseeCoreException;

   QueryBuilder fromArtifactTypeAllBranches(IArtifactType artifactType) throws OseeCoreException;

   QueryBuilder fromArtifacts(Collection<? extends ArtifactReadable> artifacts) throws OseeCoreException;

   BranchQuery branchQuery();

   TransactionQuery transactionQuery();

   TupleQuery tupleQuery();

   ApplicabilityQuery applicabilityQuery();

}