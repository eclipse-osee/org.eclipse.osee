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

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;

/**
 * @author Roberto E. Escobar
 */
public interface RelationDataFactory {

   RelationData createRelationData(RelationTypeToken relationType, BranchId branch, ArtifactId aArtifact,
      ArtifactId bArtifact, String rationale);

   RelationData createRelationData(RelationTypeToken relationType, BranchId branch, ArtifactId aArtifact,
      ArtifactId bArtifact, ArtifactId relArtifact, int order);

   RelationData clone(RelationData source);

   RelationData introduce(BranchId branch, RelationData source);
}
