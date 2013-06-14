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

import java.util.Collection;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.db.internal.search.indexer.IndexerConstants;
import org.eclipse.osee.orcs.db.internal.search.indexer.QueueToAttributeLoader;

/**
 * @author Roberto E. Escobar
 */
public class QueueToAttributeLoaderImpl implements QueueToAttributeLoader {
   private static final String LOAD_ATTRIBUTE =
      "SELECT attr1.gamma_id, attr1.VALUE, attr1.uri, attr1.attr_type_id, attr1.attr_id FROM osee_attribute attr1, osee_tag_gamma_queue tgq1 WHERE attr1.gamma_id = tgq1.gamma_id AND tgq1.query_id = ?";

   private final Log logger;
   private final IOseeDatabaseService dbService;
   private final IdentityService idService;
   private final IResourceManager resourceManager;

   public QueueToAttributeLoaderImpl(Log logger, IOseeDatabaseService dbService, IdentityService idService, IResourceManager resourceManager) {
      super();
      this.logger = logger;
      this.dbService = dbService;
      this.idService = idService;
      this.resourceManager = resourceManager;
   }

   private IOseeDatabaseService getDatabaseService() {
      return dbService;
   }

   private IdentityService getIdentityService() {
      return idService;
   }

   private IResourceManager getResourceManager() {
      return resourceManager;
   }

   private void loadAttributeData(Collection<AttributeReadable<?>> attributeDatas, OseeConnection connection, int tagQueueQueryId) throws OseeCoreException {
      IOseeStatement chStmt = getDatabaseService().getStatement(connection);
      try {
         chStmt.runPreparedQuery(LOAD_ATTRIBUTE, tagQueueQueryId);
         IdentityService idService = getIdentityService();
         while (chStmt.next()) {
            int localId = chStmt.getInt("attr_type_id");

            Long uuid = idService.getUniversalId(localId);
            IAttributeType attributeType = TokenFactory.createAttributeType(uuid, "N/A");

            attributeDatas.add(new AttributeForIndexingImpl(getResourceManager(), chStmt.getInt("attr_id"),
               chStmt.getLong("gamma_id"), attributeType, chStmt.getString("value"), chStmt.getString("uri")));
         }
      } finally {
         chStmt.close();
      }
   }

   @Override
   public void loadAttributes(OseeConnection connection, int tagQueueQueryId, Collection<AttributeReadable<?>> attributeDatas) throws OseeCoreException {
      loadAttributeData(attributeDatas, connection, tagQueueQueryId);

      // Re-try in case query id hasn't been committed to the database
      int retry = 0;
      while (attributeDatas.isEmpty() && retry < IndexerConstants.INDEX_QUERY_ID_LOADER_TOTAL_RETRIES) {
         try {
            Thread.sleep(2000);
         } catch (InterruptedException ex) {
            // Do Nothing
         }
         logger.debug("Retrying attribute load from gammas - queryId[%s] attempt[%s]", tagQueueQueryId, retry);
         loadAttributeData(attributeDatas, connection, tagQueueQueryId);
         retry++;
      }
   }
}
