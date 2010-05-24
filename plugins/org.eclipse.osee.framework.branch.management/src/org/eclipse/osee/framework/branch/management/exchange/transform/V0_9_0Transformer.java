/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.branch.management.exchange.transform;

import java.util.HashMap;
import org.eclipse.osee.framework.branch.management.exchange.handler.ExportItem;
import org.eclipse.osee.framework.core.cache.AbstractOseeCache;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.jdk.core.util.io.xml.SaxTransformer;

/**
 * @author Ryan D. Brooks
 */
public class V0_9_0Transformer implements IOseeExchangeVersionTransformer {

   private final IOseeCachingService cachingService;

   public V0_9_0Transformer(IOseeCachingService cachingService) {
      this.cachingService = cachingService;
   }

   @Override
   public String applyTransform(ExchangeDataProcessor processor) throws OseeCoreException {
      try {
         replaceDataTypeIdsWithGuids(processor, cachingService.getArtifactTypeCache(), ExportItem.OSEE_ARTIFACT_DATA,
               "art_type_id", "name");
         replaceDataTypeIdsWithGuids(processor, cachingService.getAttributeTypeCache(), ExportItem.OSEE_ATTRIBUTE_DATA,
               "attr_type_id", "name");
         replaceDataTypeIdsWithGuids(processor, cachingService.getRelationTypeCache(),
               ExportItem.OSEE_RELATION_LINK_DATA, "rel_link_type_id", "type_name");

         V0_9_0TxDetailsHandler txdHandler = new V0_9_0TxDetailsHandler();
         processor.parse(ExportItem.OSEE_TX_DETAILS_DATA, txdHandler);

         SaxTransformer txsTransformer = new V0_9_0TxsTransformer(txdHandler.getBranchIdMap());
         processor.transform(ExportItem.OSEE_TXS_DATA, txsTransformer);
         txsTransformer.finish();
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      }

      processor.deleteExportItem("osee.branch.definitions.xml");
      processor.transform(ExportItem.EXPORT_MANIFEST, new V0_9_0_ManifestRule());

      return "0.9.0";
   }

   private void replaceDataTypeIdsWithGuids(ExchangeDataProcessor processor, AbstractOseeCache<?> cache, ExportItem exportItem, String typeIdColumn, String typeNameColumn) throws Exception {
      V0_9_0TypeHandler typeHandler = new V0_9_0TypeHandler(cache, typeIdColumn, typeNameColumn);
      processor.parse(exportItem + ".type.xml", typeHandler);
      HashMap<Integer, String> typeIdMap = typeHandler.getTypeIdMap();
      SaxTransformer typeTransformer = new V0_9_0ItemTransformer(typeIdMap, typeIdColumn);
      processor.parse(exportItem, typeTransformer);
      typeTransformer.finish();
   }

   @Override
   public boolean isApplicable(String exportVersion) throws OseeCoreException {
      return exportVersion.startsWith("0.8.3");
   }

   @Override
   public void finalizeTransform(ExchangeDataProcessor processor) throws Exception {
   }
}
