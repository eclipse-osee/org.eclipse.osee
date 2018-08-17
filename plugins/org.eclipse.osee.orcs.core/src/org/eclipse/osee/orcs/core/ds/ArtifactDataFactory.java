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

import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;

/**
 * @author Roberto E. Escobar
 */
public interface ArtifactDataFactory {

   ArtifactData create(BranchId branch, IArtifactType artifactType, String guid);

   ArtifactData create(BranchId branch, IArtifactType artifactType, String guid, long artifactId);

   ArtifactData create(BranchId branch, IArtifactType artifactType, Long artifactId);

   ArtifactData copy(BranchId destination, ArtifactData source);

   ArtifactData clone(ArtifactData source);

   ArtifactData introduce(BranchId destination, ArtifactData source);
}