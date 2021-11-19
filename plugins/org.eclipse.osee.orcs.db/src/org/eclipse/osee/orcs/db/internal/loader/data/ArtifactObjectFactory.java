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

package org.eclipse.osee.orcs.db.internal.loader.data;

import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.VersionData;

/**
 * @author Roberto E. Escobar
 */
public interface ArtifactObjectFactory extends VersionObjectFactory {
   ArtifactData createArtifactData(VersionData version, ArtifactId artifactId, Long artifactType, ModificationType modType, String guidToSet, ApplicabilityId applicId);

   ArtifactData createArtifactData(VersionData version, ArtifactId artifactId, ArtifactTypeToken artifactType, ModificationType modType, String guidToSet, ApplicabilityId applicId);

   ArtifactData createCopy(ArtifactData source);
}