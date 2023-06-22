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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.db.internal.exchange.ExportImportXml;
import org.eclipse.osee.orcs.db.internal.exchange.ExportTableConstants;
import org.eclipse.osee.orcs.db.internal.exchange.handler.ExportItem;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractXmlExportItem extends AbstractExportItem {

   public AbstractXmlExportItem(Log logger, ExportItem id) {
      super(logger, id);
   }

   @Override
   public final void executeWork() throws Exception {
      checkForCancelled();
      Writer writer = null;
      try {

         File indexFile = new File(getWriteLocation(), getFileName());
         writer = new BufferedWriter(
            new OutputStreamWriter(new FileOutputStream(indexFile), ExportTableConstants.ENCODING), getBufferSize());
         writer.write(ExportTableConstants.XML_HEADER);

         ExportImportXml.openXmlNode(writer, ExportTableConstants.DATA);
         try {
            checkForCancelled();
            doWork(writer);
         } finally {
            ExportImportXml.closeXmlNode(writer, ExportTableConstants.DATA);
            writer.close();
         }
         writer.close();

      } catch (Exception e) {

         if (writer != null) {
            writer.close();
         }

      }

   }

   protected abstract void doWork(Appendable appendable) throws Exception;

}
