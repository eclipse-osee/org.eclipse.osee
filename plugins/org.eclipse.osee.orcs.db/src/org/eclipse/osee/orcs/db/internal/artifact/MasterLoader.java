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
package org.eclipse.osee.orcs.db.internal.artifact;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.ArtifactRow;
import org.eclipse.osee.orcs.core.ds.ArtifactRowHandler;
import org.eclipse.osee.orcs.core.ds.Confirmer;
import org.eclipse.osee.orcs.core.ds.LoadType;
import org.eclipse.osee.orcs.db.internal.attribute.AttributeLoader;
import org.eclipse.osee.orcs.db.internal.attribute.LoadOptions;

public class MasterLoader {

   private final ArtifactLoader artifactLoader;
   private final AttributeLoader attributeLoader;

   //   private final RelationLoader relationLoader;
   public MasterLoader(ArtifactLoader artifactLoader, AttributeLoader attributeLoader) {
      super();
      this.artifactLoader = artifactLoader;
      this.attributeLoader = attributeLoader;
   }

   private void loadArtifacts(Confirmer confirmer, LoadType reload, LoadOptions options) throws OseeCoreException {

      final Collection<ArtifactRow> rows = new ArrayList<ArtifactRow>();

      int fetchSize = -1;
      int queryId = -1;

      ArtifactRowHandler handler = new ArtifactRowHandler() {

         @Override
         public void onRow(ArtifactRow row) throws OseeCoreException {
            rows.add(row);
         }
      };

      artifactLoader.loadFromQueryId(handler, reload, options, fetchSize, queryId);

      if (confirmer == null || confirmer.canProceed(rows.size())) {
         if (reload == LoadType.RELOAD_CACHE) {
            //         for (Artifact artifact : artifacts) {
            //            artifact.prepareForReload();
            //         }
         }

         LoadLevel loadLevel = options.getLoadLevel();
         if (!loadLevel.isShallow()) {
            if (!loadLevel.isRelationsOnly()) {
               //               AttributeRowMapper mapper = new AttributeRowMapper();
               //               attributeLoader.loadAttributeData(mapper, options, queryId);
            }

            if (!loadLevel.isAttributesOnly()) {
               //         relationLoader.loadRelationData(handler, options, queryId);
            }
         }
      }
   }

   //   private static void checkArtifactCache(ArrayList<Artifact> artifacts, Collection<Integer> artIds, TransactionRecord transactionId, IOseeBranch branch, DeletionFlag allowDeleted) throws OseeCoreException {
   //      Iterator<Integer> iterator = artIds.iterator();
   //      while (iterator.hasNext()) {
   //         Integer artId = iterator.next();
   //         Artifact artifact = getArtifactFromCache(artId, transactionId, branch);
   //
   //         if (artifact != null) {
   //            if (allowDeleted == EXCLUDE_DELETED && artifact.isDeleted()) {
   //               continue;
   //            }
   //            iterator.remove();
   //            artifacts.add(artifact);
   //         }
   //      }
   //   }
   //
   //   private static Artifact getArtifactFromCache(Integer artId, TransactionRecord transactionId, IOseeBranch branch) throws OseeCoreException {
   //      boolean historical = transactionId != null;
   //      if (historical) {
   //         return ArtifactCache.getHistorical(artId, transactionId.getId());
   //      }
   //      return ArtifactCache.getActive(artId, branch);
   //   }
}
