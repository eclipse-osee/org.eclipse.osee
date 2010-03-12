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

import org.eclipse.osee.framework.branch.management.exchange.ImportController;
import org.eclipse.osee.framework.branch.management.exchange.handler.ExportItem;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Ryan D. Brooks
 */
public class V0_8_3Transformer implements IOseeDbExportTransformer {

   @Override
   public String applyTransform(ImportController importController) throws OseeCoreException {
      importController.transformExportItem(ExportItem.EXPORT_DB_SCHEMA, new V0_8_3_DbSchemaRule());
      importController.transformExportItem(ExportItem.OSEE_BRANCH_DATA, new V0_8_3_BranchRule());
      return "0.8.3";
   }

   @Override
   public boolean isApplicable(String exportVersion) throws OseeCoreException {
      return exportVersion == null;
   }

   @Override
   public void finalizeTransform(ImportController importController) throws Exception {
   }
}
