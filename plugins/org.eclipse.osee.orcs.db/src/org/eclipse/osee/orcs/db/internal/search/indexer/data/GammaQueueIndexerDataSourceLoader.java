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
package org.eclipse.osee.orcs.db.internal.search.indexer.data;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.IndexedResource;
import org.eclipse.osee.orcs.core.ds.OrcsDataHandler;
import org.eclipse.osee.orcs.db.internal.search.indexer.IndexedResourceLoader;
import org.eclipse.osee.orcs.db.internal.search.indexer.IndexerConstants;

/**
 * @author Roberto E. Escobar
 */
public class GammaQueueIndexerDataSourceLoader implements IndexedResourceLoader {
   private static final String LOAD_ATTRIBUTE =
      "SELECT attr1.gamma_id, attr1.VALUE, attr1.uri, attr1.attr_type_id, attr1.attr_id FROM osee_attribute attr1, osee_tag_gamma_queue tgq1 WHERE attr1.gamma_id = tgq1.gamma_id AND tgq1.query_id = ?";

   private final Log logger;
   private final IOseeDatabaseService dbService;
   private final IdentityService idService;
   private final IResourceManager resourceManager;

   public GammaQueueIndexerDataSourceLoader(Log logger, IOseeDatabaseService dbService, IdentityService idService, IResourceManager resourceManager) {
      super();
      this.logger = logger;
      this.dbService = dbService;
      this.idService = idService;
      this.resourceManager = resourceManager;
   }

   private boolean loadData(OrcsDataHandler<IndexedResource> handler, int tagQueueQueryId) throws OseeCoreException {
      boolean loaded = false;
      IOseeStatement chStmt = dbService.getStatement();
      try {
         chStmt.runPreparedQuery(LOAD_ATTRIBUTE, tagQueueQueryId);
         while (chStmt.next()) {
            loaded = true;

            int itemId = chStmt.getInt("attr_id");
            long typeUuid = idService.getUniversalId(chStmt.getInt("attr_type_id"));
            long gammaId = chStmt.getLong("gamma_id");
            String uri = chStmt.getString("uri");
            String value = chStmt.getString("value");

            IndexedResource data = createData(itemId, typeUuid, gammaId, value, uri);
            handler.onData(data);
         }
      } finally {
         chStmt.close();
      }
      return loaded;
   }

   @Override
   public void loadSource(OrcsDataHandler<IndexedResource> handler, int tagQueueQueryId) throws OseeCoreException {
      boolean loadSuccess = loadData(handler, tagQueueQueryId);
      // Re-try in case query id hasn't been committed to the database
      int retry = 0;
      while (!loadSuccess && retry < IndexerConstants.INDEX_QUERY_ID_LOADER_TOTAL_RETRIES) {
         try {
            Thread.sleep(2000);
         } catch (InterruptedException ex) {
            // Do Nothing
         }
         logger.debug("Retrying attribute load from gammas - queryId[%s] attempt[%s of %s]", tagQueueQueryId, retry,
            IndexerConstants.INDEX_QUERY_ID_LOADER_TOTAL_RETRIES);
         loadSuccess = loadData(handler, tagQueueQueryId);
         retry++;
      }
   }

   private IndexedResource createData(int localId, long typeUuid, long gammaId, String value, String uri) {
      return new IndexerDataSourceImpl(resourceManager, localId, typeUuid, gammaId, value, uri);
   }
}
