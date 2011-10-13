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
import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.data.ReadableArtifact;

/**
 * @author Ryan D. Brooks
 * @author Roberto E. Escobar
 */
public interface QueryFactory {

   QueryBuilder fromBranch(IOseeBranch branch) throws OseeCoreException;

   QueryBuilder fromArtifactType(IOseeBranch branch, IArtifactType artifactType) throws OseeCoreException;

   QueryBuilder fromArtifactTypes(IOseeBranch branch, Collection<? extends IArtifactType> artifactTypes) throws OseeCoreException;

   QueryBuilder fromArtifactTypeAllBranches(IArtifactType artifactType) throws OseeCoreException;

   QueryBuilder fromUuids(IOseeBranch branch, Collection<Integer> artifactIds) throws OseeCoreException;

   QueryBuilder fromGuidOrHrid(IOseeBranch branch, String guidOrHrid) throws OseeCoreException;

   QueryBuilder fromGuidOrHrids(IOseeBranch branch, Collection<String> guidOrHrids) throws OseeCoreException;

   QueryBuilder fromArtifact(IOseeBranch branch, IArtifactToken artifactToken) throws OseeCoreException;

   QueryBuilder fromArtifacts(Collection<? extends ReadableArtifact> artifacts) throws OseeCoreException;

   QueryBuilder fromName(IOseeBranch branch, String artifactName) throws OseeCoreException;

   QueryBuilder fromArtifactTypeAndName(IOseeBranch branch, IArtifactType artifactType, String artifactName) throws OseeCoreException;
}
