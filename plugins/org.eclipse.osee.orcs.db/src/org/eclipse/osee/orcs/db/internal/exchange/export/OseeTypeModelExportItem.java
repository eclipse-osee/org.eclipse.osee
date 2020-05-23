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

package org.eclipse.osee.orcs.db.internal.exchange.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsTypes;
import org.eclipse.osee.orcs.db.internal.exchange.handler.ExportItem;

/**
 * @author Roberto E. Escobar
 */
public class OseeTypeModelExportItem extends AbstractExportItem {
   private final OrcsTypes orcsTypes;

   public OseeTypeModelExportItem(Log logger, OrcsTypes orcsTypes) {
      super(logger, ExportItem.EXPORT_TYPE_MODEL);
      this.orcsTypes = orcsTypes;
   }

   @Override
   protected void executeWork() throws Exception {
      OutputStream outputStream = null;
      try {
         File outputFile = new File(getWriteLocation(), getFileName());
         outputStream = new FileOutputStream(outputFile);
         orcsTypes.writeTypes(outputStream).call();
      } finally {
         Lib.close(outputStream);
      }
   }
}
