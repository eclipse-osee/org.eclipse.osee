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
import javax.xml.stream.XMLStreamException;
import org.eclipse.osee.framework.branch.management.exchange.ImportController;
import org.eclipse.osee.framework.branch.management.exchange.handler.ExportItem;
import org.eclipse.osee.framework.branch.management.internal.Activator;
import org.eclipse.osee.framework.core.cache.AbstractOseeCache;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.jdk.core.util.io.xml.SaxTransformer;

/**
 * @author Ryan D. Brooks
 */
public class V0_9_0Transformer implements IOseeDbExportTransformer {

   @Override
   public String applyTransform(ImportController importController) throws Exception {
      IOseeCachingService service = Activator.getInstance().getOseeCachingService();
      try {
         replaceDataTypeIdsWithGuids(importController, service.getArtifactTypeCache(), ExportItem.OSEE_ARTIFACT_DATA,
               "art_type_id", "name");
         replaceDataTypeIdsWithGuids(importController, service.getAttributeTypeCache(), ExportItem.OSEE_ATTRIBUTE_DATA,
               "attr_type_id", "name");
         replaceDataTypeIdsWithGuids(importController, service.getRelationTypeCache(),
               ExportItem.OSEE_RELATION_LINK_DATA, "rel_link_type_id", "type_name");

         V0_9_0TxDetailsHandler txdHandler = new V0_9_0TxDetailsHandler();
         importController.parseExportItem(ExportItem.OSEE_TX_DETAILS_DATA, txdHandler);

         SaxTransformer txsTransformer = new V0_9_0TxsTransformer(txdHandler.getBranchIdMap());
         importController.transformExportItem(ExportItem.OSEE_TXS_DATA, txsTransformer);
         txsTransformer.finish();
      } catch (XMLStreamException ex) {
         throw new OseeWrappedException(ex);
      }

      importController.deleteExportItem("osee.branch.definitions.xml");
      importController.transformExportItem(ExportItem.EXPORT_MANIFEST, new V0_9_0_ManifestRule());

      return "0.9.0";
   }

   private void replaceDataTypeIdsWithGuids(ImportController importController, AbstractOseeCache<?> cache, ExportItem exportItem, String typeIdColumn, String typeNameColumn) throws Exception {
      V0_9_0TypeHandler typeHandler = new V0_9_0TypeHandler(cache, typeIdColumn, typeNameColumn);
      importController.parseExportItem(exportItem + ".type.xml", typeHandler);
      HashMap<Integer, String> typeIdMap = typeHandler.getTypeIdMap();
      SaxTransformer typeTransformer = new V0_9_0ItemTransformer(typeIdMap, typeIdColumn);
      importController.transformExportItem(exportItem, typeTransformer);
      typeTransformer.finish();
   }

   @Override
   public boolean isApplicable(String exportVersion) throws OseeCoreException {
      return exportVersion.startsWith("0.8.3");
   }

   @Override
   public void finalizeTransform(ImportController importController) throws Exception {
   }
}
