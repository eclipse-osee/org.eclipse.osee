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
import org.eclipse.osee.orcs.core.ds.LoadType;
import org.eclipse.osee.orcs.core.internal.SessionContext;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactRowMapper implements ArtifactRowHandler {

   private final SessionContext context;
   private final Log logger;
   private final BranchCache branchCache;
   private final ArtifactTypeCache typeCache;

   public ArtifactRowMapper(Log logger, SessionContext context, BranchCache branchCache, ArtifactTypeCache typeCache) {
      this.logger = logger;
      this.context = context;
      this.branchCache = branchCache;
      this.typeCache = typeCache;
   }

   private Artifact getContainer(ArtifactRow current) {
      Artifact container = null;
      if (current.isHistorical()) {
         container = context.getHistorical(current.getArtifactId(), current.getStripeId());
      } else {
         container = context.getActive(current.getArtifactId(), current.getBranchId());
      }
      if (container == null) {
         logger.warn("Orphaned attribute detected - [%s]", current);
      }
      return container;
   }

   @Override
   public void onRow(ArtifactRow row) throws OseeCoreException {
   }

   public void onComplete() {

   }

   private Artifact retrieveShallowArtifact(ArtifactRow row, LoadType reload) throws OseeCoreException {
      Artifact artifact = getContainer(row);
      if (artifact == null) {
         IArtifactType artifactType = typeCache.getByGuid(row.getArtTypeUuid());
         ArtifactFactory factory = null;
         //         ArtifactTypeManager.getFactory(artifactType);
         Branch branch = branchCache.getById(row.getBranchId());
         //         artifact =
         //            factory.loadExisitingArtifact(row.getArtifactId(), row.getGuid(), row.getHumanReadableId(), artifactType,
         //               row.getGammaId(), branch, row.getTransactionId(), row.getModType(), row.isHistorical());
      } else if (reload == LoadType.RELOAD_CACHE) {
         artifact.internalSetPersistenceData(row.getGammaId(), row.getTransactionId(), row.getModType(),
            row.isHistorical());
      }
      return artifact;
   }
}
