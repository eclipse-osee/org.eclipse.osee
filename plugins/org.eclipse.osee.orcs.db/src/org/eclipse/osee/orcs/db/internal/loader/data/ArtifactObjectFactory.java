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
package org.eclipse.osee.orcs.db.internal.loader.data;

import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.VersionData;

/**
 * @author Roberto E. Escobar
 */
public interface ArtifactObjectFactory extends VersionObjectFactory {

   ArtifactData createArtifactData(VersionData version, Integer generateArtId, long artifactType, ModificationType modType, String guidToSet, ApplicabilityId applicId);

   ArtifactData createArtifactData(VersionData version, Integer generateArtId, ArtifactTypeToken artifactType, ModificationType modType, String guidToSet, ApplicabilityId applicId);

   ArtifactData createCopy(ArtifactData source);
}