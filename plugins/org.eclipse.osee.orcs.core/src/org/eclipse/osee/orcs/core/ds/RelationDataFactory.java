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

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeId;

/**
 * @author Roberto E. Escobar
 */
public interface RelationDataFactory {

   RelationData createRelationData(RelationTypeId relationType, BranchId branch, ArtifactId aArtifact, ArtifactId bArtifact, String rationale);

   RelationData clone(RelationData source);

   RelationData introduce(BranchId branch, RelationData source);
}
