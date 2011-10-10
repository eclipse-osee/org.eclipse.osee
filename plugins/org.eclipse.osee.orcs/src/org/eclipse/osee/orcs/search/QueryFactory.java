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
import java.util.List;
import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.orcs.data.ReadableArtifact;

/**
 * @author Ryan D. Brooks
 * @author Roberto E. Escobar
 */
public interface QueryFactory {

   QueryBuilder fromBranch(IOseeBranch branch);

   QueryBuilder fromArtifactType(IOseeBranch branch, IArtifactType artifactType);

   QueryBuilder fromArtifactTypes(IOseeBranch branch, Collection<? extends IArtifactType> artifactTypes);

   QueryBuilder fromArtifactTypeAllBranches(IArtifactType artifactType);

   QueryBuilder fromUuids(IOseeBranch branch, Collection<Integer> artifactIds);

   QueryBuilder fromGuidOrHrid(IOseeBranch branch, String guidOrHrid);

   QueryBuilder fromGuidOrHrids(IOseeBranch branch, List<String> guidOrHrids);

   QueryBuilder fromArtifact(IOseeBranch branch, IArtifactToken artifactToken);

   QueryBuilder fromArtifacts(Collection<? extends ReadableArtifact> artifacts);

   QueryBuilder fromName(IOseeBranch branch, String artifactName);

   QueryBuilder fromArtifactTypeAndName(IOseeBranch branch, IArtifactType artifactType, String artifactName);
}
