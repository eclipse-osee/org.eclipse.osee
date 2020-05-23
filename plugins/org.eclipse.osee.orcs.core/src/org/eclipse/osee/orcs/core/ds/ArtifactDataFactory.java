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

import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;

/**
 * @author Roberto E. Escobar
 */
public interface ArtifactDataFactory {

   ArtifactData create(BranchId branch, ArtifactTypeToken artifactType, String guid);

   ArtifactData create(BranchId branch, ArtifactTypeToken artifactType, String guid, ApplicabilityId appId);

   ArtifactData create(BranchId branch, ArtifactTypeToken artifactType, String guid, long artifactId);

   ArtifactData create(BranchId branch, ArtifactTypeToken artifactType, String guid, long artifactId, ApplicabilityId appId);

   ArtifactData create(BranchId branch, ArtifactTypeToken artifactType, Long artifactId);

   ArtifactData create(BranchId branch, ArtifactTypeToken artifactType, Long artifactId, ApplicabilityId appId);

   ArtifactData copy(BranchId destination, ArtifactData source);

   ArtifactData clone(ArtifactData source);

   ArtifactData introduce(BranchId destination, ArtifactData source);
}