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

import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.cache.ArtifactTypeCache;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.ArtifactRow;
import org.eclipse.osee.orcs.core.ds.ArtifactRowHandler;
import org.eclipse.osee.orcs.core.internal.SessionContext;
import org.eclipse.osee.orcs.data.ReadableArtifact;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactRowMapper implements ArtifactRowHandler {

   private final SessionContext context;
   private final Log logger;
   private final BranchCache branchCache;
   private final ArtifactTypeCache typeCache;
   private final ArtifactFactory artifactFactory;
   private final ArtifactReciever artifactReceiver;

   public ArtifactRowMapper(Log logger, SessionContext context, BranchCache branchCache, ArtifactTypeCache typeCache, ArtifactFactory artifactFactory, ArtifactReciever artifactReciever) {
      this.logger = logger;
      this.context = context;
      this.branchCache = branchCache;
      this.typeCache = typeCache;
      this.artifactFactory = artifactFactory;
      this.artifactReceiver = artifactReciever;
   }

   @Override
   public void onRow(ArtifactRow row) throws OseeCoreException {
      boolean isArtifactAlreadyLoaded = true;
      ReadableArtifact artifact = getLoadedArtifact(row);
      if (artifact == null) {
         isArtifactAlreadyLoaded = false;
         IArtifactType artifactType = typeCache.getByGuid(row.getArtTypeUuid());
         Branch branch = branchCache.getById(row.getBranchId());
         artifact =
            artifactFactory.loadExisitingArtifact(row.getArtifactId(), row.getGuid(), row.getHumanReadableId(),
               artifactType, row.getGammaId(), branch, row.getTransactionId(), row.getModType(), row.isHistorical());
      }
      artifactReceiver.onArtifact(artifact, isArtifactAlreadyLoaded);
   }

   private ReadableArtifact getLoadedArtifact(ArtifactRow current) {
      ReadableArtifact container = null;
      if (current.isHistorical()) {
         container = context.getHistorical(current.getArtifactId(), current.getStripeId());
      } else {
         container = context.getActive(current.getArtifactId(), current.getBranchId());
      }
      return container;
   }
}
