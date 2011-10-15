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
package org.eclipse.osee.framework.branch.management.exchange.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.branch.management.exchange.handler.ExportItem;
import org.eclipse.osee.framework.core.services.IOseeModelingService;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Roberto E. Escobar
 */
public class OseeTypeModelExportItem extends AbstractExportItem {
   private final IOseeModelingService modelingService;

   public OseeTypeModelExportItem(IOseeModelingService modelingService) {
      super(ExportItem.EXPORT_TYPE_MODEL);
      this.modelingService = modelingService;
   }

   @Override
   protected void executeWork() throws Exception {
      File outputFile = new File(getWriteLocation(), getFileName());
      OutputStream outputStream = null;
      try {
         outputStream = new FileOutputStream(outputFile);
         modelingService.exportOseeTypes(new NullProgressMonitor(), outputStream);
      } finally {
         Lib.close(outputStream);
      }
   }
}
