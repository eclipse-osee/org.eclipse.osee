/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.internal.store;

import java.util.Date;
import org.eclipse.osee.ats.api.config.IAtsCache;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.version.IVersionFactory;
import org.eclipse.osee.ats.core.client.internal.IAtsArtifactReader;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class VersionArtifactReader implements IAtsArtifactReader<IAtsVersion> {

   private final IVersionFactory versionFactory;

   public VersionArtifactReader(IVersionFactory versionFactory) {
      this.versionFactory = versionFactory;
   }

   @Override
   public IAtsVersion load(IAtsCache cache, Artifact artifact) throws OseeCoreException {
      IAtsVersion version = versionFactory.createVersion(artifact.getName(), artifact.getGuid(), artifact.getUuid());
      version.setStoreObject(artifact);
      cache.cacheAtsObject(version);

      version.setEstimatedReleasedDate(
         artifact.getSoleAttributeValue(AtsAttributeTypes.EstimatedReleaseDate, (Date) null));
      version.setReleasedDate(artifact.getSoleAttributeValue(AtsAttributeTypes.ReleaseDate, (Date) null));
      version.setReleased(artifact.getSoleAttributeValue(AtsAttributeTypes.Released, false));
      version.setLocked(artifact.getSoleAttributeValue(AtsAttributeTypes.VersionLocked, false));
      version.setNextVersion(artifact.getSoleAttributeValue(AtsAttributeTypes.NextVersion, false));
      version.setAllowCommitBranch(artifact.getSoleAttributeValue(AtsAttributeTypes.AllowCommitBranch, false));
      version.setAllowCreateBranch(artifact.getSoleAttributeValue(AtsAttributeTypes.AllowCreateBranch, false));
      version.setBaselineBranchUuid(artifact.getSoleAttributeValue(AtsAttributeTypes.BaselineBranchUuid, ""));
      version.setDescription(artifact.getSoleAttributeValue(AtsAttributeTypes.Description, ""));
      version.setFullName(artifact.getSoleAttributeValue(AtsAttributeTypes.FullName, ""));

      return version;
   }
}
