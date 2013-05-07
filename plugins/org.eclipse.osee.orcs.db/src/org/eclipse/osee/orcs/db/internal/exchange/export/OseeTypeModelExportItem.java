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
package org.eclipse.osee.orcs.db.internal.exchange.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.db.internal.exchange.handler.ExportItem;
import org.eclipse.osee.orcs.db.internal.types.IOseeModelingService;

/**
 * @author Roberto E. Escobar
 */
public class OseeTypeModelExportItem extends AbstractExportItem {
   private final IOseeModelingService modelingService;

   public OseeTypeModelExportItem(Log logger, IOseeModelingService modelingService) {
      super(logger, ExportItem.EXPORT_TYPE_MODEL);
      this.modelingService = modelingService;
   }

   @Override
   protected void executeWork() throws Exception {
      File outputFile = new File(getWriteLocation(), getFileName());
      OutputStream outputStream = null;
      try {
         outputStream = new FileOutputStream(outputFile);
         modelingService.exportOseeTypes(outputStream);
      } finally {
         Lib.close(outputStream);
      }
   }
}
