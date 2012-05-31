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
package org.eclipse.osee.orcs.core.internal.artifact;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.cache.ArtifactTypeCache;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.ArtifactRowHandler;
import org.eclipse.osee.orcs.core.internal.SessionContext;
import org.eclipse.osee.orcs.core.internal.VersionImpl;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactCollector.LoadSourceType;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.Version;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactRowMapper implements ArtifactRowHandler {

   private final SessionContext context;
   private final BranchCache branchCache;
   private final ArtifactTypeCache typeCache;
   private final ArtifactFactory artifactFactory;
   private final ArtifactCollector artifactReceiver;

   public ArtifactRowMapper(SessionContext context, BranchCache branchCache, ArtifactTypeCache typeCache, ArtifactFactory artifactFactory, ArtifactCollector artifactReciever) {
      this.context = context;
      this.branchCache = branchCache;
      this.typeCache = typeCache;
      this.artifactFactory = artifactFactory;
      this.artifactReceiver = artifactReciever;
   }

   @Override
   public void onRow(ArtifactData row) throws OseeCoreException {
      LoadSourceType loadSourceType = LoadSourceType.FOUND_IN_CACHE;
      ArtifactReadable artifact = getLoadedArtifact(row);
      if (artifact == null) {
         loadSourceType = LoadSourceType.WAS_CREATED;
         ArtifactType artifactType = typeCache.getByGuid(row.getArtTypeUuid());
         Branch branch = branchCache.getById(row.getBranchId());

         Version version =
            new VersionImpl(row.getGammaId(), row.getArtifactId(), row.getModType(), row.getTransactionId(),
               row.isHistorical());

         artifact =
            artifactFactory.createReadableArtifact(row.getGuid(), row.getHumanReadableId(), artifactType, branch,
               version);
      }
      artifactReceiver.onArtifact(artifact, loadSourceType);
   }

   private ArtifactReadable getLoadedArtifact(ArtifactData current) {
      ArtifactReadable container = null;
      if (current.isHistorical()) {
         container = context.getHistorical(current.getArtifactId(), current.getStripeId());
      } else {
         container = context.getActive(current.getArtifactId(), current.getBranchId());
      }
      return container;
   }
}
