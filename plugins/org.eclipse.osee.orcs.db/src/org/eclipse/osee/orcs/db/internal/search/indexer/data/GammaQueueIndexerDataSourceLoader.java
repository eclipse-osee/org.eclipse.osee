/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.orcs.db.internal.search.indexer.data;

import java.util.Collection;
import java.util.HashSet;
import java.util.function.Consumer;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.IndexedResource;
import org.eclipse.osee.orcs.core.ds.OrcsDataHandler;
import org.eclipse.osee.orcs.db.internal.search.indexer.IndexedResourceLoader;
import org.eclipse.osee.orcs.db.internal.search.indexer.IndexerConstants;
import org.eclipse.osee.orcs.db.internal.util.AttributeDataUtil;

/**
 * @author Roberto E. Escobar
 */
public class GammaQueueIndexerDataSourceLoader implements IndexedResourceLoader {
   private static final String LOAD_ATTRIBUTE =
      "SELECT attr1.gamma_id, attr1.VALUE, attr1.uri, attr1.attr_type_id, attr1.attr_id FROM osee_attribute attr1, osee_tag_gamma_queue tgq1 WHERE attr1.gamma_id = tgq1.gamma_id AND tgq1.query_id = ?";

   private final Log logger;
   private final JdbcClient jdbcClient;
   private final IResourceManager resourceManager;

   public GammaQueueIndexerDataSourceLoader(Log logger, JdbcClient jdbcClient, IResourceManager resourceManager) {
      super();
      this.logger = logger;
      this.jdbcClient = jdbcClient;
      this.resourceManager = resourceManager;
   }

   private int loadData(OrcsDataHandler<IndexedResource> handler, int tagQueueQueryId, OrcsTokenService tokenService) {
      Collection<AttributeData> attrData = new HashSet<>();
      Consumer<JdbcStatement> consumer = stmt -> {
         AttributeId itemId = AttributeId.valueOf(stmt.getLong("attr_id"));
         AttributeTypeToken attributeType = tokenService.getAttributeTypeOrCreate(stmt.getLong("attr_type_id"));
         GammaId gammaId = GammaId.valueOf(stmt.getLong("gamma_id"));
         String uri = stmt.getString("uri");
         String value = stmt.getString("value");
         attrData.add(new AttributeData(itemId, attributeType, gammaId, uri, value));
      };

      int loaded = jdbcClient.runQuery(consumer, LOAD_ATTRIBUTE, tagQueueQueryId);

      for (AttributeData attributeData : attrData) {
         StringBuffer sb = new StringBuffer();
         sb = sb.append(attributeData.getValue());
         sb = sb.append(AttributeDataUtil.getNameByGuid(attributeData.getValue(), jdbcClient));
         IndexedResource data = new IndexerDataSourceImpl(resourceManager, attributeData.getItemId(),
            attributeData.getAttributeType(), attributeData.getGammaId(), sb.toString(), attributeData.getUri());
         handler.onData(data);
      }
      return loaded;
   }

   @Override
   public void loadSource(OrcsDataHandler<IndexedResource> handler, int tagQueueQueryId, OrcsTokenService tokenService) {
      int count = loadData(handler, tagQueueQueryId, tokenService);
      // Re-try in case query id hasn't been committed to the database
      int retry = 0;
      while (count == 0 && retry < IndexerConstants.INDEX_QUERY_ID_LOADER_TOTAL_RETRIES) {
         try {
            Thread.sleep(2000);
         } catch (InterruptedException ex) {
            // Do Nothing
         }
         logger.debug("Retrying attribute load from gammas - queryId[%s] attempt[%s of %s]", tagQueueQueryId, retry,
            IndexerConstants.INDEX_QUERY_ID_LOADER_TOTAL_RETRIES);
         loadData(handler, tagQueueQueryId, tokenService);
         retry++;
      }
   }

   private class AttributeData {

      private final AttributeId itemId;
      private final AttributeTypeToken attributeType;
      private final GammaId gammaId;
      private final String uri;
      private final String value;

      public AttributeData(AttributeId itemId, AttributeTypeToken attributeType, GammaId gammaId, String uri, String value) {
         this.itemId = itemId;
         this.attributeType = attributeType;
         this.gammaId = gammaId;
         this.uri = uri;
         this.value = value;
      }

      public AttributeId getItemId() {
         return itemId;
      }

      public AttributeTypeToken getAttributeType() {
         return attributeType;
      }

      public GammaId getGammaId() {
         return gammaId;
      }

      public String getUri() {
         return uri;
      }

      public String getValue() {
         return value;
      }

   }
}
