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
import org.eclipse.osee.framework.branch.management.exchange.handler.ExportItemId;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.io.xml.SaxTransformer;

/**
 * @author Ryan D. Brooks
 */
public class V0_9_0Transformer implements IOseeDbExportTransformer {

   @Override
   public String applyTransform(ImportController importController) throws OseeCoreException {

      SaxTransformer txsTransformer = new V0_9_0TxsTransformer();
      importController.transformExportItem(ExportItemId.OSEE_TXS_DATA, txsTransformer);
      return "0.9.0";
   }

   @Override
   public boolean isApplicable(String exportVersion) throws OseeCoreException {
      return exportVersion.startsWith("0.8.3");
   }
}
